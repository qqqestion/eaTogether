package ru.blackbull.eatogether.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
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
import ru.blackbull.eatogether.other.Constants.MAP_ZOOM
import ru.blackbull.eatogether.other.Constants.REQUEST_CODE_LOCATION_PERMISSION
import ru.blackbull.eatogether.other.Constants.SEARCH_TIME_DELAY
import ru.blackbull.eatogether.other.LocationUtility
import timber.log.Timber

class MapFragment : Fragment(R.layout.fragment_map) , EasyPermissions.PermissionCallbacks {

    private var map: GoogleMap? = null

    private val mapViewModel: MapViewModel by viewModels()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var placeMarkers: MutableList<Marker> = mutableListOf()

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        subscribeToObservers()
        requestPermission()
        mapView?.onCreate(savedInstanceState)
        setupFusedLocationProviderClient()
        Snackbar.make(
            requireView(),
            "Hello, World!",
            Snackbar.LENGTH_LONG
        ).show()

        var job: Job? = null
        etMapSearchPlaces.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_TIME_DELAY)
                editable?.let {
                    placeMarkers.forEach { marker ->
                        marker.remove()
                    }

                    mapViewModel.searchPlaces(it.toString())
                }
            }
        }

        mapView?.getMapAsync {
            map = it
            map?.isMyLocationEnabled = true
            map?.setOnMapLongClickListener { location ->
                val bundle = Bundle().apply {
                    putParcelable("location" , location)
                }
                findNavController().navigate(
                    R.id.action_mapFragment_to_recycleRestaurantsFragment ,
                    bundle
                )
            }
            map?.setOnMarkerClickListener { marker ->
                val bundle = Bundle().apply {
                    putString("placeId" , marker.tag.toString())
                }
                findNavController().navigate(
                    R.id.action_mapFragment_to_placeDetailFragment ,
                    bundle
                )
                true
            }
            Timber.d("getMapAsync")
        }
    }

    private fun subscribeToObservers() {
        mapViewModel.searchPlaces.observe(viewLifecycleOwner , Observer { places ->
            places.forEach { place ->
                placeMarkers.add(
                    createMarker(
                        LatLng(place.geometry.location.lat , place.geometry.location.lng) ,
                        place.placeId ,
                        BitmapDescriptorFactory.HUE_RED
                    )
                )
            }
        })
    }

    @SuppressLint("MissingPermission")
    private fun setupFusedLocationProviderClient() {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        val request = LocationRequest().apply {
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

    private fun createMarker(latLng: LatLng , tag: String , color: Float): Marker {
        val marker = map?.addMarker(
            MarkerOptions().position(latLng).icon(
                BitmapDescriptorFactory.defaultMarker(color)
            )
        )
        marker?.tag = tag
        return marker!!
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

    override fun onPermissionsDenied(requestCode: Int , perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this , perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermission()
        }
    }

    override fun onPermissionsGranted(requestCode: Int , perms: MutableList<String>) {
        /* NO-OP */
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

    var isFirstLocation = true

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            if (isFirstLocation) {
                result?.lastLocation?.let { location ->
                    val latLng = LatLng(location.latitude , location.longitude)
                    map?.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            latLng ,
                            MAP_ZOOM
                        )
                    )
                }
                isFirstLocation = false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        isFirstLocation = true
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

}