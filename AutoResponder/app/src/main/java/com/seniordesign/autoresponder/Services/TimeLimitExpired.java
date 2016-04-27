package com.seniordesign.autoresponder.Services;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;

/**
 * Created by MarschOSX on 4/27/2016.
 */
public class TimeLimitExpired extends BroadcastReceiver {
    public static int AutoResponseNotificationID = 24;
    private DBInstance db;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            db = DBProvider.getInstance(false, context);
            db.setResponseToggle(false);
            Log.v("TimeLimitExpired", "Successful setResponseToggle in DB");
        }catch (NullPointerException e){
            //failed to change database
            Log.v("TimeLimitExpired", "Failed to setResponseToggle DB");
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(AutoResponseNotificationID);
    }
}