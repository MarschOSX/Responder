package com.seniordesign.autoresponder;

//import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
//import android.os.Bundle;
//import android.telephony.SmsManager;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
//import android.util.*;
//import android.widget.Toast;



public class EventListener extends BroadcastReceiver{
    //@Override
    /*public void onReceive(Context context, Intent intent)
    {
        boolean checked = true;
        android.util.Log.v("AutoResponder is ", java.lang.Boolean.toString(checked));
        String message = "Test ";
        Bundle bundle=intent.getExtras();

        Object[] messages=(Object[])bundle.get("pdus");
        if(messages != null) {
            SmsMessage[] sms = new SmsMessage[messages.length];
            for(int n=0;n<messages.length;n++){
                sms[n]=SmsMessage.createFromPdu((byte[]) messages[n]);
                message += sms[n];
            }
        }

        //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage("8568327320", null, message, null, null);
    }*/

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean checked = true;
        android.util.Log.v("AutoResponder is ", java.lang.Boolean.toString(checked));
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                android.util.Log.v("AutoResponder is ", java.lang.Boolean.toString(checked));
                String messageBody = smsMessage.getMessageBody();
                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage("8568327320", null, messageBody, null, null);
            }
        }
    }

}
