package com.seniordesign.autoresponder.Services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.*;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.seniordesign.autoresponder.DataStructures.Contact;

/**
 * Created by Garlan on 2/28/2016.
 */
public class DrivingDetectionService extends Service {
    private String TAG = "DrivingDetection";
    private final LocalBinder mBinder = new LocalBinder();
    private Thread drivingDetector;
    private float[] testArray = {1.0f, 2.0f, 3.0f};
    private boolean driving = false;
    private boolean shuttingDown = false;

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

        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(DrivingDetection.ACTION_UPDATE);
        intentFilter.addAction(DrivingDetection.ACTION_NOTIFY_SHUTDOWN);

        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, intentFilter);

        drivingDetector = new Thread(new DrivingDetection(getApplicationContext(), testArray));
        drivingDetector.setDaemon(true);
        drivingDetector.start();
        //to start in foreground
       /* Notification notification = new Notification.Builder(context)
                .setSmallIcon(R.drawable.something)
                .setContentText("Content")
                .setContentTitle("Title")
                .getNotification();
        startForeground(17, notification); // Because it can't be zero...*/
    }

    public float[] testing(){
        drivingDetector.interrupt();
        return testArray;
    }

    @Override
    public void onDestroy(){
        shuttingDown = true;
        drivingDetector.interrupt();
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
                    case DrivingDetection.ACTION_UPDATE:
                        testArray = intent.getFloatArrayExtra("test");
                        break;
                    case DrivingDetection.ACTION_NOTIFY_SHUTDOWN:
                        if (!shuttingDown){
                            Log.e(TAG, "unexpected shutdown detected, restarting");
                            testArray = intent.getFloatArrayExtra("state");
                            drivingDetector = new Thread(new DrivingDetection(getApplicationContext(), testArray));
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
}
