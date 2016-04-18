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

    /***
     * checks if the application has the RECEIVE_SMS Permission, and if not, may generate a permission request
     * @param caller the activity calling this function, or null
     *           if he param is null, no permission request will be generated for the user
     * @param context application context
     * @param requestCode the request code for if a permission request is to be generated, must be > 0
     * @return whether or not the application has this permission
     */
    public static boolean checkReceiveSMSPermission(Activity caller, Context context, int requestCode){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            if(caller != null){
                ActivityCompat.requestPermissions(caller, new String[]{Manifest.permission.RECEIVE_SMS}, requestCode);
            }
        }else{
            return true;
        }
        return false;
    }

    /***
     * checks if the application has the SEND_SMS Permission, and if not, may generate a permission request
     * @param caller the activity calling this function, or null
     *           if he param is null, no permission request will be generated for the user
     * @param context application context
     * @param requestCode the request code for if a permission request is to be generated, must be > 0
     * @return whether or not the application has this permission
     */
    public static boolean checkSendSMSPermission(Activity caller, Context context, int requestCode){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if(caller != null){
                ActivityCompat.requestPermissions(caller, new String[]{Manifest.permission.SEND_SMS}, requestCode);
            }
        }else{
            return true;
        }
        return false;
    }

    /***
     * checks if the application has the READ_SMS Permission, and if not, may generate a permission request
     * @param caller the activity calling this function, or null
     *           if he param is null, no permission request will be generated for the user
     * @param context application context
     * @param requestCode the request code for if a permission request is to be generated, must be > 0
     * @return whether or not the application has this permission
     */
    public static boolean checkReadSMSPermission(Activity caller, Context context, int requestCode){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            if(caller != null){
                ActivityCompat.requestPermissions(caller, new String[]{Manifest.permission.READ_SMS}, requestCode);
            }
        }else{
            return true;
        }
        return false;
    }

    /***
     * checks if the application has the READ_CONTACTS Permission, and if not, may generate a permission request
     * @param caller the activity calling this function, or null
     *           if he param is null, no permission request will be generated for the user
     * @param context application context
     * @param requestCode the request code for if a permission request is to be generated, must be > 0
     * @return whether or not the application has this permission
     */
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

    /***
     * checks if the application has the READ_CALENDAR Permission, and if not, may generate a permission request
     * @param caller the activity calling this function, or null
     *           if he param is null, no permission request will be generated for the user
     * @param context application context
     * @param requestCode the request code for if a permission request is to be generated, must be > 0
     * @return whether or not the application has this permission
     */
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

    /***
     * checks if the application has the ACCESS_FINE_LOCATION Permission, and if not, may generate a permission request
     * @param caller the activity calling this function, or null
     *           if he param is null, no permission request will be generated for the user
     * @param context application context
     * @param requestCode the request code for if a permission request is to be generated, must be > 0
     * @return whether or not the application has this permission
     */
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

    /***
     * checks if the application has the RECEIVE_SMS Permission, and if not, may generate a permission request
     * @param caller the activity calling this function, or null
     *           if he param is null, no permission request will be generated for the user
     * @param context application context
     * @param requestCode the request code for if a permission request is to be generated, must be > 0
     * @return whether or not the application has this permission
     */
    public static boolean checkReceiveMMSPermission(Activity caller, Context context, int requestCode){

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            if(caller != null) ActivityCompat.requestPermissions(caller, new String[]{Manifest.permission.RECEIVE_SMS}, requestCode);

            //TODO PARENTAL CONTROL CHECK AND LOGGINGing

            return false;
        }
        else {
            return true;
        }
    }
}
