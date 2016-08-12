package com.geeky7.rohit.location.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.geeky7.rohit.location.Main;
import com.geeky7.rohit.location.R;
import com.geeky7.rohit.location.service.BackgroundService;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity implements LocationListener {
    static Activity thisActivity = null;
    static MainActivity mainActivity;

    private static String latPlacesS,lonPlacesS;
    private Double lat,lon;
    private String provider;
    private final int permissionVariable = 0;
    private static long time = 1;
    private static long distance = 1;
    private static int NOTIFICATION_ID = 1;

    TextView latitude,longitude,address;
    static TextView places;
    static EditText latPlaces,lonPlaces,radiusPlaces;
    Button placesB;


    LocationManager locationManager;
    Location location;
    Geocoder geocoder;
    List<Address> addresses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        thisActivity = this;
        mainActivity = new MainActivity();

        findViews();
        checkPermission();
        startService();

        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        geocoder = new Geocoder(this, Locale.getDefault());

        boolean enabled = openLocationSettings(service);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, time, distance, this);

        if (locationManager != null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                lat = location.getLatitude();
                lon = location.getLongitude();
                latitude.setText(location.getLatitude() + "");
                longitude.setText(location.getLongitude() + "");
            }
        }
        if (location == null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, time, distance, this);
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    latitude.setText(location.getLatitude() + "");
                    longitude.setText(location.getLongitude() + "");
                }
            }
        }
        if (enabled) {
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            setAddress();
        }
            placesB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    ArrayList<String> arrayList = new ArrayList<String>();
                    Intent intent = new Intent(getApplicationContext(), Places.class);
                    Bundle bundle = new Bundle();

                    bundle.putString("lat", lat + "");
                    bundle.putString("lon", lon + "");

                    intent.putExtras(bundle);

                    startActivity(intent);

                    /*arrayList = Places.placeName;

                    if (arrayList.size() > 0) {
                        String name = arrayList.get(0) + "";
                    }*/
                }
            });
    }

    private void startService() {
        Intent serviceIntent = new Intent(MainActivity.this,BackgroundService.class);
        startService(serviceIntent);
    }

    private boolean openLocationSettings(LocationManager service) {
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        return enabled;
    }

    private void checkPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                permissionVariable);
    }

    private void findViews() {
        latitude = (TextView) findViewById(R.id.lat);
        longitude = (TextView) findViewById(R.id.lon);
        address = (TextView) findViewById(R.id.address);
        places = (TextView) findViewById(R.id.places);
        placesB = (Button) findViewById(R.id.placesB);
        latPlaces = (EditText)findViewById(R.id.latPlaces);
        lonPlaces = (EditText)findViewById(R.id.lonPlaces);
        radiusPlaces = (EditText)findViewById(R.id.radiusPlaces);
        latPlaces.setSelection(latPlaces.length());
        lonPlaces.setSelection(lonPlaces.length());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case permissionVariable: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                return;
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        /*Intent intent = new Intent(getApplicationContext(), Places.class);
        Bundle bundle = new Bundle();
        bundle.putString("lat", location.getLatitude() + "");
        bundle.putString("lon", location.getLongitude() + "");
        intent.putExtras(bundle);
        startActivity(intent);*/

        latitude.setText(location.getLatitude() + "");
        longitude.setText(location.getLongitude() + "");

        Main.showToast(getApplicationContext(), "NewCoordinates: "+location.getLatitude()+"\n"+location.getLongitude());
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setAddress();
    }

    private void setAddress() {
        String address1 = addresses.get(0).getAddressLine(0);
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();
        address.setText(address1 + " " + city + "\n" + state + " " + postalCode);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    public static void updatePlaceName(String name){
        places.setText(name);
        Main.showToast(thisActivity, name);
        new MainActivity().createNotification(name, name, thisActivity);

    }
    public void createNotification(String contentTitle, String contentText,Context context) {

        //Build the notification using Notification.Builder
        Notification.Builder builder = new Notification.Builder(context)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setAutoCancel(true)
                .setContentTitle(contentTitle)
                .setContentText(contentText);
        //Show the notification
        NotificationManager mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
