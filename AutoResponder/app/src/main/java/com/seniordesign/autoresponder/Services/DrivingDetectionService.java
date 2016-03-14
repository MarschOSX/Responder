package com.seniordesign.autoresponder.Services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.*;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.seniordesign.autoresponder.DataStructures.DrivingDetectionInfo;
import com.seniordesign.autoresponder.DataStructures.LocationRecord;

import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;

/**
 * Created by Garlan on 2/28/2016.
 */
public class DrivingDetectionService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private final LocalBinder mBinder = new LocalBinder();
    public static final String ACTION_DEBUG_UPDATE = "ACTION_DEBUG_UPDATE";
    private String TAG = "DrivingDetection";
    private Thread drivingDetector;
    private boolean isDriving = false;
    private boolean shuttingDown = false;
    private DrivingDetectionInfo info;
    private Date lastUpdate;
    private GoogleApiClient googleApiClient;

    @Override
    public int onStartCommand(Intent intent, int flags, int startID){

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent){
        return mBinder;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(TAG, "is created");

        //initialize data structures
        this.info = new DrivingDetectionInfo(40);

        //register service to receive broadcasts
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DrivingDetectionWorker.ACTION_UPDATE);
        intentFilter.addAction(DrivingDetectionWorker.ACTION_NOTIFY_SHUTDOWN);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, intentFilter);

        //build location request

        if (checkPlayServices()){
            buildGoogleApiClient();
        }
        else{
            Log.e(TAG, "could not find Google Play Services");
        }


        //to start in foreground
       /* Notification notification = new Notification.Builder(context)
                .setSmallIcon(R.drawable.something)
                .setContentText("Content")
                .setContentTitle("Title")
                .getNotification();
        startForeground(17, notification); // Because it can't be zero...*/
    }

    @Override
    public void onDestroy(){
        shuttingDown = true;
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        if(drivingDetector != null) drivingDetector.interrupt();
        Log.d(TAG, "is dead");
    }

    public class LocalBinder extends Binder {
        public DrivingDetectionService getService() {
            // Return this instance of DrivingDetection so clients can call public methods
            return DrivingDetectionService.this;
        }
    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null){
                switch (intent.getAction()){
                    case DrivingDetectionWorker.ACTION_UPDATE:
                        break;
                    case DrivingDetectionWorker.ACTION_NOTIFY_SHUTDOWN:
                        if (!shuttingDown){
                            Log.e(TAG, "unexpected shutdown detected, restarting");
                            drivingDetector = new Thread(new DrivingDetectionWorker(getApplicationContext(), info));
                            drivingDetector.start();
                        }
                        else{
                            Log.d(TAG, "allowing driving detection shutdown");
                            LocalBroadcastManager.getInstance(context).unregisterReceiver(messageReceiver);
                        }
                        break;
                    default:
                        Log.d(TAG, "unknown broadcast received");
                }
            }
            else Log.e(TAG, "broadcast with null intent received");
        }
    };

    ///////////////////////////////////
    //location & connection functions//
    ///////////////////////////////////
    @Override
    public void onLocationChanged(Location location) {
        info.addToHistory(new LocationRecord(location, new Date(System.currentTimeMillis())));
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(DrivingDetectionService.ACTION_DEBUG_UPDATE));
        Log.d(TAG, " lat = " + location.getLatitude() + " long = " + location.getLongitude() + " speed = " + location.getSpeed());
        //start the worker thread
        /*drivingDetector = new Thread(new DrivingDetectionWorker(getApplicationContext(), info));
        drivingDetector.setDaemon(true);
        drivingDetector.start();*/
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int result) {
        Log.i(TAG, "Connection suspended: int result = "
                + result);
    }

    @Override
    public void onConnected(Bundle arg0) {

        LocationRequest lr = new LocationRequest();
        lr.setInterval(1000);
        lr.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, lr, this);
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        return resultCode == ConnectionResult.SUCCESS;
    }

    //////////////////////////
    //debug & demo functions//
    //////////////////////////

    public LocationRecord getCurrentLocation(){
        ArrayList<LocationRecord> history = this.info.getLocationHistory();

        if(history.size() == 0) return null;
        else return history.get(0);
    }
}
