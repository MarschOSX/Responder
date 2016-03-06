package com.seniordesign.autoresponder.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.util.Log;

/**
 * Created by Garlan on 2/28/2016.
 */
public class DrivingDetectionService extends Service {
    private String TAG = "DrivingDetection";
    private final LocalBinder mBinder = new LocalBinder();
    private int value;
    private Context context;
    private Integer[] array = {1, 2, 3};

    @Override
    public int onStartCommand(Intent intent, int flags, int startID){


        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent){
        this.context = getApplicationContext();
        return mBinder;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(TAG, "is created");
        //to start in foreground
       /* Notification notification = new Notification.Builder(context)
                .setSmallIcon(R.drawable.something)
                .setContentText("Content")
                .setContentTitle("Title")
                .getNotification();
        startForeground(17, notification); // Because it can't be zero...*/



    }

    public Integer[] testing(){
        array[0]++;
        array[1]++;
        array[2]++;

        return array;
    }

    @Override
    public void onDestroy(){
        Log.d(TAG, "is dead");
    }

    public class LocalBinder extends Binder {
        public DrivingDetectionService getService() {
            // Return this instance of DrivingDetection so clients can call public methods
            return DrivingDetectionService.this;
        }
    }
}
