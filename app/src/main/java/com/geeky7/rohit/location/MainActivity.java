package com.geeky7.rohit.location;

import android.Manifest;
import android.app.Activity;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends Activity implements LocationListener {
    private static String latPlacesS,lonPlacesS;
    TextView latitude,longitude,address;
    static TextView places;
    static EditText latPlaces,lonPlaces,radiusPlaces;
    Double lat,lon;
    Button placesB;
    final int i = 0;
    LocationManager locationManager;
    String provider;
    Location location;
    static long time = 1;
    static long distance = 1;
    Geocoder geocoder;
    List<Address> addresses;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        latitude = (TextView) findViewById(R.id.lat);
        longitude = (TextView) findViewById(R.id.lon);
        address = (TextView) findViewById(R.id.address);
        places = (TextView) findViewById(R.id.places);
        placesB = (Button) findViewById(R.id.placesB);
        latPlaces = (EditText)findViewById(R.id.latPlaces);
        lonPlaces = (EditText)findViewById(R.id.lonPlaces);
        radiusPlaces = (EditText)findViewById(R.id.radiusPlaces);

        geocoder = new Geocoder(this, Locale.getDefault());

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                i);

        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
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
//            addresses = geocoder.getFromLocation(-34.9381429, 138.5002845, 1);
                //34.9381429,138.5002845 home
            } catch (IOException e) {
                e.printStackTrace();
            }
            String address1 = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
            address.setText(address1 + " " + city + "\n" + state + " " + postalCode);
        }
            placesB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), Places.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("lat", lat + "");
                    bundle.putString("lon", lon + "");
                    intent.putExtras(bundle);
                    startActivity(intent);
//                    places.setText("Nothing Found");
                    ArrayList<String> arrayList = new ArrayList<String>();
                    arrayList = Places.placeName;
                    if (arrayList.size() > 0) {
                        String name = arrayList.get(0) + "";
                    }
                }
            });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case i: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                return;
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude.setText(location.getLatitude()+"");
        longitude.setText(location.getLongitude()+"");
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//            addresses = geocoder.getFromLocation(-34.9381429, 138.5002845, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public static String getMethodName(final int depth)
    {
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        return ste[1+depth].getMethodName();
    }
    public static void updatePlaceName(String name){
        places.setText(name);
    }
}
