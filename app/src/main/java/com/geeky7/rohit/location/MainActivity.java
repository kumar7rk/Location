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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MainActivity extends Activity implements LocationListener {
    TextView latitude,longitude,address;
    Double lat,lon;
    Button places;
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
        latitude = (TextView)findViewById(R.id.lat);
        longitude = (TextView)findViewById(R.id.lon);
        address = (TextView)findViewById(R.id.address);
        places = (Button)findViewById(R.id.places);

        geocoder = new Geocoder(this, Locale.getDefault());

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                i);

        /*LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }*/
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,time,distance, this);
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    lat = location.getLatitude();
                    lon = location.getLongitude();
                    latitude.setText(location.getLatitude()+"");
                    longitude.setText(location.getLongitude()+"");
                }
            }
        if(location==null){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, time, distance, this);
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {

                    latitude.setText(location.getLatitude() + "");
                    longitude.setText(location.getLongitude() + "");
                }
            }
        }
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String address1 = addresses.get(0).getAddressLine(0);
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();
        address.setText(address1 + " \n" + city + "\n" + state + " " + postalCode + "\n" + knownName);
        places.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Places.class);
                Bundle bundle = new Bundle();
                bundle.putString("lat", lat + "");
                bundle.putString("lon",lon+"");
                intent.putExtras(bundle);
//                intent.putExtra("lon", lon + "");
                Log.v("MainActivity",lat+","+lon);
                startActivity(intent);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude.setText(location.getLatitude()+"");
        longitude.setText(location.getLongitude()+"");
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String address1 = addresses.get(0).getAddressLine(0);
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();
        address.setText(address1 + " \n" + city + "\n" + state + " " + postalCode + "\n" + knownName);
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

    /*@Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        Toast.makeText(getApplicationContext(),getMethodName(2), Toast.LENGTH_SHORT).show();
    }*/

    public static String getMethodName(final int depth)
    {
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        return ste[1+depth].getMethodName();
    }
}
