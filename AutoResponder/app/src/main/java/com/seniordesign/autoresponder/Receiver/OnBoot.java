package com.seniordesign.autoresponder.Receiver;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.seniordesign.autoresponder.Interface.Settings.ParentalControlsSetUp;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;
import com.seniordesign.autoresponder.Services.DrivingDetectionService;
import com.seniordesign.autoresponder.Services.ParentalControlsWatcher;

/**
 * Created by Garlan on 4/11/2016.
 */
public class OnBoot extends BroadcastReceiver{
    public static final String TAG = "OnBoot";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
            Log.e(TAG, "Android OS was restarted! checking AutoResponder Database");
            DBInstance db = DBProvider.getInstance(false, context);
            if (db != null){
                Log.e(TAG, "Database is not null! Attempting to resume services that were running");
                if (db.getParentalControlsToggle()){
                    Log.e(TAG, "Parental Controls Was Running! Restarting Service");
                    if (!ParentalControlsWatcher.isRunning(context)) {
                        context.startService(new Intent(context, ParentalControlsWatcher.class));
                        Log.e(TAG, "Parental Controls Service was started");
                    }
                }
                if (db.getDrivingDetectionToggle()){
                    Log.e(TAG, "Driving Detection Was Running! Restarting Service");
                    if (!DrivingDetectionService.isRunning(context)) {
                        context.startService(new Intent(context, DrivingDetectionService.class));
                        Log.e(TAG, "Driving Detection Service was started");
                    }
                }
            }
        }
    }
}
