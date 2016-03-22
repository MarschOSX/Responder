package com.seniordesign.autoresponder.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.seniordesign.autoresponder.Persistance.PermDBInstance;


public class SMSListener extends BroadcastReceiver{
    public static final String TAG = "SMSListener";
    private static final String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    //Listener gets a message
    @Override
    public void onReceive(Context context, Intent intent)
    {
        //Get an SMS
        android.util.Log.v(TAG, "SMSListener Activated!");
        String action  = intent.getAction();
        String type = intent.getType();
        android.util.Log.v(TAG, "Intent received: " + action);
        android.util.Log.v(TAG, "Type of Intent received: " + type);

        //Needed for the EventHandler
        String phoneNumber = null;
        String message = "";
        Long timeReceived = null;

        //IF it's an SMS
        if(action.equals(ACTION_SMS_RECEIVED)) {
            android.util.Log.v(TAG, "Recieved an SMS");
            Bundle bundle = intent.getExtras();
            Object[] messages = (Object[]) bundle.get("pdus");
            if (messages != null) {
                SmsMessage[] sms = new SmsMessage[messages.length];
                for (int n = 0; n < messages.length; n++) {
                    sms[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
                    phoneNumber = sms[n].getOriginatingAddress();
                    message = sms[n].getMessageBody();
                    timeReceived = sms[n].getTimestampMillis();
                }
            }
            //pass information to EventHandler.respondToText()
            if (phoneNumber != null) {
                Thread handler = new Thread(new EventHandler(new PermDBInstance(context), context, phoneNumber, message, timeReceived));
                handler.setDaemon(true);
                handler.start();
            } else {
                android.util.Log.v("EventHandler,", "Invalid Phone Number");
                throw new IllegalArgumentException("Invalid Phone Number in SMSListener");
            }
        }else{
            android.util.Log.v(TAG, "Recieved unknown communication type");
        }
    }
}
