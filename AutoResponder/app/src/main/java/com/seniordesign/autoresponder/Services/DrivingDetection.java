package com.seniordesign.autoresponder.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Garlan on 2/28/2016.
 */
public class DrivingDetection extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startID){

        return -1;
    }

    @Override
    public IBinder onBind(Intent intent){

        return null;
    }

    @Override
    public void onCreate(){

    }

    @Override
    public void onDestroy(){

    }
}
