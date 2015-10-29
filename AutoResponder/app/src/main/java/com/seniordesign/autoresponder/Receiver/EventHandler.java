package com.seniordesign.autoresponder.Receiver;


import android.telephony.SmsManager;

import com.seniordesign.autoresponder.DataStructures.ResponseLog;
import com.seniordesign.autoresponder.Persistance.DBInstance;

import java.sql.Date;

/**
 * Created by MarschOSX on 10/8/2015.
 */
public class EventHandler {

    private DBInstance db;

    public EventHandler(DBInstance db) {
        this.db = db;
    }

    public int respondToText(String phoneNumber, String message, Long timeRecieved, boolean debug) {
        //get toggle off/on from DB and CHECK to see if you run!
        //db.getResponseToggle();
        //Check phoneNumber validity
        android.util.Log.v("EventHandler,", "EventHandler is active!");
        if (phoneNumber == null || !phoneNumber.matches("[+][0-9]{11}")){
            android.util.Log.v("EventHandler,", "Invalid PhoneNumber Recieved");
            return -1;
        }

        if (db.getResponseToggle()) {
            //get lastRecieved from database
            ResponseLog updateLog = db.getLastResponseByNum(phoneNumber);
            if (updateLog == null) {
                android.util.Log.v("EventHandler,", "Invalid, UpdateLog is NULL");
                return -1;
            }
            Long lastRecieved = updateLog.getTimeStamp().getTime();
            //get delaySet from database
            Long delaySet = 60000 * Long.valueOf(db.getDelay());//convert minutes to milliseconds
            if (timeRecieved != null && timeRecieved >= 0L) {
                if (lastRecieved == 0 || lastRecieved + delaySet < timeRecieved) {
                    lastRecieved = timeRecieved;
                    updateLog.setTimeStamp(new Date(lastRecieved));
                    updateLog.setMessageReceived(message);
                    updateLog.setSenderNumber(phoneNumber);
                    //get generalResponse from Database and set as message
                    if (!debug) {
                        message = db.getReplyAll();
                    } else {
                        message += "\nGeneralReplyDEBUG:\n" + db.getReplyAll();
                    }
                    updateLog.setMessageSent(message);

                    //Add number, message sent, message recieved, and lastRecieved time to DB
                    db.addToResponseLog(updateLog);

                    //Send the GeneralResponse Message
                    SmsManager sms = SmsManager.getDefault();

                    sms.sendTextMessage(phoneNumber, null, message, null, null);
                    android.util.Log.v("EventHandler,", "Message successfully sent to: " + phoneNumber + " Message Body: " + message);
                    return 0;
                } else {
                    android.util.Log.v("EventHandler,", "Cannot send a response yet!");
                    return -1;
                }
            } else {
                android.util.Log.v("EventHandler,", "Invalid Time Recieved");
                return -1;
            }
        }
        android.util.Log.v("EventHandler,", "Response Toggle is OFF");
        return -1;
    }
}


