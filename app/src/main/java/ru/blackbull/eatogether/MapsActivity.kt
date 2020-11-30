package ru.blackbull.eatogether

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.blackbull.eatogether.utils.PlaceDataParser
import ru.blackbull.eatogether.InformationActivity as informationActivity1

internal class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener, TextWatcher, View.OnKeyListener,
    GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var searchField: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        checkAccessLocationPermission()

        searchField = findViewById(R.id.search)
        searchField.addTextChangedListener(this)
        searchField.setOnKeyListener(this)
    }

    private fun checkAccessLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            ActivityCompat.requestPermissions(this, permissions, 0)
        } else {
            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
        }
    }


    private fun getCurrentLocation() {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            location ->
//            if (location == null || location.accuracy > 100) {
//                val mLocationCallback = object : LocationCallback() {
//                    override fun onLocationResult(locationResult: LocationResult?) {
//                        stopLocationUpdates()
//                        if (locationResult != null && locationResult.locations.isNotEmpty()) {
//                            val newLocation = locationResult.locations[0]
//                            callback.onCallback(Status.SUCCESS, newLocation)
//                        } else {
//                            callback.onCallback(Status.ERROR_LOCATION, null)
//                        }
//                    }
//                }
//
//                fusedLocationProviderClient!!.requestLocationUpdates(
//                    getLocationRequest(),
//                    mLocationCallback, null
//                )
//            } else {
//                callback.onCallback(Status.SUCCESS, location)
//            }
            val latLng = LatLng(location.latitude, location.longitude)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15F));
        }

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapClickListener(this)
        intent = Intent(this, informationActivity1::class.java)
        mMap.setOnMarkerClickListener(this)
        mMap.setOnMapLongClickListener(this)
        mMap.isMyLocationEnabled = true
        getCurrentLocation()
    }

    override fun onMapClick(location: LatLng) {
        mMap.clear()
        createMarker(location, "user", BitmapDescriptorFactory.HUE_GREEN)
    }

    private fun createMarker(location: LatLng, tag: String, color: Float) {
        val marker = mMap.addMarker(
            MarkerOptions().position(location).icon(
                BitmapDescriptorFactory.defaultMarker(color)
            )
        )
        marker.tag = tag
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode == 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
        }
    }

    override fun onMarkerClick(it: Marker): Boolean {
        if (it.tag == "user") {
            intent.putExtra("lat", it.position.latitude)
            intent.putExtra("lng", it.position.longitude)
            startActivity(intent)
        } else {
            // TODO: отработать нажатие на маркер заведения
        }
        return false
    }

    override fun afterTextChanged(p0: Editable?) {

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(changedText: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onKey(view: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
                Toast.makeText(this, searchField.text.toString(), Toast.LENGTH_SHORT).show()
                searchPlace(searchField.text.toString())
                return true;
            }
        }
        return false;
    }

    private fun searchPlace(query: String) {
        mMap.clear()
        GlobalScope.launch(Dispatchers.Main) {
            val placeList = PlaceDataParser().getPlaceByName(query)
            for (place in placeList) {
                Log.d("search", "searchPlace: ${place.name}")
                createMarker(
                    LatLng(place.geometry.location.lat, place.geometry.location.lng),
                    place.placeId,
                    BitmapDescriptorFactory.HUE_RED
                )
            }
        }

    }

    override fun onMapLongClick(p0: LatLng?) {
        Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show()
    }
}
