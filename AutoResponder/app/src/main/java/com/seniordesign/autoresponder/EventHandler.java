package com.seniordesign.autoresponder;


import android.telephony.SmsManager;

/**
 * Created by MarschOSX on 10/8/2015.
 */
public class EventHandler{

    public static void respondToText(String phoneNumber, String message, Long timeRecieved)
    {
        if(phoneNumber != null){

            //TODO get notRespTill from database (now just null, as if never recieved)
            Long notRespTill = null;/*Get*/

            if(notRespTill == null || notRespTill < timeRecieved){
                //TODO get delaySet from database (now just 1 minute/60 seconds for testing)
                    //TODO TEMP
                    notRespTill = timeRecieved + /*Get*/Long.valueOf(1 * 6000);//converts int minutes to long ms
                //TODO set notRespTill in database to timeRecieved
                    //TODO HERE

                //TODO get generalResponse from Database and set as message
                    //TODO HERE

                //Send the GeneralResponse Message
                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(phoneNumber, null, message, null, null);
                android.util.Log.v("SMSIsSent!,", "Message sent to: " + phoneNumber + " Message Body: " + message);
            }






        }


    }
}
