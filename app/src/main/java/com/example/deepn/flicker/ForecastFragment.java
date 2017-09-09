package com.example.deepn.flicker;


import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

    //This function updates weather
    public void updateWeather(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = sharedPreferences.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));
        FetchWeather fetchWeather = new FetchWeather();
        fetchWeather.execute(location);

    }
    //This function creates weather
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
                //Log.v("list-clicked",forecast);

                //We pass second argument name of class which we want to include
                Intent intent = new Intent(getActivity(),WeatherDetailActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, forecast);
                startActivity(intent);
            }
        });
        return rootView;
    }
    @Override
    public void onStart(){
        super.onStart();
        updateWeather();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                Intent intent = new Intent(getActivity(), DetailSettingActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_refresh:
                updateWeather();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public class FetchWeather extends AsyncTask<String,Void,String>{
        private ArrayList<String> jsonWeatherParser(String jsonForecast) throws JSONException {
            ArrayList<String> weatherArray = new ArrayList<>();
            JSONObject weatherObject = new JSONObject(jsonForecast);
            int cod = Integer.parseInt(weatherObject.get("cod").toString());
            if (cod == 200) {
                JSONArray list = weatherObject.getJSONArray("list");
                for (int i = 0; i < list.length(); i++) {
                    JSONObject weather = list.getJSONObject(i);
                    DateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                    Date netDate = (new Date(Long.parseLong(weather.get("dt").toString())));
                    String date = sdf.format(netDate);
                    JSONArray wether_array = weather.getJSONArray("weather");
                    JSONObject weather_detail = wether_array.getJSONObject(0);
                    String desc = weather_detail.get("main").toString();
                    JSONObject temp = (JSONObject) weather.get("temp");
                    String max_temp = temp.get("max").toString();
                    String min_temp = temp.get("min").toString();
                    String tempHighLow = tempratureConverter(max_temp, min_temp);
                    weatherArray.add(date + "  " + desc + "  " + tempHighLow);
                    //Log.v("output", date + "  " + desc + "  " + tempHighLow);
                }
            }
            return weatherArray;
        }

        private String tempratureConverter(String high,String low){
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String unitType = sharedPreferences.getString(getString(R.string.pref_temp_key),
                    getString(R.string.pref_temp_default));

            double max = Double.parseDouble(high);
            double min = Double.parseDouble(low);
            max = max - 273;
            min = min - 273;
            if (unitType.equals(getString(R.string.pref_temp_imperial))){
                max = (max * 1.8) + 32;
                min = (min * 1.8) + 32;
            }
            else if (!unitType.equals(getString(R.string.pref_temp_metric))){
                Log.d("unit","Unit type not found");
            }
            max = Math.round(max);
            min = Math.round(min);

            return max + "/" + min ;

        }
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonForecast = null;
            String location = params[0];
            try {
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q="+location+"&APPID=c10e7100063f10864ba3ffb839aed7f3");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                if(inputStream == null){
                    return null;
                }
                StringBuffer buffer = new StringBuffer();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine())!= null){
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0){
                    return null;
                }
                jsonForecast = buffer.toString();
                Log.e("output", jsonForecast);
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
            if (forecast != null) {
                try {
                    ArrayList<String> forecastList = jsonWeatherParser(forecast);
                    if (forecastList.size() != 0) {
                        mForecastAdapter.clear();
                        mForecastAdapter.addAll(forecastList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                Toast toast = Toast.makeText(getActivity(),"Wrong pin code",Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }
}
