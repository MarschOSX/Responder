package com.seniordesign.autoresponder;


import android.telephony.SmsManager;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;

/**
 * Created by MarschOSX on 10/8/2015.
 */
public class EventHandler{

    public static void respondToText(String phoneNumber, String message, Long timeRecieved)
    {
        //DBInstance db = DBProvider.get
        //TODO get toggle off/on from DB and CHECK to see if you run!
        if(phoneNumber != null){

            /**This Requires Database Access, which we are still working on
             * To prove we can delay a text
             **/
            //WORK IN PROGRESS
            //TODO get lastRecieved from database
            Long lastRecieved = null;//FROMDATABASE
            //TODO get delaySet from database

            Long delaySet = 0L;//FROMDATABASE

            if(lastRecieved == null || lastRecieved + delaySet < timeRecieved){
                //TODO set lastRecieved in database to timeRecieved

                lastRecieved = timeRecieved;

                //TODO get generalResponse from Database and set as message

                    //message = FROMDATABASE;

                //Send the GeneralResponse Message
                SmsManager sms = SmsManager.getDefault();

                sms.sendTextMessage(phoneNumber, null, message, null, null);
                android.util.Log.v("EventHandler,", "Message sent to: " + phoneNumber + " Message Body: " + message);
            }
        }


    }
}
