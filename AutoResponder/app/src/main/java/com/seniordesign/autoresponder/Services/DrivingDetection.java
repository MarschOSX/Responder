package com.seniordesign.autoresponder.Services;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.util.Log;

import com.seniordesign.autoresponder.Interface.LocationOutput;
import com.seniordesign.autoresponder.R;

/**
 * Created by Garlan on 2/28/2016.
 */
public class DrivingDetection extends Service {
    private String TAG = "DrivingDetection";
    private final LocalBinder mBinder = new LocalBinder();
    private int value;
    private Context context;

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
        Integer[] array = {1, 2, 3};
        return array;
    }

    @Override
    public void onDestroy(){
        Log.d(TAG, "is dead");
    }

    public class LocalBinder extends Binder {
        public DrivingDetection getService() {
            // Return this instance of DrivingDetection so clients can call public methods
            return DrivingDetection.this;
        }
    }
}
