package com.seniordesign.autoresponder.Logging;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by MarschOSX on 4/13/2016.
 */
public class PermissionsChecker {
    public static boolean checkReceiveSMSPermission(){
        return false;
    }
    public static boolean checkSendSMSPermission(){
        return false;
    }
    public static boolean checkReadSMSPermission(){
        return false;
    }
    public static boolean checkReadContactsPermission(Activity caller, Context context, int requestCode){

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if(caller != null) ActivityCompat.requestPermissions(caller, new String[]{Manifest.permission.READ_CONTACTS}, requestCode);
            //TODO PARENTAL CONTROL CHECK AND LOGGING

            return false;
        }
        else {
            return true;
        }
    }
    public static boolean checkReadCalendarPermission(Activity caller, Context context, int requestCode){

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            if(caller != null) ActivityCompat.requestPermissions(caller, new String[]{Manifest.permission.READ_CALENDAR}, requestCode);
            //TODO PARENTAL CONTROL CHECK AND LOGGING

            return false;
        }
        else {
            return true;
        }
    }
    public static boolean checkAccessLocationPermission(Activity caller, Context context, int requestCode){

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(caller != null) ActivityCompat.requestPermissions(caller, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, requestCode);
            //TODO PARENTAL CONTROL CHECK AND LOGGING

            return false;
        }
        else {
            return true;
        }
    }
    public static boolean checkReceiveMMSPermission(Activity caller, Context context, int requestCode){

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            if(caller != null) ActivityCompat.requestPermissions(caller, new String[]{Manifest.permission.RECEIVE_SMS}, requestCode);

            //TODO PARENTAL CONTROL CHECK AND LOGGING

            return false;
        }
        else {
            return true;
        }
    }
}
