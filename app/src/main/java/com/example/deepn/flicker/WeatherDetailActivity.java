package com.example.deepn.flicker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class WeatherDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_detail);
        if (savedInstanceState == null) {
            WeatherDetailFragment weatherDetailFragment = new WeatherDetailFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.weatherDetail_activity, weatherDetailFragment).commit();
        }
    }
}
