package com.seniordesign.autoresponder.Services;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.*;

import com.seniordesign.autoresponder.Interface.LocationOutput;
import com.seniordesign.autoresponder.R;

/**
 * Created by Garlan on 2/28/2016.
 */
public class DrivingDetection extends Service {
    private final LocalBinder mBinder = new LocalBinder();
    private int value;
    private Context context = getApplicationContext();

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

    }

    public class LocalBinder extends Binder {
        DrivingDetection getService() {
            // Return this instance of DrivingDetection so clients can call public methods
            return DrivingDetection.this;
        }
    }
}
