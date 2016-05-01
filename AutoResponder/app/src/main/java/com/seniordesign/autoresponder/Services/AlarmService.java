package com.seniordesign.autoresponder.Services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.sql.Time;
import java.util.Date;
import java.util.Calendar;

/**
 * Created by MarschOSX on 4/27/2016.
 */
public class AlarmService {
    private static final String TAG = "AlarmService";
    private Context context;
    private PendingIntent mAlarmSender;

    public AlarmService(Context context, Class<?> cls) {
        this.context = context;
        mAlarmSender = PendingIntent.getBroadcast(context, 0, new Intent(context, cls), 0);
    }

    public AlarmService(Context context, String action) {
        this.context = context;
        mAlarmSender = PendingIntent.getBroadcast(context, 0, new Intent(action), 0);
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

    public void setEventTime(int hour, int minutes){
        Calendar c = Calendar.getInstance();
        Date d = c.getTime();

        d.setHours(hour);
        d.setMinutes(minutes);

        Date alarmDate = new Date(d.getTime() + (1000 * 60 * 60 * 24));

        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, alarmDate.getTime(), mAlarmSender);
    }

}
