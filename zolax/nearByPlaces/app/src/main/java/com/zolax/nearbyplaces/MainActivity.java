package com.zolax.nearbyplaces;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //Initialize variable
    Spinner spType;
    Button btFind;
    SupportMapFragment supportMapFragment;
    GoogleMap map;
    FusedLocationProviderClient fusedLocationProviderClient;
    double currentLat = 0, currentLong = 0;
    LatLng myLocation;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assign variable
        spType = findViewById(R.id.sp_type);
        btFind = findViewById(R.id.bt_find);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);

        //Initialize array of place type
        final String[] placeTypeList = {"restaurant", "cafe", "bakery","subway_station","food"};
        //Initialize array of place name
        String[] placeNameList = {"Restaurant", "Cafe", "Bakery","Metro","All food"};

        //set adapter on spinner
        spType.setAdapter(new ArrayAdapter<>(MainActivity.this
                , android.R.layout.simple_spinner_dropdown_item, placeNameList));

        //Initialize fused location provider client
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //Check permission
        if (ActivityCompat.checkSelfPermission(MainActivity.this
                , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //When permission granted
            //Call method
            getCurrentLocation();
        } else {
            //When permission denied
            //Request permission
            ActivityCompat.requestPermissions(MainActivity.this
                    , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
        btFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get selected position of spinner
                int i = spType.getSelectedItemPosition();
                //Initialize url
                String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" + //URL
                        "?location=" + currentLat + "," + currentLong + // Location lat and Long
                        "&radius=5000" + //Nearby radius
                        "&types=" + placeTypeList[i] + // placeType
                        "&sensor=true" + //Sensor
                        "&key=" + getResources().getString(R.string.google_map_key); // api key

                //Execute place task method to download json data
                new PlaceTask().execute(url);
            }
        });
    }

    private void getCurrentLocation() {
        //Initialize task location
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                //When success
                if (location != null) {
                    //When location is not equal
                    //Get current latitude
                    currentLat = location.getLatitude();
                    currentLong = location.getLongitude();

                    myLocation = new LatLng(currentLat,currentLong);


                    //Sync map
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            //When map is ready
                            map = googleMap;
                            //Zoom current map location
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(currentLat, currentLong), 15
                            ));
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //When permission granted
                //Call method
                getCurrentLocation();
            }
        }
    }

    private class PlaceTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            String data = null;
            try {
                //Initialize data
                data = downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            //Execute parser task
            new ParserTask().execute(s);
        }
    }

    private String downloadUrl(String string) throws IOException {
        //Initialize url
        URL url = new URL(string);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //Connect connection
        connection.connect();
        //Initialize input stream
        InputStream stream = connection.getInputStream();
        //Initialize buffer reader
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        //Initialize string builder
        StringBuilder builder = new StringBuilder();
        //Init string variable
        String line = "";
        //use while loop
        while ((line = reader.readLine()) != null) {
            //append line
            builder.append(line);
        }
        //Get append data
        String data = builder.toString();
        reader.close();
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {
        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {
            //create json parser class
            JsonParser jsonParser = new JsonParser();
            //init hasm map list
            List<HashMap<String, String>> mapList = null;
            //init json object
            JSONObject object = null;
            try {
                object = new JSONObject(strings[0]);
                //parse json object
                mapList  = jsonParser.parseResult(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Return map list
            return mapList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
            getCurrentLocation();
            //clear map
            map.clear();
            MarkerOptions myMarkerOptions = new MarkerOptions();
            myMarkerOptions.position(myLocation);
            myMarkerOptions.title("My Location");
            myMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            map.addMarker(myMarkerOptions);
            for (int i = 0; i < hashMaps.size() ; i++) {
                HashMap<String ,String > hashMapList = hashMaps.get(i);
                //get latitude
                double lat = Double.parseDouble(hashMapList.get("lat"));
                //get longitude
                double lng = Double.parseDouble(hashMapList.get("lng"));
                String name = hashMapList.get("name");
                //concat latitude and lng
                LatLng latLng = new LatLng(lat,lng);
                //init marker options
                MarkerOptions options = new MarkerOptions();
                //set position
                options.position(latLng);
                options.title(name);
                //add marker on map
                map.addMarker(options);
            }
        }
    }
}
