package com.seniordesign.autoresponder.Services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by MarschOSX on 4/27/2016.
 */
public class AlarmService {
    private Context context;
    private PendingIntent mAlarmSender;
    public AlarmService(Context context) {
        this.context = context;
        mAlarmSender = PendingIntent.getBroadcast(context, 0, new Intent(context, TimeLimitExpired.class), 0);
    }

    public void setTimeLimitCountdown(int timeLimitInSeconds){
        //Set the alarm to 10 seconds from now
        Log.v("AlarmService", "Set the countdown to " + timeLimitInSeconds/3600 +" hour(s) from now...");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, timeLimitInSeconds);
        long firstTime = c.getTimeInMillis();
        // Schedule the alarm!
        Log.v("AlarmService", "Set the countdown!");
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, firstTime, mAlarmSender);
        Log.v("AlarmService", "The countdown is set!");
    }
}
