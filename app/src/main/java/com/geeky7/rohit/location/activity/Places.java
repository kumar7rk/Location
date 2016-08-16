package com.geeky7.rohit.location.activity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.geeky7.rohit.location.R;
import com.geeky7.rohit.location.service.BackgroundService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Rohit on 1/07/2016.
 */
public class Places extends Activity {
    TextView textView;
    static ArrayList<String> placeName= new ArrayList<>();
    static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
                super.onCreate(savedInstanceState);
        setContentView(R.layout.places);
        textView = (TextView)findViewById(R.id.placeName);

        Intent serviceIntent = new Intent(Places.this,BackgroundService.class);
        startService(serviceIntent);

        String sb = sbMethod().toString();
        new PlacesTask().execute(sb);

        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);

    }
    public void places(){
        Places places = new Places();
        String sb = sbMethod().toString();
        new PlacesTask().execute(sb);
    }
    public void placesS(){
        String sb = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-34.923792,138.6047722&radius=10&types=restaurant&sensor=true&key=AIzaSyC0ZdWHP1aun8cfHq9aXzOOztUaD1Fmw_I";
        new PlacesTask().execute(sb);
    }
    public StringBuilder sbMethod()
    {
        //jasmin -34.923792 138.6047722
        double mLatitude = -34.923792;
        double mLongitude = 138.6047722;
        int mRadius = 10;
        String lat = "",lon="";

        Bundle extras = getIntent().getExtras();
        lat = extras.getString("lat");
        lon = extras.getString("lon");

        //Manually added coordinates in MainActivity.java
        String latPlaces = MainActivity.latPlaces.getText().toString();
        String lonPlaces = MainActivity.lonPlaces.getText().toString();
        String radiusPlaces = MainActivity.radiusPlaces.getText().toString();
        mRadius = Integer.parseInt(radiusPlaces);

        Log.i("Places.lat,lon place",latPlaces+" " + lonPlaces);

        if (latPlaces.equals("-34.")&&lonPlaces.equals("138.")) {
        }
        else if (latPlaces.equals("")&&lonPlaces.equals("")) {
            mLatitude = Double.parseDouble(lat);
            mLongitude = Double.parseDouble(lon);
        }
        else{
            mLatitude = Double.parseDouble(latPlaces);
            mLongitude = Double.parseDouble(lonPlaces);
        }
        Log.i("Places.mlat,mlon",mLatitude+" " +mLongitude);

        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        sb.append("location=" + mLatitude + "," + mLongitude);
        sb.append("&radius="+radiusPlaces);
        sb.append("&types=" + "restaurant");
        sb.append("&sensor=true");
        sb.append("&key=AIzaSyC0ZdWHP1aun8cfHq9aXzOOztUaD1Fmw_I");
        Log.v("Places",sb.toString());
        return sb;
    }
    private void createNotification(String contentTitle, String contentText) {

        NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        //Build the notification using Notification.Builder
        Notification.Builder builder = new Notification.Builder(getApplicationContext())
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setAutoCancel(true)
                .setContentTitle(contentTitle)
                .setContentText(contentText);
        //Show the notification
        mNotificationManager.notify(1, builder.build());
    }

    //PlacesTask class to fetch the name of the places but in raw format
    public class PlacesTask extends AsyncTask<String, Integer, String>

    {
        String data = null;
        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }
        @Override
        protected void onPostExecute(String result) {
            ParserTask parserTask = new ParserTask( context);
            parserTask.execute(result);
          /*  textView.setText("Nothing Found");
            MainActivity.updatePlaceName("Nothing Found");*/
            Log.i("PlacesTask", result);
        }
    }// end of the placesTask class

    // in the main class
    private String downloadUrl(String strUrl) throws IOException
    {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();iStream = urlConnection.getInputStream();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();

        } catch (Exception e) {
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jObject;

        Context mContext;
        public ParserTask(Context context){
            mContext = context;
        }
        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;
            Place_JSON placeJson = new Place_JSON();

            try {
                jObject = new JSONObject(jsonData[0]);

                places = placeJson.parse(jObject);

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String, String>> list) {

            for (int i = 0; i < list.size(); i++) {
                HashMap<String, String> hmPlace = list.get(0);
//                double lat = Double.parseDouble(hmPlace.get("lat"));
//                double lng = Double.parseDouble(hmPlace.get("lng"));
                final String name = hmPlace.get("place_name");
                String vicinity = hmPlace.get("vicinity");
                MainActivity.updatePlaceName(name);
                placeName.add(name);
                textView.setText(name + "\n" + vicinity);
//                Toast.makeText(mContext, name, Toast.LENGTH_SHORT).show();

            }
        }
    }// end of the parserTask class

    public class Place_JSON {

        /**
         * Receives a JSONObject and returns a list
         */
        public List<HashMap<String, String>> parse(JSONObject jObject) {

            JSONArray jPlaces = null;
            try {
                /** Retrieves all the elements in the 'places' array */
                jPlaces = jObject.getJSONArray("results");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            /** Invoking getPlaces with the array of json object
             * where each json object represent a place
             */
            return getPlaces(jPlaces);
        }

        private List<HashMap<String, String>> getPlaces(JSONArray jPlaces) {
            int placesCount = jPlaces.length();
            List<HashMap<String, String>> placesList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> place = null;

            /** Taking each place, parses and adds to list object */
            for (int i = 0; i < placesCount; i++) {
                try {
                    /** Call getPlace with place JSON object to parse the place */
                    place = getPlace((JSONObject) jPlaces.get(i));
                    placesList.add(place);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return placesList;
        }

        /**
         * Parsing the Place JSON object
         */
        private HashMap<String, String> getPlace(JSONObject jPlace)
        {

            HashMap<String, String> place = new HashMap<String, String>();
            String placeName = "-NA-";
            String vicinity = "-NA-";
            String latitude = "";
            String longitude = "";
            String reference = "";

            try {
                // Extracting Place name, if available
                if (!jPlace.isNull("name")) {
                    placeName = jPlace.getString("name");
                }

                // Extracting Place Vicinity, if available
                if (!jPlace.isNull("vicinity")) {
                    vicinity = jPlace.getString("vicinity");
                }

                latitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lat");
                longitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lng");
                reference = jPlace.getString("reference");

                place.put("place_name", placeName);
                place.put("vicinity", vicinity);
                place.put("lat", latitude);
                place.put("lng", longitude);
                place.put("reference", reference);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return place;
        }
    }

}