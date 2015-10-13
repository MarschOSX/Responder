package com.seniordesign.autoresponder;


import android.content.Context;
import android.telephony.SmsManager;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;

/**
 * Created by MarschOSX on 10/8/2015.
 */
public class EventHandler {

    public static int respondToText(String phoneNumber, String message, Long timeRecieved, Context context, boolean debug) {
        DBInstance db = DBProvider.getInstance(false, context);
        //TODO get toggle off/on from DB and CHECK to see if you run!
        // && db.getResponseToggle() == true
        if (phoneNumber != null) {
            //access the database


            //TODO get lastRecieved from database
            Long lastRecieved = null;//FROMDATABASE
            //TODO get delaySet from database
            Long delaySet = 0L;//60000*(Long.valueOf(db.getDelay()));//convert minutes to milliseconds


            if (lastRecieved == null || lastRecieved + delaySet < timeRecieved) {
                //TODO set lastRecieved in database to timeRecieved
                //lastRecieved = timeRecieved;

                //get generalResponse from Database and set as message
                if (debug == true) {
                    message += "\nGeneralReply:\n" + db.getReplyAll();
                } else {
                    message = db.getReplyAll();
                }

                //Send the GeneralResponse Message
                SmsManager sms = SmsManager.getDefault();

                sms.sendTextMessage(phoneNumber, null, message, null, null);
                android.util.Log.v("EventHandler,", "Message sent to: " + phoneNumber + " Message Body: " + message);
            }
        } else {
            return -1;
        }

        return 0;
    }

    public static int tester(String phoneNumber, String message, Long timeRecieved, Context context, boolean debug) {
        DBInstance db = DBProvider.getInstance(false, context);
        return -1;
    }

}
