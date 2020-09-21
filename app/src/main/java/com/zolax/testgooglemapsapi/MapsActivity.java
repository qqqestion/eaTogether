package com.zolax.testgooglemapsapi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Map;
import java.util.Random;

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnPoiClickListener {

    MapFragment mapFragment;
    GoogleMap map;
    FusedLocationProviderClient fusedLocationProviderClient;
    Intent openInputEventInformation;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //инициализация переменных
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        openInputEventInformation = new Intent(this, InputEventInformationActivity.class);

        //Проверка наличия разрешения на отслеживание местонахождения
        if (ActivityCompat.checkSelfPermission(this
                , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //Если разрешение есть, запускается карта
            onPermissionGranted();
        } else {
            //Если нет разрешения
            //Разрешение запришивается
            ActivityCompat.requestPermissions(this
                    , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }


    }

    private void onPermissionGranted() {
        mapFragment = (MapFragment) getFragmentManager().
                findFragmentById(R.id.googleMaps);
        mapFragment.getMapAsync(this);
    }

    private void updateMapUI(GoogleMap googleMap) {
        if (googleMap == null) {
            return;
        } else {
            googleMap.setMyLocationEnabled(true);
            googleMap.setOnMyLocationClickListener(this);
            googleMap.setOnMyLocationButtonClickListener(this);
            googleMap.setOnMapClickListener(this);

            UiSettings settings = googleMap.getUiSettings();

            settings.setMapToolbarEnabled(false);

            settings.setZoomControlsEnabled(true);
            settings.setCompassEnabled(true);
            settings.setMyLocationButtonEnabled(true);
        }
    }

    private void getCurrentLocation(final GoogleMap map) {
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double currentLat = location.getLatitude();
                    double currentLong = location.getLongitude();

                    LatLng myLocation = new LatLng(currentLat, currentLong);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
                }
            }
        });
    }

    @Override
    //callback метода .getMapAsync(this)
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        updateMapUI(map);
        getCurrentLocation(map);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        startActivity(openInputEventInformation);
        String  title;
        String  sportType;
        String  authorName;
        int markerId = new Random().nextInt();
        title = preferences.getString("title","");
        sportType = preferences.getString("sportType","");
        authorName = preferences.getString("authorName","");
        preferences.edit().clear().apply();
        map.addMarker(new MarkerOptions()
                .title(title + " " + sportType + " " + authorName)
                .position(latLng));
    }

    // метод отслеживающий нажатие на мое местонахождение
    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current Location: " + location, Toast.LENGTH_LONG).show();
    }

    //метод отслеживающий нажатие на кнопку местонаходения
    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "Current Location: button clicked", Toast.LENGTH_LONG).show();
        return false;
    }


    //метод отслеживаюший на нажатие на объекты на карте(парки, школы, магазины и т.п.)
    @Override
    public void onPoiClick(PointOfInterest pointOfInterest) {
        Log.d("$$$$$$$$$$$$$$$$$", pointOfInterest.latLng.toString());
    }

    //метод запроса разрешения на отслеживание местонахождения
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted();
            }
        }
    }


}
