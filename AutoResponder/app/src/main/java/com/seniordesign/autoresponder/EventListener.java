package com.seniordesign.autoresponder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;



public class EventListener extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent)
    {

        Bundle extras = intent.getExtras();
        String message = "";
        if (extras != null)
        {
            Bundle bundle=intent.getExtras();

            Object[] messages=(Object[])bundle.get("pdus");
            if(messages != null) {
                SmsMessage[] sms = new SmsMessage[messages.length];
                for(int n=0;n<messages.length;n++){
                    sms[n]=SmsMessage.createFromPdu((byte[]) messages[n]);
                    message += sms[n];
                }
            }



        }

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}
