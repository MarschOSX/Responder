package com.seniordesign.autoresponder.Services;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.seniordesign.autoresponder.DataStructures.DrivingDetectionInfo;
import com.seniordesign.autoresponder.DataStructures.LocationRecord;

import java.util.ArrayList;

/**
 * Created by Garlan on 3/9/2016.
 */
public class DrivingDetectionWorker implements Runnable{
    public static final String ACTION_UPDATE = "ACTION_UPDATE";
    public static final String ACTION_NOTIFY_SHUTDOWN = "ACTION_NOTIFY_SHUTDOWN";

    private final String TAG = "DrivingDetection";
    private LocalBroadcastManager localBroadcastManager;
    private ArrayList<LocationRecord> info;

    private float[] array;
    private int i = 0;

    public DrivingDetectionWorker(Context context, ArrayList<LocationRecord> info){
        this.info = info;
        this.localBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    public void run(){

        try{
            Thread.sleep(1000);
        }
        catch (InterruptedException e){
            prepareShutDown();
            return;
        }

        Intent intent = new Intent(DrivingDetectionWorker.ACTION_UPDATE);

        for (int k = 0; k < array.length; k++){
                array[k]++;
        }

        intent.putExtra("test", array);

        localBroadcastManager.sendBroadcast(intent);

        Log.d(TAG, i++ + "...");

        if(Thread.currentThread().isInterrupted()){
            prepareShutDown();
            return;
        }

        run();
    }

    private void prepareShutDown(){
        Log.d(TAG, "interrupt detected, shutting down");

        Intent status = new Intent(DrivingDetectionWorker.ACTION_NOTIFY_SHUTDOWN);
        status.putExtra("state", array);

        localBroadcastManager.sendBroadcast(status);
    }
}
