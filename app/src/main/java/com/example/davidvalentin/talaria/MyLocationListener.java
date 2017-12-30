package com.example.davidvalentin.talaria;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by martin.flintham on 12/28/17.
 * Extended by David Valentin
 *
 */

public class MyLocationListener implements LocationListener {

    private static final String TAG = "MyLocationListener";

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, location.getLatitude() + " " + location.getLongitude());
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // information about the signal, i.e. number of satellites
        Log.d(TAG, "onStatusChanged: " + provider + " " + status);
    }
    @Override
    public void onProviderEnabled(String provider) {
        // the user enabled (for example) the GPS
        Log.d(TAG, "onProviderEnabled: " + provider);
    }
    @Override
    public void onProviderDisabled(String provider) {
        // the user disabled (for example) the GPS
        Log.d(TAG, "onProviderDisabled: " + provider);
    }


}