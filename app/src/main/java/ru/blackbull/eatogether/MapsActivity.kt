package ru.blackbull.eatogether

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.blackbull.eatogether.extensions.shortToast
import ru.blackbull.eatogether.googleplacesapi.ResultList
import ru.blackbull.eatogether.modules.NetworkModule

class MapsActivity : AppCompatActivity() , OnMapReadyCallback ,
    GoogleMap.OnMapClickListener , GoogleMap.OnMarkerClickListener ,
    View.OnKeyListener , GoogleMap.OnMapLongClickListener {

    private val theGooglePlaceApiService = NetworkModule.theGooglePlaceApiService

    private val REQUEST_CODE: Int = 0

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var searchField: EditText
    private lateinit var bottomSheet: BottomSheetDialog

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

        searchField = findViewById(R.id.search)
        searchField.setOnKeyListener(this)

        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword("hello.world@email.com" , "Aa123456789")
            .addOnFailureListener {
                it.printStackTrace()
                Toast.makeText(
                    this ,
                    "Ошибка авторизации" ,
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    override fun onResume() {
        super.onResume()
        checkAccessLocationPermission()
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
//            val latLng = LatLng(location.latitude , location.longitude)
                val latLng = LatLng(59.941170 , 30.302707)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng , 15F))
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
        intent = Intent(this , InformationActivity::class.java)
        mMap.setOnMarkerClickListener(this)
        mMap.setOnMapLongClickListener(this)
        try {
            mMap.isMyLocationEnabled = true
        } catch (exp: SecurityException) {
            exp.printStackTrace()
            Log.d("DebugAPI" , "onMapReady: failed: ${exp.message}")
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
            intent.putExtra("lat" , it.position.latitude)
            intent.putExtra("lng" , it.position.longitude)
            startActivity(intent)
        } else {
            bottomSheet.show()
        }
        return false
    }

    private fun createBottomSheet(context: Context): BottomSheetDialog {
        val bottomSheetDialog: BottomSheetDialog =
            BottomSheetDialog(context , R.style.BottomSheetDialogTheme)
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
                shortToast(searchField.text.toString())
                searchPlace(searchField.text.toString())
                return true;
            }
        }
        return false;
    }

    private fun searchPlace(placeName: String) {
        placeMarkers.forEach {
            it.remove()
        }
        val responseCall = theGooglePlaceApiService.getPlacesByName(placeName)
        responseCall.enqueue(object : Callback<ResultList> {
            override fun onResponse(
                call: Call<ResultList> ,
                response: Response<ResultList>
            ) {
                val responseResult = response.body()
                if (responseResult?.status == "OK") {
                    Log.d(
                        "DebugAPI" ,
                        "Retrofit -> onResponse: success: ${responseResult.placeList}"
                    )
                    responseResult.placeList.forEach {
                        placeMarkers.add(
                            createMarker(
                                LatLng(it.geometry.location.lat , it.geometry.location.lng) ,
                                it.placeId ,
                                BitmapDescriptorFactory.HUE_RED
                            )
                        )
                    }
                } else {
                    Log.d(
                        "DebugAPI" ,
                        "Retrofit -> onResponse: failed: ${responseResult?.errorMessage}"
                    )
                }
            }

            override fun onFailure(call: Call<ResultList> , t: Throwable) {
                t.printStackTrace()
                Log.d("DebugAPI" , "Retrofit -> onFailure: ${t.message}")
            }
        })
    }

    override fun onMapLongClick(p0: LatLng?) {
        Toast.makeText(this , "Hello" , Toast.LENGTH_SHORT).show()
    }
}
