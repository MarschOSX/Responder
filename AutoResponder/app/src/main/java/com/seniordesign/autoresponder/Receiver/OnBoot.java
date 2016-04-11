package com.seniordesign.autoresponder.Receiver;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.seniordesign.autoresponder.Interface.Settings.ParentalControlsSetUp;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;
import com.seniordesign.autoresponder.Services.DrivingDetectionService;

/**
 * Created by Garlan on 4/11/2016.
 */
public class OnBoot extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        DBInstance db = DBProvider.getInstance(false, context);

        //TODO put check permissions status block here

        if (db != null){
            if (db.getParentalControlsToggle()){

            }

            if (db.getDrivingDetectionToggle()){
                // if app does not have Location Permissions
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //TODO put code to notify parent that driving detection service is not enab
                }
                // if app does have permission and service is not running
                else if (!DrivingDetectionService.isRunning(context) && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    context.startService(new Intent(context, DrivingDetectionService.class));
                }
            }
        }
    }
}
