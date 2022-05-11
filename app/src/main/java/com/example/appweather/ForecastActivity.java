package com.example.appweather;

import static java.lang.Math.round;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.*;

public class ForecastActivity extends AppCompatActivity {

    // Variables
    private RequestQueue queue;
    String cityName = "";
    double latitude = 0.0;
    double longitude = 0.0;
    String url = "";
    TextView cityTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        // Initialize request queue
        queue = Volley.newRequestQueue(this);

        // Get the cityName received from the main activity
        cityName = getIntent().getStringExtra("CITY_NAME");

        // Get the latitude and longitude receive from the main activity
        // because latitude range [-90;90], longitude range [-180;180]
        latitude = getIntent().getDoubleExtra("LATITUDE", -91);
        longitude = getIntent().getDoubleExtra("LONGITUDE", -181);

        // Set the city name of the text view
        cityTextView = findViewById(R.id.forecastlocation);
        if (cityName != null) {
            cityTextView.setText(cityName.toUpperCase());

            // render the UI with the received info
            updateWeather(cityName);
        }
        else if (latitude > -91 && longitude > -181) {

            // render the UI with the received info
            updateWeather(latitude, longitude);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // Store the object state to the bundle
        savedInstanceState.putDouble("LATITUDE", latitude);
        savedInstanceState.putDouble("LONGITUDE", longitude);
        savedInstanceState.putString("CITY_NAME", cityName);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore the object  state to the bundle
        latitude = savedInstanceState.getDouble("LATITUDE");
        longitude = savedInstanceState.getDouble("LONGITUDE");
        cityName = savedInstanceState.getString("CITY_NAME");
    }


