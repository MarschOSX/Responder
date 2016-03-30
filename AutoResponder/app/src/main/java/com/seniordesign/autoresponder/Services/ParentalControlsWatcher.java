package com.seniordesign.autoresponder.Services;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.seniordesign.autoresponder.DataStructures.ResponseLog;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.PermDBInstance;
import com.seniordesign.autoresponder.Receiver.SMSSender;

/**
 * Created by MarschOSX on 3/30/2016.
 */
public class ParentalControlsWatcher extends BroadcastReceiver{
    public static final String TAG = "ParentalControlsWatcher";
    private static final String ACTION_SMS_SENT = "android.provider.Telephony.SMS_SENT";
    private DBInstance db;


    @Override
    public void onReceive(Context context, Intent intent) {
        android.util.Log.v(TAG, "A message was sent out from the device!");

        /*
        db = new PermDBInstance(context);
        if(db.getParentalControlsToggle()) {
            android.util.Log.v(TAG, "Parental controls is enabled!");
            String action = intent.getAction();
            String type = intent.getType();
            android.util.Log.v(TAG, "Intent received: " + action);
            android.util.Log.v(TAG, "Type of Intent received: " + type);

            //IF an SMS has been sent
            if (action.equals(ACTION_SMS_SENT)) {
                String phoneNumber = null;
                String message = "";
                Bundle bundle = intent.getExtras();
                Object[] messages = (Object[]) bundle.get("pdus");
                if (messages != null) {
                    SmsMessage[] sms = new SmsMessage[messages.length];
                    for (int n = 0; n < messages.length; n++) {
                        sms[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
                        phoneNumber = sms[n].getOriginatingAddress();
                        message = sms[n].getMessageBody();
                    }
                }
                ResponseLog lastLog = db.getLastResponseByNum(phoneNumber);
                if(!lastLog.getMessageSent().matches(message)){
                    android.util.Log.v(TAG, "Message WAS NOT SENT from AutoResponder!");
                    //Todo check if user is driving
                    boolean isDriving = true;
                    if(isDriving){
                        android.util.Log.v(TAG, "A text was sent WHILE DRIVING! BAD DOGGY, alert parent");
                        SMSSender sender = new SMSSender(db);
                        sender.sendSMS("ALERT this user sent a text while driving", "",
                                db.getParentalControlsNumber(), 0L, false, false);

                    }

                }else{
                    android.util.Log.v(TAG, "Message WAS SENT from AutoResponder! No worries!");
                }
            }


        }
        */


    }

}
