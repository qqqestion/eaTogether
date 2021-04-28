package ru.blackbull.eatogether.ui.main.map

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.ScrollView
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.logo.Alignment
import com.yandex.mapkit.logo.HorizontalAlignment
import com.yandex.mapkit.logo.VerticalAlignment
import com.yandex.mapkit.map.*
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.uri.UriObjectMetadata
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.runtime.image.ImageProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.other.Constants.FASTEST_LOCATION_INTERVAL
import ru.blackbull.eatogether.other.Constants.LOCATION_UPDATE_INTERVAL
import ru.blackbull.eatogether.other.Constants.REQUEST_CODE_LOCATION_PERMISSION
import ru.blackbull.eatogether.other.Constants.SEARCH_TIME_DELAY
import ru.blackbull.eatogether.other.EventObserver
import ru.blackbull.eatogether.other.LocationUtility
import ru.blackbull.eatogether.ui.main.snackbar
import timber.log.Timber
import javax.inject.Inject
import com.yandex.mapkit.map.Map as YandexMap


@AndroidEntryPoint
class MapFragment : Fragment(R.layout.fragment_map) , EasyPermissions.PermissionCallbacks ,
    CameraListener , MapObjectTapListener {

    private lateinit var userLocationLayer: UserLocationLayer
    private lateinit var mapView: MapView

    private val viewModel: MapViewModel by viewModels()

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ScrollView>

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    var isFirstLocation: Boolean = true

    private val place = "kfc"

    private lateinit var localController: NavController

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        val localNavHost =
            childFragmentManager.findFragmentById(R.id.childNavFragment) as NavHostFragment
        localController = localNavHost.navController

        Timber.d("onViewCreated")
        isFirstLocation = true
        mapView = yandexMapView
        // TODO: поработать с разрешениями. Приложение падает при первом запуске на устройстве
        requestPermission()
        subscribeToObservers()

        setupFusedLocationProviderClient()
        mapView.map?.addCameraListener(this)
        mapView.map.logo.setAlignment(Alignment(HorizontalAlignment.RIGHT , VerticalAlignment.TOP))
        val mapKit = MapKitFactory.getInstance()
        userLocationLayer = mapKit.createUserLocationLayer(mapView.mapWindow)
        userLocationLayer.isVisible = true
        userLocationLayer.isHeadingEnabled = true

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        var job: Job? = null
        etMapSearchPlaces.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_TIME_DELAY)
                editable?.let {
                    submitQuery(it.toString())
                }
            }
        }
        fab.setOnClickListener {
            Snackbar.make(
                requireView() ,
                "Clicked" ,
                Snackbar.LENGTH_LONG
            ).show()
            submitQuery(place)
        }

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                    isEnabled = false
                    requireActivity().onBackPressed()
                } else {
                    /**
                     * .popBackStack возвращает true, если был произведен переход, иначе false, но при имении только одного фрагмента в backstack
                     * .popBackStack его удаляет и делает невозможным дальнейшее использование (был баг с открытием нижнего меню два раза и
                     * IllegalStateException при повторном использовании)
                     * MapFragment -> select item on map -> *opens bottom sheet menu* -> press back
                     * -> select item on map again -> click create party -> IllegalStateException
                     */
                    if (localNavHost.navController.currentDestination?.id == R.id.placeDetailFragment) {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    } else {
                        localNavHost.navController.popBackStack()
                    }
                }
            }
        })

        submitQuery(place)
        etMapSearchPlaces.setText(place)
    }

    private fun subscribeToObservers() {
        viewModel.searchResult.observe(viewLifecycleOwner , EventObserver(
            onError = {
                snackbar(it)
            }
        ) { results ->
            val mapObjects: MapObjectCollection = mapView.map.mapObjects
            mapObjects.clear()
            for (searchResult in results) {
                val obj = searchResult.obj!!
                val resultLocation = obj.geometry[0].point
                if (resultLocation != null) {
                    Timber.d("Place ${obj.name} with address ${resultLocation.latitude}, ${resultLocation.longitude}")
                    val placemark = mapObjects.addPlacemark(
                        resultLocation ,
                        ImageProvider.fromResource(
                            requireContext() ,
                            R.drawable.search_result
                        )
                    )
                    val uri =
                        obj.metadataContainer.getItem(UriObjectMetadata::class.java).uris.firstOrNull()?.value
                    Timber.d("Put uri: $uri")
                    placemark.userData = uri
                    placemark.addTapListener(this)
                }
            }
        })
    }

    private fun submitQuery(query: String) {
        val mapObjects: MapObjectCollection = mapView.map.mapObjects
        mapObjects.clear()
        if (query.isEmpty()) {
            return
        }
        viewModel.search(query , VisibleRegionUtils.toPolygon(mapView.map.visibleRegion))
    }

    override fun onMapObjectTap(mapObject: MapObject , point: Point): Boolean {
        val uri = mapObject.userData as String
//        findNavController().navigate(
//            MapFragmentDirections.actionMapFragmentToPlaceDetailFragment(
//                uri
//            )
//        )
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        Timber.d("Get uri: $uri")
//        navHostChildFragment.findNavController()
        return true
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            result?.lastLocation?.let { location ->
                if (isFirstLocation) {
                    val latLng = LatLng(location.latitude , location.longitude)
                    mapView.map.move(
                        CameraPosition(
                            Point(latLng.latitude , latLng.longitude) ,
                            13.0f ,
                            0.0f ,
                            0.0f
                        )
                    )
                }
                isFirstLocation = false
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun setupFusedLocationProviderClient() {
        if (!LocationUtility.hasLocationPermission(requireContext())) {
            return
        }
        val request = LocationRequest.create().apply {
            interval = LOCATION_UPDATE_INTERVAL
            fastestInterval = FASTEST_LOCATION_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        fusedLocationProviderClient.requestLocationUpdates(
            request ,
            locationCallback ,
            Looper.getMainLooper()
        )
    }

    override fun onCameraPositionChanged(
        map: YandexMap ,
        cameraPosition: CameraPosition ,
        cameroUpdateReason: CameraUpdateReason ,
        finished: Boolean
    ) {
        if (finished) {
            submitQuery(etMapSearchPlaces.text.toString())
        }
    }

    private fun requestPermission() {
        if (LocationUtility.hasLocationPermission(requireContext())) {
            return
        }
        EasyPermissions.requestPermissions(
            this ,
            getString(R.string.requestpermission_rationale) ,
            REQUEST_CODE_LOCATION_PERMISSION ,
            Manifest.permission.ACCESS_COARSE_LOCATION ,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    override fun onPermissionsDenied(
        requestCode: Int ,
        perms: MutableList<String>
    ) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this , perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermission()
        }
    }

    override fun onPermissionsGranted(
        requestCode: Int ,
        perms: MutableList<String>
    ) {
        setupFusedLocationProviderClient()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int ,
        permissions: Array<out String> ,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode , permissions , grantResults)
        EasyPermissions.onRequestPermissionsResult(
            requestCode , permissions , grantResults , this
        )
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        MapKitFactory.getInstance().onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }
}