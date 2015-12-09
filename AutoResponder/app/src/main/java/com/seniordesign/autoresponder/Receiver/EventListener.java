package com.seniordesign.autoresponder.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.ArrayMap;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.seniordesign.autoresponder.Persistance.DBProvider;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EventListener extends BroadcastReceiver{
    public static final String TAG = "EventListener";
    private GoogleLocator locator;
    private Context context;
    private Intent intent;
    //for more information about incoming SMS, set to true
   // boolean debug = false;

    //Listener gets a message
    @Override
    public void onReceive(Context context, Intent intent)
    {
        //EventListener passes info to EventHandler
        this.context = context;
        this.intent = intent;
        //location info demo
        locator = new GoogleLocator(context, this);

        //locator.close();

        /*android.util.Log.v("EventListener,", "Intent received: " + intent.getAction());
        String phoneNumber = null;
        String message = "";
        Long timeRecieved = null;
        Bundle bundle = intent.getExtras();

        Object[] messages = (Object[]) bundle.get("pdus");

        //@SuppressWarnings("unchecked")
        //ArrayMap<String, Object>[] messages = new ArrayMap[20000];
        //Object messages = new Object[]();

        if (messages != null) {
            SmsMessage[] sms = new SmsMessage[messages.length];
            for (int n = 0; n < messages.length; n++) {
                sms[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
                //if(debug == false) {
                phoneNumber = sms[n].getOriginatingAddress();
                message = sms[n].getMessageBody();
                timeRecieved = sms[n].getTimestampMillis();
                *//*}else{
                    message += "OriginatingAddress:\n";
                    message += sms[n].getOriginatingAddress();
                    phoneNumber = sms[n].getOriginatingAddress();
                    message += "\nMessageBody:\n";
                    message += sms[n].getMessageBody();
                    message += "\nMilliseconds Timestamp:\n";
                    message += String.valueOf(sms[n].getTimestampMillis());
                }*//*
            }
        }

        //pass information to EventHandler.respondToText()
        if(phoneNumber != null) {
            EventHandler ev= new EventHandler(DBProvider.getInstance(false, context));
            ev.respondToText(phoneNumber, message, timeRecieved, context);
        }else{
            android.util.Log.v("EventHandler,", "Invalid Phone Number");
            throw new IllegalArgumentException("Invalid Phone Number in EventListener");
        }*/
    }

    public void onReceive_cont()
    {
        Location currentLocation = locator.getCurrentLocation();
        if (currentLocation != null) Log.d(TAG, currentLocation.getLatitude() + " " + currentLocation.getLongitude());
        else Log.e(TAG, "no location returned");
        locator.close();

        android.util.Log.v("EventListener,", "Intent received: " + intent.getAction());
        String phoneNumber = null;
        String message = "";
        Long timeRecieved = null;
        Bundle bundle = intent.getExtras();

        Object[] messages = (Object[]) bundle.get("pdus");

        //@SuppressWarnings("unchecked")
        //ArrayMap<String, Object>[] messages = new ArrayMap[20000];
        //Object messages = new Object[]();

        if (messages != null) {
            SmsMessage[] sms = new SmsMessage[messages.length];
            for (int n = 0; n < messages.length; n++) {
                sms[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
                //if(debug == false) {
                phoneNumber = sms[n].getOriginatingAddress();
                message = sms[n].getMessageBody();
                timeRecieved = sms[n].getTimestampMillis();
                /*}else{
                    message += "OriginatingAddress:\n";
                    message += sms[n].getOriginatingAddress();
                    phoneNumber = sms[n].getOriginatingAddress();
                    message += "\nMessageBody:\n";
                    message += sms[n].getMessageBody();
                    message += "\nMilliseconds Timestamp:\n";
                    message += String.valueOf(sms[n].getTimestampMillis());
                }*/
            }
        }

        //pass information to EventHandler.respondToText()
        if(phoneNumber != null) {
            EventHandler ev= new EventHandler(DBProvider.getInstance(false, context));
            ev.respondToText(phoneNumber, message, timeRecieved, context);
        }else{
            android.util.Log.v("EventHandler,", "Invalid Phone Number");
            throw new IllegalArgumentException("Invalid Phone Number in EventListener");
        }
    }

    private void pauseHere(int i, int max){
        try{
            if (i <= max) {
                TimeUnit.MILLISECONDS.sleep(500);
                Log.d(TAG, "waiting...");
                pauseHere(i + 1, max);
            }
        }
        catch (InterruptedException e){
            Log.e(TAG, "wait interrupted");
        }
    }
}
