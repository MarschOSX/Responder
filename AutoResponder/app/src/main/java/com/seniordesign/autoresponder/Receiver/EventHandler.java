package com.seniordesign.autoresponder.Receiver;


import android.content.Context;
import android.telephony.SmsManager;

import com.seniordesign.autoresponder.DataStructures.ResponseLog;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;

import java.sql.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by MarschOSX on 10/8/2015.
 */
public class EventHandler {

    private DBInstance db;

    public EventHandler(DBInstance db){
        this.db = db;
    }

    public int respondToText(String phoneNumber, String message, Long timeRecieved, boolean debug) {
        //get toggle off/on from DB and CHECK to see if you run!
        if(db.getResponseToggle()) {

            //Check phoneNumber validity
            /*if (phoneNumber != null && phoneNumber.length() == 12) {
                /*String regex = "\\d";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(phoneNumber);
                if (!matcher.matches()) {
                    android.util.Log.v("EventHandler,", "Invalid Phone Number");
                    return -1;
                }
            } else {
                return -1;
            }*/

            //get lastRecieved from database
            ResponseLog updateLog = db.getLastEntryByNum(phoneNumber);
            if(updateLog == null){
                return -1;
            }

            Long lastRecieved = updateLog.getTimeStamp().getTime();

            //get delaySet from database
            Long delaySet = 60000*(Long.valueOf(db.getDelay()));//convert minutes to milliseconds
            if (delaySet == null) {
                android.util.Log.v("EventHandler,", "Invalid delaySet from DB");
                return -1;
            }
            if (timeRecieved != null && timeRecieved >= 0L) {
                if (lastRecieved == 0 || lastRecieved + delaySet < timeRecieved) {
                    lastRecieved = timeRecieved;
                    updateLog.setTimeStamp(new Date(lastRecieved));
                    updateLog.setMessageReceived(message);
                    updateLog.setSenderNumber(phoneNumber);
                    //get generalResponse from Database and set as message
                    if (debug == false) {
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
                    android.util.Log.v("EventHandler,", "Message sent to: " + phoneNumber + " Message Body: " + message);
                }
            } else {
                android.util.Log.v("EventHandler,", "Invalid Time Recieved");
                return -1;
            }
        }
        return 0;
    }
}
