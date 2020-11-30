package ru.blackbull.eatogether.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import ru.blackbull.eatogether.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class UpdateGeo extends AppCompatActivity {
    private TextView textView;
    private static final String TAG = "$$$$$$$$$$$$$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textView = findViewById(R.id.text);

        if (ActivityCompat.checkSelfPermission(this
                , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //Если разрешение есть, запускается карта
            textView.setText("Permission granted");
            onPermissionGranted();
        } else {
            //Если нет разрешения
            //Разрешение запришивается
            textView.setText("Permission denied");
            ActivityCompat.requestPermissions(this
                    , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }


    private void onPermissionGranted() {
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new UpdateGeo.MyLocationListener();
        ContextCompat.checkSelfPermission(
                getApplication(), Manifest.permission.ACCESS_FINE_LOCATION);// You can use the API that requires the permission.
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 1, locationListener);
    }


    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            Toast.makeText(
                    getBaseContext(),
                    "Location changed: Lat: " + loc.getLatitude() + " Lng: "
                            + loc.getLongitude(), Toast.LENGTH_SHORT).show();
            String longitude = "Longitude: " + loc.getLongitude();
            Log.v(TAG, longitude);
            String latitude = "Latitude: " + loc.getLatitude();
            Log.v(TAG, latitude);

            /*------- To get city name from coordinates -------- */
            String cityName = null;
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(loc.getLatitude(),
                        loc.getLongitude(), 1);
                if (addresses.size() > 0) {
                    System.out.println(addresses.get(0).getLocality());
                    cityName = addresses.get(0).getLocality();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            String s = longitude + "\n" + latitude + "\n\nMy Current City is: "
                    + cityName;
            Toast.makeText(getApplication(), s, Toast.LENGTH_LONG).show();
            textView.setText(s);
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
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


