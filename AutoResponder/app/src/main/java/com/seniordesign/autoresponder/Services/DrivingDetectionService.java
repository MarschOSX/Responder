package com.seniordesign.autoresponder.Services;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.util.Log;

/**
 * Created by Garlan on 2/28/2016.
 */
public class DrivingDetectionService extends Service {
    private String TAG = "DrivingDetection";
    private final LocalBinder mBinder = new LocalBinder();
    private Thread drivingdetector;

    private Float[] testArray = {1.0f, 2.0f, 3.0f};

    @Override
    public int onStartCommand(Intent intent, int flags, int startID){
        //work();

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent){
        return mBinder;
    }

    private void work(){
        try{
            Thread.sleep(500);
        }
        catch (InterruptedException e){
            Log.e(TAG, "sleep was interrupted");
        }
        /*Calendar c = Calendar.getInstance();
        int start = c.get(Calendar.SECOND);
        int last = start;
        int current = start;
        while(current < start + 1){
            c = Calendar.getInstance();
            current = c.get(Calendar.SECOND);
            if(current > last) {
                Log.d(TAG, (current - start) + "...");
                last = current;
            }
        }*/

        testArray[0] += 0.5f;
        testArray[1] += 0.5f;
        testArray[2] += 0.5f;

        work();
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

    public Float[] testing(){
        return testArray;
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