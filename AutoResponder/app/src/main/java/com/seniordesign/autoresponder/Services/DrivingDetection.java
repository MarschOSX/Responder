package com.seniordesign.autoresponder.Services;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.*;

import com.seniordesign.autoresponder.Interface.LocationOutput;
import com.seniordesign.autoresponder.R;

/**
 * Created by Garlan on 2/28/2016.
 */
public class DrivingDetection extends Service {
    private DrivingDetectionHandler mServiceHandler;
    private Context context = getApplicationContext();

    @Override
    public int onStartCommand(Intent intent, int flags, int startID){

        if(intent != null) { // May not have an Intent is the service was killed and restarted (See STICKY_SERVICE).
            Message msg = mServiceHandler.obtainMessage();
            //msg.arg1 = startId;
            msg.obj = intent.getStringExtra("something");
            mServiceHandler.sendMessage(msg);
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent){

        return null;
    }

    @Override
    public void onCreate(){

        //to start in foreground
       /* Notification notification = new Notification.Builder(context)
                .setSmallIcon(R.drawable.something)
                .setContentText("Content")
                .setContentTitle("Title")
                .getNotification();
        startForeground(17, notification); // Because it can't be zero...*/

        super.onCreate();
        HandlerThread thread = new HandlerThread("DrivingDetectionService", android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        Looper serviceLooper = thread.getLooper();
    }

    @Override
    public void onDestroy(){

    }

    public class DrivingDetectionHandler extends Handler{
        public DrivingDetectionHandler (Looper looper){
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int startId = msg.arg1;
            Object message = msg.obj;
            // Do some processing
            boolean stopped = stopSelfResult(startId);
            // stopped is true if the service is stopped
        }
    }
}
