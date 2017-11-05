package com.mssample.nirit.mobileschoolsample;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import static android.content.Context.LOCATION_SERVICE;

public class LocationUtility implements ActivityCompat.OnRequestPermissionsResultCallback {

    static final int LOCATION_PERMISSIONS_REQUEST = 1;
    static Context context;

    protected static Location getDeviceLocation(Context c) {
        context = c;

        Location result;


        // to control location updates
        LocationManager lm = (LocationManager) c.getSystemService(LOCATION_SERVICE);
        Location gpsLocation = null;

        if (ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity)c, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSIONS_REQUEST);
        }

        // try to retrieve location using satellite (ACCESS_FINE_LOCATION permission is needed)
        try {
            gpsLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (SecurityException e) {
            Log.d("INN","Error retrieved when trying to get the GPS location - access appears to be disabled.");
        } catch (IllegalArgumentException e) {
            Log.d("INN","Error retrieved when trying to get the GPS location - device has no GPS provider.");
        }

        Location networkLocation = null;

        // try to retrieve location using cell tower and WiFi access points
        try {
            networkLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } catch (SecurityException e) {
            Log.d("INN","Error retrieved when trying to get the network location - access appears to be disabled.");
        } catch (IllegalArgumentException e) {
            Log.d("INN","Error retrieved when trying to get the network location - device has no network provider.");
        }

        if (gpsLocation == null && networkLocation == null) {
            return null;
        }
        else if (gpsLocation != null && networkLocation != null) {
            if (gpsLocation.getTime() > networkLocation.getTime()) result = gpsLocation;
            else result = networkLocation;
        }
        else if (gpsLocation != null) result = gpsLocation;
        else result = networkLocation;


        return result;


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getDeviceLocation(context);

                }
            }
        }
    }
}