    // update weather with city name
    public void updateWeather(String cityName) {
        url = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&units=metric&appid=a8a71fbc9440ced14b8e7ab79837b738";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {

            parseJsonAndUpdateUI(response);
        }, error -> {
            cityTextView.setText("City not found".toUpperCase());
            Toast.makeText(this, "Error: " + cityName + " is not a valid city", Toast.LENGTH_SHORT).show();

        });
        queue.add(stringRequest);
    }

    //update weather with lat and long
    public void updateWeather(double latitude, double longitude) {
        url = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&units=metric&appid=a8a71fbc9440ced14b8e7ab79837b738";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {

            parseJsonAndUpdateUI(response);
            cityTextView.setText(cityName.toUpperCase());
        }, error -> {
            cityTextView.setText("City not found".toUpperCase());
            Toast.makeText(this, "Error: " + cityName + " is not a valid city", Toast.LENGTH_SHORT).show();

        });
        queue.add(stringRequest);
    }

    private void parseJsonAndUpdateUI(String response) {
        String weatherDesc = "";
        double temperature = 0.0;
        double tempmax = 0.0;
        double tempmin = 0.0;
        long sunset = 0;
        long sunrise = 0;
        int humidity = 0;
        double feelslike = 0.0;
        double windspeed = 0.0;
        long visibility = 0;

        try {
            JSONObject weather = new JSONObject(response);
            cityName = weather.getString("name");
            temperature = weather.getJSONObject("main").getDouble("temp");
            weatherDesc = weather.getJSONArray("weather").getJSONObject(0).getString("main");
            tempmax = weather.getJSONObject("main").getDouble("temp_max");
            tempmin = weather.getJSONObject("main").getDouble("temp_min");
            sunset = weather.getJSONObject("sys").getLong("sunset");
            sunrise = weather.getJSONObject("sys").getLong("sunrise");
            humidity = weather.getJSONObject("main").getInt("humidity");
            feelslike = weather.getJSONObject("main").getDouble("feels_like");
            windspeed = weather.getJSONObject("wind").getDouble("speed");
            visibility = weather.getLong("visibility");

            // set background repsective to weather condition
            weatherDesc = weatherDesc.toLowerCase(Locale.ROOT);
            ConstraintLayout background = findViewById(R.id.background);
            if (weatherDesc.equals("clear")) {
                background.setBackground(getDrawable(R.drawable.sunny));
                changeTextColor(this.getResources().getColor(R.color.black));
            }
            else if (weatherDesc.equals("clouds")) {
                background.setBackground(getDrawable(R.drawable.scatterclounds));
                changeTextColor(this.getResources().getColor(R.color.black));
            }
            else if (weatherDesc.equals("rain") || weatherDesc.equals("drizzle")) {
                background.setBackground(getDrawable(R.drawable.rain));
                changeTextColor(this.getResources().getColor(R.color.white));
            }
            else if (weatherDesc.equals("thunderstorm")) {
                background.setBackground(getDrawable(R.drawable.thunderstorm));
                changeTextColor(this.getResources().getColor(R.color.white));
            }
            else if (weatherDesc.equals("snow")) {
                background.setBackground(getDrawable(R.drawable.snow));
                changeTextColor(this.getResources().getColor(R.color.black));
            }
            else if (weatherDesc.equals("mist") || weatherDesc.equals("fog") || weatherDesc.equals("smoke") || weatherDesc.equals("dust")) {
                background.setBackground(getDrawable(R.drawable.mist));
                changeTextColor(this.getResources().getColor(R.color.white));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // show temperature
        TextView temperatureTextView = findViewById(R.id.temperature);
        temperatureTextView.setText(Math.round(temperature) + "°C");
        // show description
        TextView weatherDescTextView = findViewById(R.id.weatherstate);
        weatherDescTextView.setText(weatherDesc);
        // show max min temperature
        TextView tempMaxMinTextView = findViewById(R.id.tempmaxmin);
        tempMaxMinTextView.setText("H:" + Math.round(tempmax) + "°C    L:" + Math.round(tempmin) + "°C");
        // show sunrise
        TextView sunRiseTextView = findViewById(R.id.sunrisevalue);
        Timestamp t = new Timestamp(sunrise * 1000);
        sunRiseTextView.setText(formatTo2Digit((long) t.getHours()) + ":" + formatTo2Digit((long) t.getMinutes()));
        // show sunset
        TextView sunSetTextView = findViewById(R.id.sunsetvalue);
        t = new Timestamp(sunset * 1000);
        sunSetTextView.setText(formatTo2Digit((long) t.getHours()) + ":" + formatTo2Digit((long) t.getMinutes()));
        // show humidity
        TextView humidityTextView = findViewById(R.id.humidityvalue);
        humidityTextView.setText(humidity + "%");
        // show feels like
        TextView feelslikeTextView = findViewById(R.id.feelslikevalue);
        feelslikeTextView.setText(Math.round(feelslike) + "°C");
        // show wind speed
        TextView windSpeedTextView = findViewById(R.id.windvalue);
        windSpeedTextView.setText(round(windspeed * 3.6, 2) + " km/h");

        // show visibility
        TextView visibilityTextView = findViewById(R.id.visibilityvalue);
        visibilityTextView.setText(round(visibility / 1000, 0) + " km");
    }


    // format time to always in 00:00 form
    private String formatTo2Digit(Long time) {
        if (time < 10) {
            return "0" + time;
        }
        else {
            return time.toString();
        }
    }

    // Rounding number
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    // change text color
    private void changeTextColor(int color) {
        TextView location = findViewById(R.id.forecastlocation);
        TextView weatherdesc = findViewById(R.id.weatherstate);
        TextView temperatureTextView = findViewById(R.id.temperature);
        TextView tempMaxMinTextView = findViewById(R.id.tempmaxmin);
        TextView sunRiseLbTextView = findViewById(R.id.sunriselabel);
        TextView sunRiseTextView = findViewById(R.id.sunrisevalue);
        TextView sunSetLbTextView = findViewById(R.id.sunsetlabel);
        TextView sunSetTextView = findViewById(R.id.sunsetvalue);
        TextView humidityLbTextView = findViewById(R.id.humiditylabel);
        TextView humidityTextView = findViewById(R.id.humidityvalue);
        TextView feelslikeLbTextView = findViewById(R.id.feelslikelabel);
        TextView feelslikeTextView = findViewById(R.id.feelslikevalue);
        TextView windSpeedLbTextView = findViewById(R.id.windlabel);
        TextView windSpeedTextView = findViewById(R.id.windvalue);
        TextView visibilityLbTextView = findViewById(R.id.visibilitylabel);
        TextView visibilityTextView = findViewById(R.id.visibilityvalue);
        View divider1 = findViewById(R.id.divider);
        View divider2 = findViewById(R.id.divider2);
        View divider3 = findViewById(R.id.divider4);

        location.setTextColor(color);
        weatherdesc.setTextColor(color);
        temperatureTextView.setTextColor(color);
        tempMaxMinTextView.setTextColor(color);
        sunRiseLbTextView.setTextColor(color);
        sunRiseTextView.setTextColor(color);
        sunSetLbTextView.setTextColor(color);
        sunSetTextView.setTextColor(color);
        humidityLbTextView.setTextColor(color);
        humidityTextView.setTextColor(color);
        feelslikeLbTextView.setTextColor(color);
        feelslikeTextView.setTextColor(color);
        windSpeedLbTextView.setTextColor(color);
        windSpeedTextView.setTextColor(color);
        visibilityLbTextView.setTextColor(color);
        visibilityTextView.setTextColor(color);
        if (divider1 != null) {
            divider1.setBackgroundColor(color);
        }

        divider2.setBackgroundColor(color);
        divider3.setBackgroundColor(color);

    }

}