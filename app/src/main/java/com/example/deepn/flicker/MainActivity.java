package com.example.deepn.flicker;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ForecastFragment forecastFargment = new ForecastFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.main_activity, forecastFargment).commit();
    }
}
