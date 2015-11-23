package com.seniordesign.autoresponder.Receiver;


import android.telephony.SmsManager;

import com.seniordesign.autoresponder.DataStructures.Contact;
import com.seniordesign.autoresponder.DataStructures.Group;
import com.seniordesign.autoresponder.DataStructures.ResponseLog;
import com.seniordesign.autoresponder.Persistance.DBInstance;

import java.sql.Date;

/*
 * Created by MarschOSX on 10/8/2015.
 */
public class EventHandler {

    private DBInstance db;

    public EventHandler(DBInstance db) {
        this.db = db;
    }

    public int respondToText(String phoneNumber, String message, Long timeRecieved) {
        //get toggle off/on from DB and CHECK to see if you run!
        //db.getResponseToggle();
        //Check phoneNumber validity
        android.util.Log.v("EventHandler,", "EventHandler is active!");
        // || !phoneNumber.matches("[+][0-9]{11}")
        if (phoneNumber == null){
            android.util.Log.v("EventHandler,", "Invalid PhoneNumber Recieved");
            return -1;
        }
        Contact contact = db.getContactInfo(phoneNumber);

        if (db.getResponseToggle() && contact != null) {
            //get lastRecieved from database
            ResponseLog updateLog = db.getLastResponseByNum(phoneNumber);
            if (updateLog == null) {
                android.util.Log.v("EventHandler,", "Invalid, UpdateLog is NULL");
                return -1;
            }
            Long lastRecieved = updateLog.getTimeStamp().getTime();
            //get delaySet from database
            Long delaySet = 60000 * (long) db.getDelay();//convert minutes to milliseconds
            if (timeRecieved != null && timeRecieved >= 0L) {
                if (lastRecieved == 0 || lastRecieved + delaySet < timeRecieved) {
                    lastRecieved = timeRecieved;
                    updateLog.setTimeStamp(new Date(lastRecieved));
                    updateLog.setMessageReceived(message);
                    updateLog.setSenderNumber(phoneNumber);
                    //get generalResponse from Database and set as message
                    String contactResponse = contact.getResponse();
                    String contactGroupName = contact.getGroupName();
                    Boolean contactInheritance = contact.isInheritance();
                    //Check the inheritance bit to see to use Contact Message or Group Message
                    if(contactInheritance){//inheritance from...
                        if(!contactGroupName.matches(Group.DEFAULT_GROUP)) {//contact groups
                            message = db.getGroupInfo(contactGroupName).getResponse();
                        }else{//default group
                            message = db.getReplyAll();
                        }
                    }else{//no inheritance, get contact info
                        message = contactResponse;
                    }

                    updateLog.setMessageSent(message);

                    //Add number, message sent, message recieved, and lastRecieved time to DB
                    db.addToResponseLog(updateLog);

                    //Send the GeneralResponse Message
                    SmsManager sms = SmsManager.getDefault();

                    android.util.Log.v("EventHandler,", "Message successfully sent to: " + phoneNumber + " Message Body: " + message);
                    sms.sendTextMessage(phoneNumber, null, message, null, null);
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
        android.util.Log.v("EventHandler,", "No Text Sent!");
        return -1;
    }
}


