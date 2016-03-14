package com.seniordesign.autoresponder.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.seniordesign.autoresponder.Persistance.PermDBInstance;


public class MMSListener extends BroadcastReceiver{
    public static final String TAG = "MMSListener";
    private static final String ACTION_MMS_RECEIVED = "android.provider.Telephony.WAP_PUSH_RECEIVED";
    private static final String MMS_DATA_TYPE = "application/vnd.wap.mms-message";

    //Listener gets a message
    @Override
    public void onReceive(Context context, Intent intent)
    {
        //Get a MMS
        Log.v(TAG, "MMSListener Activated!");
        String action  = intent.getAction();
        String type = intent.getType();
        Log.v(TAG, "Intent received: " + action);
        Log.v(TAG, "Type of Intent received: " + type);

        //Needed for the EventHandler
        String phoneNumber = null;
        String message = "";
        Long timeReceived;

        //If its an MMS
        if(action.equals(ACTION_MMS_RECEIVED) && type.equals(MMS_DATA_TYPE)){
            Log.v(TAG, "Recieved an MMS");
            Bundle bundle = intent.getExtras();
            if(bundle != null){
                byte[] buffer = bundle.getByteArray("data");
                phoneNumber = new String(buffer);
                int indx = phoneNumber.indexOf("/TYPE");
                if(indx>0 && (indx-15)>0){
                    int newIndx = indx - 15;
                    phoneNumber = phoneNumber.substring(newIndx, indx);
                    indx = phoneNumber.indexOf("+");
                    if(indx>0){
                        phoneNumber = phoneNumber.substring(indx);
                        Log.d(TAG, "Mobile Number: " + phoneNumber);
                    }
                }
                byte[] dataBuffer = bundle.getByteArray("data");
                message = new String(dataBuffer);
                Log.d(TAG, "data from message: " + message);
            }
            timeReceived = System.currentTimeMillis();

            if (phoneNumber != null) {
                Thread handler = new Thread(new EventHandler(new PermDBInstance(context), context, phoneNumber, message, timeReceived));
                handler.setDaemon(true);
                handler.start();
            } else {
                Log.v(TAG, "Invalid Phone Number");
                throw new IllegalArgumentException("Invalid Phone Number in MMSListener");
            }
        }else{
            Log.v(TAG, "Recieved unknown communication type");
        }
    }
}
