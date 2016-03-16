package com.seniordesign.autoresponder.Services;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.widget.Switch;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;
import com.seniordesign.autoresponder.R;
import java.util.concurrent.TimeUnit;

/**
 * Created by MarschOSX on 3/16/2016.
 */


/*

  //Thread timeLimitChecker = new Thread(new TimeLimitChecker(getApplicationContext()));
                    //timeLimitChecker.setDaemon(true);
                    //timeLimitChecker.start();
                    //android.util.Log.i(TAG, "TimeLimit Thread started!");
 */
public class TimeLimitChecker extends AppCompatActivity implements Runnable{
    private DBInstance db;
    Context context;
    private Switch mResponseToggle;
    public static final String TAG = "TimeLimitChecker";


    public TimeLimitChecker(Context c){
        context = c;
        db = DBProvider.getInstance(false, context);
    }


    public void run(){
        try{
            //android.util.Log.v(TAG, "Thread is live checking TimeLimit!");
            if(db.getResponseToggle()){
                if(db.getTimeLimit() != 100) { //If == 100, then it is assumed to be indefinite
                    //android.util.Log.v(TAG, "Time Limit is not indefinite!");
                    long timeLimitInMilliseconds = Long.valueOf(db.getTimeLimit() * 3600000);
                    //android.util.Log.e(TAG, "The set TimeLimit in milliseconds is " + timeLimitInMilliseconds);
                    long currentTime = System.currentTimeMillis();
                    //android.util.Log.e(TAG, "The CurrentTime of the system in milliseconds is " + currentTime);
                    long timeToggleWasSet = db.getTimeResponseToggleSet();
                    //android.util.Log.e(TAG, "TimeToggleWasSet in milliseconds is " + timeToggleWasSet);
                    //if the current system time is greater than or equal to the time the app was activated + the time limit
                    //turn the app off
                    if (currentTime >= (timeToggleWasSet + timeLimitInMilliseconds)) {
                        db.setResponseToggle(false);
                        mResponseToggle = (Switch) findViewById(R.id.autoRespond_switch);
                        mResponseToggle.setChecked(false);
                        android.util.Log.i(TAG, "TimeLimit has expired! Turning off AutoResponder");
                    } else {
                        long timeRemainingLong = (timeToggleWasSet + timeLimitInMilliseconds) - currentTime;
                        String timeRemainingString = String.format("%d min, %d sec",
                                TimeUnit.MILLISECONDS.toMinutes(timeRemainingLong),
                                TimeUnit.MILLISECONDS.toSeconds(timeRemainingLong) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeRemainingLong))
                        );
                        android.util.Log.i(TAG, "TimeLimit is not indefinite! AutoResponder still running for: " + timeRemainingString);
                    }
                }else{
                    android.util.Log.i(TAG, "Time Limit is indefinite!");
                }
            } else {
                android.util.Log.i(TAG, "AutoResponder Toggle is inactive, killing this thread");
                return;
            }
            //Sleep to prevent constant checks
            Thread.sleep(5000);
        }
        catch (InterruptedException e){
            android.util.Log.e(TAG, "Thread is dead for unknown reason!");
        }
        //Continue to run the loop
        run();
    }

}
