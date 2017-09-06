package com.example.deepn.flicker;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class ForecastFragment extends Fragment {
    public ArrayAdapter<String> mForecastAdapter ;

    public ForecastFragment() {
        // Required empty public constructor
    }

    //
    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forecast_menu, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_forecast, container, false);


        //This lines are to add toolbar
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.forecast_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        //Till Here


        setHasOptionsMenu(true);
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

        //This lines of code set an item click listener to android
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String forecast = mForecastAdapter.getItem(position);
                Toast.makeText(getActivity(), forecast, Toast.LENGTH_SHORT);
            }
        });
        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                return true;

            case R.id.action_refresh:
                FetchWeather fetchWeather = new FetchWeather();
                fetchWeather.execute();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
    public class FetchWeather extends AsyncTask<Void,Void,String>{
        private ArrayList<String> jsonWeatherParser(String jsonForecast) throws JSONException {
            ArrayList<String> weatherArray = new ArrayList<>();
            JSONObject weatherObject = new JSONObject(jsonForecast);
            JSONArray list = weatherObject.getJSONArray("list");
            for (int i = 0; i < list.length(); i++){
                JSONObject weather = list.getJSONObject(i);
                DateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                Date netDate = (new Date(Long.parseLong(weather.get("dt").toString())));
                String date = sdf.format(netDate);
                JSONObject temp = (JSONObject) weather.get("temp");
                String max_temp = temp.get("max").toString();
                String min_temp = temp.get("min").toString();
                weatherArray.add(date + " " + max_temp + " " + min_temp);
                Log.v("output",date + " " + max_temp + " " + min_temp);
            }
            return weatherArray;
        }
        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonForecast = null;
            try {
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=london,uk&APPID=c10e7100063f10864ba3ffb839aed7f3");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                if(inputStream == null){
                    jsonForecast = null;
                }
                StringBuffer buffer = new StringBuffer();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine())!= null){
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0){
                    jsonForecast = null;
                }
                jsonForecast = buffer.toString();
                //Log.v("output", jsonForecast);
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (urlConnection != null){
                    urlConnection.disconnect();
                }
                if (reader != null){
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            
            return jsonForecast;
        }

        protected void onPostExecute(String forecast){
            Log.v("post-output",forecast);
            try {
                ArrayList<String> forecastList = jsonWeatherParser( forecast );
                if (forecastList.size() != 0) {
                    mForecastAdapter.clear();
                    mForecastAdapter.addAll(forecastList);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
