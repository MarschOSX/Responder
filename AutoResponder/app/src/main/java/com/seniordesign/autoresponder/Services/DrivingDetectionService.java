package com.seniordesign.autoresponder.Services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.*;
import android.support.v4.app.NotificationCompat;
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
import com.seniordesign.autoresponder.Permissions.PermissionsChecker;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;
import com.seniordesign.autoresponder.R;

import java.sql.Date;
import java.util.ArrayList;

/**
 * Created by Garlan on 2/28/2016.
 */

//icon for notification created by:<div>Icons made by <a href="http://www.freepik.com" title="Freepik">Freepik</a> from <a href="http://www.flaticon.com" title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></div>

public class DrivingDetectionService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    public static final String ACTION_DEBUG_UPDATE = "ACTION_DEBUG_UPDATE";
    public static int notificationID = 42;
    private String TAG = "DrivingDetectionS";

    private final LocalBinder mBinder = new LocalBinder();
    private GoogleApiClient googleApiClient;

    private DrivingDetectionInfo info;
    private Thread worker;
    private DBInstance db;
    private DrivingDetectionService me;
    private  ArrayList<LocationRecord> loadResults;

    private boolean isDriving = false;
    private boolean shuttingDown = false;

    private int status;
    private final int checkPeriod = 30;
    private final int timeout = 10;
    private int shutdownCount;

    public static final int IDLE = 0;
    public static final int LOADING_BUFFER = 1;
    public static final int DETERMINING_STATUS = 2;
    public static final int DRIVING = 3;

    @Override
    public int onStartCommand(Intent intent, int flags, int startID){
        //to start in foreground
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.transport)
                .setContentText("Current Status: not driving")
                .setContentTitle("Driving Detection");
        Notification notification = mBuilder.build();
        startForeground(DrivingDetectionService.notificationID, notification);


        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent){
        Log.d(TAG, "passing binder");
        return mBinder;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(TAG, "is created");

        //initialize a 2 element buffer for storing current and last location
        this.info = new DrivingDetectionInfo(2);

        //register service to receive broadcasts
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DrivingDetectionWorker.ACTION_STATUS_UPDATE);
        intentFilter.addAction(DrivingDetectionWorker.ACTION_NOTIFY_SHUTDOWN);
        intentFilter.addAction(SettingListAdapter.ACTION_UPDATE_INTERVAL);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, intentFilter);

        //TODO make it so that service status is LOAD_BUFFER -> DETERMINE_STATUS
        //set the status to the default of IDLE/not driving
        status = DrivingDetectionService.IDLE;

        shutdownCount = 0;

        //start location detection
        if (checkPlayServices()){
            buildGoogleApiClient();
        }
        else{
            Log.e(TAG, "could not find Google Play Services");
        }

        //load connection to DB
        this.db = DBProvider.getInstance(false, getApplicationContext());
    }

    @Override
    public void onDestroy(){

        stopForeground(true);
        //set shut down to true so worker thread will not restart
        shuttingDown = true;

        //close out location services
        if (googleApiClient != null && googleApiClient.isConnected()){
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }

        //if worker is running, stop it
        if(worker != null) worker.interrupt();

        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(messageReceiver);

        Log.d(TAG, "is dead");
    }

    public class LocalBinder extends Binder {
        public DrivingDetectionService getService() {
            // Return this instance of DrivingDetection so clients can call public methods
            return DrivingDetectionService.this;
        }
    }

    /** @return driving stats */
    public boolean isDriving(){
        return isDriving;
    }

    /** @return process status of the service*/
    public int getStatus(){
        return this.status;
    }



    /** initializes the process of checking the driving status*/
    private void checkDrivingStatus(){

        Log.d(TAG, "checking status");
        //stop location updates so that interval can be increased
        //LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, me);
        googleApiClient.disconnect();

        //allocate a buffer large enough to get checkPeriod x 1 second worth of updates and update the status
        this.status = DrivingDetectionService.LOADING_BUFFER;
        info = new DrivingDetectionInfo(this.checkPeriod);
        updateNotification(true);

        // restart the location detection
        if (checkPlayServices()){
            buildGoogleApiClient();
        }
        else{
            Log.e(TAG, "could not find Google Play Services");
        }
    }


    /** local broadcast handler*/
    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null){
                switch (intent.getAction()){

                    //TODO improve this
                    case DrivingDetectionWorker.ACTION_STATUS_UPDATE:
                        Log.d(TAG,"status received: " + intent.getIntExtra(DrivingDetectionWorker.ACTION_STATUS_UPDATE, 0));

                        status = intent.getIntExtra(DrivingDetectionWorker.ACTION_STATUS_UPDATE, 0);
                        Log.d(TAG,"status received: " + status);

                        isDriving = (status == DrivingDetectionService.DRIVING);
                        updateNotification(true);
                        break;

                    //handles when the notification of the worker thread being interuppted
                    case DrivingDetectionWorker.ACTION_NOTIFY_SHUTDOWN:
                        //if the thread is not supposed to shutdown, restart it
                        if (!shuttingDown && shutdownCount++ < timeout){
                            Log.e(TAG, "unexpected shutdown detected, restarting");
                            worker = new Thread(new DrivingDetectionWorker(getApplicationContext(), loadResults));
                            worker.start();
                        }
                        else {
                            if (shuttingDown) {
                                Log.d(TAG, "allowing driving detection shutdown");
                                shutdownCount = 0;
                                //LocalBroadcastManager.getInstance(context).unregisterReceiver(messageReceiver);
                            }
                            else {
                                Log.e(TAG, "TIME OUT ERROR: worker thread was repeatedly shut down");
                                shutdownCount = 0;
                            }
                        }
                        break;

                    //handles broadcast from Settings to update the driving detection interval
                    case SettingListAdapter.ACTION_UPDATE_INTERVAL:
                        //check to make sure you are not currently loading the buffer
                        if(status != DrivingDetectionService.LOADING_BUFFER){

                            //refresh the location detection service which on restart grabs the new interval
                            //LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, me);
                            googleApiClient.disconnect();
                            Log.d(TAG, "updating interval");
                            if (checkPlayServices()){
                                buildGoogleApiClient();
                            }
                            else{
                                Log.e(TAG, "could not find Google Play Services");
                            }
                        }
                        break;

                    default:
                        Log.d(TAG, "unknown broadcast received");
                }
            }
            else Log.e(TAG, "broadcast with null intent received");
        }
    };

    private void updateNotification(boolean permissionEnabled){
        String msg = "Current Status: ";

        if (permissionEnabled) {
            if (isDriving) {
                msg += "driving";
            } else {
                msg += "not driving";
            }

            if (status == DrivingDetectionService.DETERMINING_STATUS || status == DrivingDetectionService.LOADING_BUFFER) {
                msg += "...updating";
            }
        }
        else{
            msg += "location permissions have been denied by user";
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.transport)
                .setContentText(msg)
                .setContentTitle("Driving Detection");
        Notification notification = mBuilder.build();

        notificationManager.notify(DrivingDetectionService.notificationID, notification);
    }

    ///////////////////////////////////
    //location & connection functions//
    ///////////////////////////////////
    @Override
    public void onLocationChanged(Location location) {
        //check and make sure service has not been disabled (safety check)
        //prevents "rouge" service
        if(db != null && !db.getDrivingDetectionToggle()){
            this.stopSelf();
            return;
        }

        float threshHold = DrivingDetectionInfo.threshHold;
        ArrayList<LocationRecord> buffer = info.getLocationHistory();

        if (DrivingDetectionInfo.isTesting) threshHold = DrivingDetectionInfo.testingThreshHold;

        //add the location to the buffer
        info.addToHistory(new LocationRecord(location, new Date(System.currentTimeMillis())));

        //notify Location output of an update
        Intent intent = new Intent(DrivingDetectionService.ACTION_DEBUG_UPDATE);
        intent.putExtra("Status", this.status);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

        switch (this.status){
            case DrivingDetectionService.IDLE:
                Log.d(TAG, "IDLE reached");
                //if threshold exceed, perform a more intensive driving status check
                if(buffer.size() > 1){
                    Log.d(TAG,DrivingDetectionInfo.getSpeed(buffer.get(1), buffer.get(0)) + " : " + threshHold);
                }

                if (buffer.size() > 1 && DrivingDetectionInfo.getSpeed(buffer.get(1), buffer.get(0)) >= threshHold) {
                    Log.d(TAG, "IDLE threshHold exceeded");
                    checkDrivingStatus();
                }
                break;
            case DrivingDetectionService.LOADING_BUFFER:
                Log.d(TAG, "LOADING_BUFFER reached");
                //once the buffer has been filled, remove that buffer and pass it to worker thread
                if (info.getLocationHistory().size() >= checkPeriod){

                    //stop location updates so that interval can be increased
                    //LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, me);
                    googleApiClient.disconnect();

                    //update the service status
                    status = DrivingDetectionService.DETERMINING_STATUS;

                    //segregate the buffer so that its not modified while worker thread is using it
                    loadResults = info.getLocationHistory();
                    info = new DrivingDetectionInfo(2);

                    //pass the bufferResult to the worker and start the thread
                    //once the worker has made a determination it will send a broadcast with the DRIVING/IDLE status
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
                Log.d(TAG, "DETERMINING_STATUS reached");
                //do nothing
                break;
            case DrivingDetectionService.DRIVING:
                Log.d(TAG, "DRIVING reached");
                //if threshold no longer exceeded, perform a more intensive driving status check
                if (buffer.size() > 1 && DrivingDetectionInfo.getSpeed(buffer.get(1), buffer.get(0)) < threshHold){
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
                if (DrivingDetectionInfo.isTesting) interval /= 60;
                break;

            case DrivingDetectionService.LOADING_BUFFER:
                interval = 1000;
                break;

            case DrivingDetectionService.DETERMINING_STATUS:
                if (DrivingDetectionInfo.isTesting) interval /= 60;
                break;

            case DrivingDetectionService.DRIVING:
                if (DrivingDetectionInfo.isTesting) interval /= 60;
                break;

            default:
                Log.e(TAG, "unknown status encountered: " + status );
                break;
        }

        //once connected to GooglePS, start location request services
        Log.d(TAG, "setting interval to: " + interval + " ms");
        LocationRequest lr = new LocationRequest();
        lr.setInterval(interval);
        lr.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (PermissionsChecker.checkAccessLocationPermission(null, getApplicationContext(), 1)){
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, lr, this);
        }
        else {
            updateNotification(false);
            this.stopSelf();
        }
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

            p1.getLocation().setSpeed(DrivingDetectionInfo.getSpeed(p1, p2));

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
