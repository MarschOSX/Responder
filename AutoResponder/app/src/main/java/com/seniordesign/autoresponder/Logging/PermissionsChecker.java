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
    public static boolean checkReceiveSMSPermission(Activity caller, Context context, int RECEIVE_SMS_PERMISSIONS){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            if(caller != null){
                ActivityCompat.requestPermissions(caller, new String[]{Manifest.permission.RECEIVE_SMS}, RECEIVE_SMS_PERMISSIONS);
            }
        }else{
            return true;
        }
        return false;
    }
    public static boolean checkSendSMSPermission(Activity caller, Context context, int SEND_SMS_PERMISSIONS){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if(caller != null){
                ActivityCompat.requestPermissions(caller, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSIONS);
            }
        }else{
            return true;
        }
        return false;
    }
    public static boolean checkReadSMSPermission(Activity caller, Context context, int READ_SMS_PERMISSIONS){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            if(caller != null){
                ActivityCompat.requestPermissions(caller, new String[]{Manifest.permission.READ_SMS}, READ_SMS_PERMISSIONS);
            }
        }else{
            return true;
        }
        return false;
    }
    public static boolean checkReadContactsPermission(){
        return false;
    }
    public static boolean checkReadCalendarPermission(){
        return false;
    }
    public static boolean checkAccessLocationPermission(){
        return false;
    }
    public static boolean checkReceiveMMSPermission(){
        return false;
    }

}
