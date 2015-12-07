package com.seniordesign.autoresponder.Receiver;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by gabowser on 12/6/15.
 */
public class Locator {
    public static final String TAG = "Locator";
    private Context context;
    private LocationManager locationManager;
    //private LocationListener locationListener;
    private ArrayList<Location> locationList;
    private Location currentLocation;

    public Locator (Context context){
        this.locationList = new ArrayList<>();
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.currentLocation = null;

        /*this.locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                //makeUseOfNewLocation(location);
                locationList.add(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };*/

        //PackageManager pm = context.getPackageManager();

        //first we must check the permissions to ensure no security error is thrown
        /*if (pm.checkPermission("ACCESS_FINE_LOCATION", context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "permission to access location granted");
            this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        } else {
            Log.e(TAG, "ERROR: permission to access location denied");
        }*/
    }

    public Location getCurrentLocation(){
        Criteria c = new Criteria();
        c.setPowerRequirement(Criteria.NO_REQUIREMENT);
        c.setAccuracy(Criteria.ACCURACY_FINE);


        String bestProvider = locationManager.getBestProvider(c, true);
        Log.d(TAG, "the best provider is: " + bestProvider);

        PackageManager pm = this.context.getPackageManager();

       this.currentLocation = null;
        //first we must check the permissions to ensure no security error is thrown
        if (pm.checkPermission("android.permission.ACCESS_FINE_LOCATION", context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "permission to access location granted");
            locationManager.requestSingleUpdate(
                    bestProvider,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            updateCurrentLocation(location);
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
                    },
                    null);

            //TODO MAKE LESS POWER INTENSIVE
            return  this.currentLocation;
        }
        else {
            Log.e(TAG, "ERROR: permission to access location denied");
        }

        return currentLocation;
    }

    private void updateCurrentLocation (Location loc){
        this.currentLocation = loc;
    }
}
