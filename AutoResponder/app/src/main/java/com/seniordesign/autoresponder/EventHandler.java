package com.seniordesign.autoresponder;


import android.os.SystemClock;
import android.telephony.SmsManager;

/**
 * Created by MarschOSX on 10/8/2015.
 */
public class EventHandler{

    public static void respondToText(String phoneNumber, String message, Long timeRecieved)
    {
        if(phoneNumber != null){

            /**This Requires Database Access, which we are still working on
             * To prove we can delay a text
             **/
            //WORK IN PROGRESS
            //TODO get lastRecieved from database
            Long lastRecieved = null;//FROMDATABASE
            //TODO get delaySet from database
                //TODO TEMP
            Long delaySet = 0L;//FROMDATABASE

            if(lastRecieved == null || lastRecieved + delaySet < timeRecieved){
                //TODO set lastRecieved in database to timeRecieved
                    //TODO HERE
                lastRecieved = timeRecieved;

                //TODO get generalResponse from Database and set as message
                    //TODO HERE
                    //message = FROMDATABASE;

                //Send the GeneralResponse Message
                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(phoneNumber, null, message, null, null);
                android.util.Log.v("EventHandler,", "Message sent to: " + phoneNumber + " Message Body: " + message);
            }
        }


    }
}
