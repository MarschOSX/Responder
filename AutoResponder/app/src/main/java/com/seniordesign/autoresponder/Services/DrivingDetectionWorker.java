package com.seniordesign.autoresponder.Services;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.seniordesign.autoresponder.DataStructures.DrivingDetectionInfo;
import com.seniordesign.autoresponder.DataStructures.LocationRecord;

import java.util.ArrayList;

/**
 * Created by Garlan on 3/9/2016.
 */
public class DrivingDetectionWorker implements Runnable{
    public static final String ACTION_STATUS_UPDATE = "ACTION_STATUS_UPDATE";
    public static final String ACTION_NOTIFY_SHUTDOWN = "ACTION_NOTIFY_SHUTDOWN";

    public static final String TAG = "DrivingDetectionW";
    private LocalBroadcastManager localBroadcastManager;
    private ArrayList<LocationRecord> info;

    public DrivingDetectionWorker(Context context, ArrayList<LocationRecord> info){
        this.info = info;
        this.localBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    public void run(){
        Log.d(TAG, "starting " + info.size());
        int status = determineStatus();

        Log.d(TAG, "status has been decided: " + status);

        if (status >= 0){
            Intent intent = new Intent(DrivingDetectionWorker.ACTION_STATUS_UPDATE);
            intent.putExtra(DrivingDetectionWorker.ACTION_STATUS_UPDATE, status);
            localBroadcastManager.sendBroadcast(intent);
        }
    }

    private int determineStatus(){
        float threshHold = DrivingDetectionInfo.threshHold;
        if (DrivingDetectionInfo.isTesting) threshHold = DrivingDetectionInfo.testingThreshHold;

        return determineStatus_rec(0, 0, 0, threshHold);
    }

    private int determineStatus_rec(int index, float idlePoints, float drivingPoints, float threshHold){
        if(Thread.currentThread().isInterrupted()){
            shutdown();
            return -1;
        }

        Log.d(TAG, index + ", " + idlePoints + ", " + drivingPoints + ", " + threshHold);

        if (index < (info.size()-1) && DrivingDetectionInfo.getSpeed(info.get(index+1), info.get(index)) < threshHold){
            return determineStatus_rec(index + 1, idlePoints + 1, drivingPoints, threshHold);
        }
        else if(index < (info.size()-1) && DrivingDetectionInfo.getSpeed(info.get(index+1), info.get(index)) >= threshHold){
            return determineStatus_rec(index + 1, idlePoints, drivingPoints + 1, threshHold);
        }

        if (drivingPoints > idlePoints){
            return DrivingDetectionService.DRIVING;
        }
        if (idlePoints > drivingPoints){
            return DrivingDetectionService.IDLE;
        }
        else {
            float overallSpeed = DrivingDetectionInfo.getSpeed(info.get(info.size()-1), info.get(0));

            if (overallSpeed >= threshHold) return DrivingDetectionService.DRIVING;
            else  return DrivingDetectionService.IDLE;
        }
    }

    private void shutdown(){
        Log.d(TAG, "interrupt detected, shutting down");

        Intent status = new Intent(DrivingDetectionWorker.ACTION_NOTIFY_SHUTDOWN);

        localBroadcastManager.sendBroadcast(status);
    }
}
