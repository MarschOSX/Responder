package com.seniordesign.autoresponder.Services;

import android.app.ActivityManager;
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
import com.seniordesign.autoresponder.Interface.Settings.SettingListAdapter;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;

import java.sql.Date;
import java.util.ArrayList;

/**
 * Created by Garlan on 2/28/2016.
 */
public class DrivingDetectionService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    public static final String ACTION_DEBUG_UPDATE = "ACTION_DEBUG_UPDATE";
    private String TAG = "DrivingDetection";

    private final LocalBinder mBinder = new LocalBinder();
    private GoogleApiClient googleApiClient;

    private DrivingDetectionInfo info;
    private Thread worker;
    private Date lastUpdate;
    private DBInstance db;
    private DrivingDetectionService me;
    private  ArrayList<LocationRecord> loadResults;

    private boolean isDriving = false;
    private boolean isTesting = true;
    private boolean shuttingDown = false;

    private int status;
    private final int checkPeriod = 60;

    public static final int IDLE = 0;
    public static final int LOADING_BUFFER = 1;
    public static final int DETERMINING_STATUS = 2;
    public static final int DRIVING = 3;

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
        this.info = new DrivingDetectionInfo(2);

        //register service to receive broadcasts
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DrivingDetectionWorker.ACTION_UPDATE);
        intentFilter.addAction(DrivingDetectionWorker.ACTION_NOTIFY_SHUTDOWN);
        intentFilter.addAction(SettingListAdapter.ACTION_UPDATE_INTERVAL);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, intentFilter);

        status = DrivingDetectionService.IDLE;

        //build location request

        if (checkPlayServices()){
            buildGoogleApiClient();
        }
        else{
            Log.e(TAG, "could not find Google Play Services");
        }

        this.db = DBProvider.getInstance(false, getApplicationContext());
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
        googleApiClient.disconnect();
        if(worker != null) worker.interrupt();
        Log.d(TAG, "is dead");
    }

    public class LocalBinder extends Binder {
        public DrivingDetectionService getService() {
            // Return this instance of DrivingDetection so clients can call public methods
            return DrivingDetectionService.this;
        }
    }

    public boolean isDriving(){
        return isDriving;
    }

    public int getStatus(){
        return this.status;
    }

    private float getSpeed(Location p1, Location p2, long time){
        float distance = p1.distanceTo(p2);

        float inMiles = Math.abs((distance / 1000f) * 0.62f);

        float hours = time / 3600000f;

        if (inMiles / hours < 0.1) return 0;
        else return inMiles / hours;
    }

    private void checkDrivingStatus(){
        //stop location updates so that interval can be increased
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, me);
        googleApiClient.disconnect();

        //allocate a buffer large enough to get a full minute of updates and update the status
        this.status = DrivingDetectionService.LOADING_BUFFER;
        info = new DrivingDetectionInfo(this.checkPeriod);

        // restart the location detection
        if (checkPlayServices()){
            buildGoogleApiClient();
        }
        else{
            Log.e(TAG, "could not find Google Play Services");
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
                            worker = new Thread(new DrivingDetectionWorker(getApplicationContext(), loadResults));
                            worker.start();
                        }
                        else{
                            Log.d(TAG, "allowing driving detection shutdown");
                            LocalBroadcastManager.getInstance(context).unregisterReceiver(messageReceiver);
                        }
                        break;
                    case SettingListAdapter.ACTION_UPDATE_INTERVAL:
                        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, me);
                        googleApiClient.disconnect();
                        Log.d(TAG, "updating interval");
                        if (checkPlayServices()){
                            buildGoogleApiClient();
                        }
                        else{
                            Log.e(TAG, "could not find Google Play Services");
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

        switch (status){
            case DrivingDetectionService.IDLE:
                if (info.getMostRectent().getLocation().getSpeed() >= 20.0f){
                    checkDrivingStatus();
                }
                break;
            case DrivingDetectionService.LOADING_BUFFER:
                if (info.getLocationHistory().size() >= checkPeriod){
                    //stop location updates so that interval can be increased
                    LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, me);
                    googleApiClient.disconnect();

                    loadResults = info.getLocationHistory();
                    info = new DrivingDetectionInfo(2);
                    status = DrivingDetectionService.DETERMINING_STATUS;

                    worker = new Thread(new DrivingDetectionWorker(getApplicationContext(), loadResults));
                    worker.setDaemon(true);
                    worker.start();

                    // restart the location detection
                    if (checkPlayServices()){
                        buildGoogleApiClient();
                    }
                    else{
                        Log.e(TAG, "could not find Google Play Services");
                    }
                }
                break;
            case DrivingDetectionService.DETERMINING_STATUS:
                //do nothing
                break;
            case DrivingDetectionService.DRIVING:
                if (info.getMostRectent().getLocation().getSpeed() < 20.0f ){
                    checkDrivingStatus();
                }
                break;
            default:
                Log.e(TAG, "unknown status encountered: " + status );
                break;
        }

        //Log.d(TAG, " lat = " + location.getLatitude() + " long = " + location.getLongitude() + " speed = " + location.getSpeed());
        //start the worker thread
        /*worker = new Thread(new DrivingDetectionWorker(getApplicationContext(), info));
        worker.setDaemon(true);
        worker.start();*/
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
        int interval = db.getDrivingDetectionInterval() * 60000;

        switch (status){
            case DrivingDetectionService.IDLE:
                if (isTesting) interval /= 60;
                break;

            case DrivingDetectionService.LOADING_BUFFER:
                interval = 1000;
                break;

            case DrivingDetectionService.DETERMINING_STATUS:
                if (isTesting) interval /= 60;
                break;

            case DrivingDetectionService.DRIVING:
                if (isTesting) interval /= 60;
                break;

            default:
                Log.e(TAG, "unknown status encountered: " + status );
                break;
        }


        Log.d(TAG, "setting interval to: " + interval + " ms");
        LocationRequest lr = new LocationRequest();
        lr.setInterval(interval);
        lr.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, lr, this);
    }

    /** creates a new client to connect to the Google Api for this object */
    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

    /** @return the availability of Google Play Services*/
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
        else if (history.size() == 1){
            return history.get(0);
        }
        else {
            LocationRecord p2 = history.get(0);
            LocationRecord p1 = history.get(1);

            p1.getLocation().setSpeed(getSpeed(p1.getLocation(), p2.getLocation(), p2.getDate().getTime() - p1.getDate().getTime()));

            return p1;
        }
    }

    /** @return whether or not this service is running*/
    public static boolean isRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (DrivingDetectionService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
