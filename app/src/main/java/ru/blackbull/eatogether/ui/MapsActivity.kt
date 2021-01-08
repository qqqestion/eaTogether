package ru.blackbull.eatogether.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_maps.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.ui.viewmodels.FirebaseViewModel
import ru.blackbull.eatogether.ui.viewmodels.PlaceViewModel


class MapsActivity : AppCompatActivity() , OnMapReadyCallback ,
    GoogleMap.OnMapClickListener , GoogleMap.OnMarkerClickListener ,
    View.OnKeyListener , GoogleMap.OnMapLongClickListener , AdapterView.OnItemClickListener ,
    View.OnClickListener {

    val placeViewModel: PlaceViewModel by viewModels()
    val firebaseViewModel: FirebaseViewModel by viewModels()

    private val REQUEST_CODE: Int = 0

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var bottomSheet: BottomSheetDialog

    private lateinit var titles: Array<String>
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerList: ListView


    private var userMaker: Marker? = null
    private var placeMarkers: MutableList<Marker> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        bottomSheet = createBottomSheet(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        search.setOnKeyListener(this)

        titles = resources.getStringArray(R.array.titles)
        drawerLayout = findViewById(R.id.maps_drawer_layout)
        drawerList = findViewById(R.id.maps_drawer)
        drawerList.adapter = ArrayAdapter(
            this ,
            android.R.layout.simple_list_item_1 ,
            titles
        )
        drawerList.onItemClickListener = this
        maps_menu_btn.setOnClickListener(this)

        placeViewModel.searchPlaces.observe(this , Observer { places ->
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

    override fun onResume() {
        super.onResume()
        checkAccessLocationPermission()
        if (!firebaseViewModel.isAuthenticated()) {
            val intent = Intent(application , StartActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkAccessLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this ,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this ,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION ,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            ActivityCompat.requestPermissions(this , permissions , REQUEST_CODE)
        }
    }


    private fun getCurrentLocation() {
        try {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latLng = LatLng(location.latitude , location.longitude)
//                val latLng = LatLng(59.941170 , 30.302707)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng , 15F))
                }
            }
            Log.d("DebugAPI" , "getCurrentLocation: success")
        } catch (exp: SecurityException) {
            exp.printStackTrace()
            Log.d("DebugAPI" , "getCurrentLocation: failed: ${exp.message}")
            checkAccessLocationPermission()
        }

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapClickListener(this)
        mMap.setOnMarkerClickListener(this)
        mMap.setOnMapLongClickListener(this)
        try {
            mMap.isMyLocationEnabled = true
        } catch (exp: SecurityException) {
            Log.d("DebugAPI" , "onMapReady: failed: ${exp.message}" , exp)
            checkAccessLocationPermission()
        }
        getCurrentLocation()
    }

    override fun onMapClick(location: LatLng) {
        userMaker?.remove()
        userMaker = createMarker(
            location , "user" ,
            BitmapDescriptorFactory.HUE_BLUE
        )
    }

    private fun createMarker(
        location: LatLng ,
        tag: String ,
        color: Float
    ): Marker {
        val marker = mMap.addMarker(
            MarkerOptions().position(location).icon(
                BitmapDescriptorFactory.defaultMarker(color)
            )
        )
        marker.tag = tag
        return marker
    }

    override fun onRequestPermissionsResult(
        requestCode: Int ,
        permissions: Array<out String> ,
        grantResults: IntArray
    ) {

        if (requestCode == REQUEST_CODE &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
        }
    }

    override fun onMarkerClick(it: Marker): Boolean {
        if (it.tag == "user") {
            val intent = Intent(this , InformationActivity::class.java)
            intent.putExtra("lat" , it.position.latitude)
            intent.putExtra("lng" , it.position.longitude)
            startActivity(intent)
        } else {
            bottomSheet.show()
        }
        return false
    }

    private fun createBottomSheet(context: Context): BottomSheetDialog {
        val bottomSheetDialog =
            BottomSheetDialog(
                context ,
                R.style.BottomSheetDialogTheme
            )
        val bottomSheetView: View = LayoutInflater.from(applicationContext)
            .inflate(
                R.layout.map_search_bottom_sheet ,
                findViewById<LinearLayout>(R.id.container_bottom_sheet)
            )
        bottomSheetDialog.setContentView(bottomSheetView)
        return bottomSheetDialog
    }

    override fun onKey(
        view: View? ,
        keyCode: Int ,
        event: KeyEvent?
    ): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER ||
                keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                searchPlace(search.text.toString())
                return true;
            }
        }
        return false;
    }

    private fun searchPlace(placeName: String) {
        placeMarkers.forEach {
            it.remove()
        }
        placeViewModel.searchPlaces(placeName)
    }

    override fun onMapLongClick(p0: LatLng?) {
        Toast.makeText(this , "Hello" , Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(drawerList)) {
            drawerLayout.closeDrawer(drawerList)
        } else {
            super.onBackPressed()
        }
    }

    override fun onItemClick(adapterView: AdapterView<*>? , view: View? , i: Int , l: Long) {
        val intent = when (titles[i]) {
            "Профиль" -> Intent(this , ProfileActivity::class.java)
            "Мои мероприятия" -> Intent(this , MyPartiesActivity::class.java)
            "Рядом" -> Intent(this , NearbyActivity::class.java)
            else -> return
        }
        startActivity(intent)
        drawerLayout.closeDrawer(drawerList)
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                R.id.maps_menu_btn ->
                    drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }
}
