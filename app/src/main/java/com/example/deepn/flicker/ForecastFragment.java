package com.example.deepn.flicker;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ForecastFragment extends Fragment {
    public ArrayAdapter<String> mForecastAdapter ;

    public ForecastFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_forecast, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.forecastList);
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < 20; i++){
            arrayList.add("List item " + i);
        }
        mForecastAdapter = new ArrayAdapter<String>(getContext(),
                R.layout.forecast_textview,
                R.id.forcasttextview,
                arrayList);
        listView.setAdapter(mForecastAdapter);

        return rootView;
    }

}
