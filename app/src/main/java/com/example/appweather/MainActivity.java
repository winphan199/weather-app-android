package com.example.appweather;

import androidx.annotation.NonNull;
import androidx.annotation.StyleableRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements LocationListener {


    // Variables
    double latitude = 0;
    double longitude = 0;
    LocationManager locationManager;
    String cityname = "";
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize location service
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // Store the object state to the bundle
        savedInstanceState.putDouble("LATITUDE", latitude);
        savedInstanceState.putDouble("LONGITUDE", longitude);
        savedInstanceState.putString("LAST_SEARCH", cityname);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore the object  state to the bundle
        latitude = savedInstanceState.getDouble("LATITUDE");
        longitude = savedInstanceState.getDouble("LONGITUDE");
        cityname = savedInstanceState.getString("LAST_SEARCH");
        System.out.println("lat1: " + latitude + "long1: " + longitude);
    }

    @Override
    protected void  onStart() {
        super.onStart();
    }

    @Override
    protected void  onPause() {
        super.onPause();
        // unregister listener location service
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    protected void  onStop() {
        super.onStop();
    }

    @Override
    protected void  onResume() {
        super.onResume();

        //startGPS();
    }

    @Override
    protected void  onDestroy() {
        super.onDestroy();
    }

    public void openCity(String cityName) {
        // Open a new activity called ForecastAcitivity
        Intent intent = new Intent(this, ForecastActivity.class);

        // Send the city name to the activity
        intent.putExtra("CITY_NAME", cityName);
        startActivity(intent);
    }

    public void openCity(double latitude, double longitude) {
        Intent intent = new Intent(this, ForecastActivity.class);

        // Send latitude and longitude to the forecast activity.
        intent.putExtra("LATITUDE", latitude);
        intent.putExtra("LONGITUDE", longitude);
        startActivity(intent);
    }


    public void searchCity(View view) {

        // Get the city name from the edittext view
        EditText cityText = (EditText) findViewById(R.id.locationtext);

        // Convert to string
        cityname = cityText.getText().toString();

        // If user dont input anything, tell the user to do so
        if (cityname.length() <= 0) {
            Toast.makeText(this, "Please input city name", Toast.LENGTH_SHORT).show();
        }
        else {
            openCity(cityname);
        }
    }

    public void searchNY(View view) {
        cityname = "new york";
        openCity(cityname);
    }

    public void searchParis(View view) {
        cityname = "paris";
        openCity(cityname);
    }

    public void searchVN(View view) {
        cityname = "thanh pho ho chi minh";
        openCity(cityname);
    }

    public void searchHel(View view) {
        cityname = "tokyo";
        openCity(cityname);
    }


    public void startGPS(View view) {

        // Asking user permission for using location service
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so ask it here!
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            return;
        }

        // Start listening to location update
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        // Notify the user
        //Toast.makeText(this, "Getting location info, please wait!", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Getting location info, please wait!", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {


        // Update latitude and longitude
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        Toast.makeText(this, "lat: " + latitude + " long: " + longitude, Toast.LENGTH_SHORT).show();

        if (count <= 1) {
            // Open the forecast activity with latitude and longitude
            openCity(latitude, longitude);
        }
        else {
            count++;
        }
    }
}