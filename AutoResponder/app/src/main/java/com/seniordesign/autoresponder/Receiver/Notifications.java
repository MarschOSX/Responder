package com.seniordesign.autoresponder.Receiver;

import android.app.NotificationManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;


/**
 * Created by MarschOSX on 2/17/2016.
 */
public class Notifications extends AppCompatActivity {
    public void Notify(String notificationTitle, String notificationMessage){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        mBuilder.setContentTitle(notificationTitle);
        mBuilder.setContentText(notificationMessage);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, mBuilder.build());
    }
}
