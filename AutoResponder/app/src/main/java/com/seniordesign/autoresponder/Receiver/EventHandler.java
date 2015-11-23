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

                    //get response from Database and set as message
                    String contactResponse = contact.getResponse();
                    String contactGroupName = contact.getGroupName();
                    Boolean contactInheritance = contact.isInheritance();
                    String permissionsMessage;// = null


                    //Check the inheritance bit to see to use Contact Message or Group Message
                    if(contactInheritance){//inheritance from...
                        android.util.Log.v("EventHandler,", "Contact is inheriting group info/permissions");
                        permissionsMessage = permissionsRequested(contact,message,true);
                        if(permissionsMessage == null) {//no permissions, get normal response
                            /*if (!contactGroupName.matches(Group.DEFAULT_GROUP)) {//contact groups
                                message = db.getGroupInfo(contactGroupName).getResponse();
                            } else {
                                message = db.getReplyAll();
                            }*/
                            message = db.getGroupInfo(contactGroupName).getResponse();
                            android.util.Log.v("EventHandler,", "Message is the group response");
                        }else{
                            message = permissionsMessage;
                            android.util.Log.v("EventHandler,", "Message is an inherited location/activity response");
                        }
                    }else{//no inheritance, get contact info
                        android.util.Log.v("EventHandler,", "Contact is not inheriting, using its own info/permissions");
                        permissionsMessage = permissionsRequested(contact,message,false);
                        if(permissionsMessage == null) {
                            message = contactResponse;
                            android.util.Log.v("EventHandler,", "Message is the contact response");
                        }else{
                            message = permissionsMessage;
                            android.util.Log.v("EventHandler,", "Message is a not inherited location/activity response");

                        }
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

    public String permissionsRequested(Contact contact, String message, Boolean inheritance) {
        Boolean locationPermission;
        Boolean activityPermission;
        String returnMessage;
        if(inheritance) {
            //we get permissions from the group
            locationPermission = db.getGroupInfo(contact.getGroupName()).isLocationPermission();
            activityPermission = db.getGroupInfo(contact.getGroupName()).isActivityPermission();
        }else{
            //we get permissions from the contact
            locationPermission = contact.isLocationPermission();
            activityPermission = contact.isActivityPermission();
        }

        if(!locationPermission && !activityPermission){//if both are false, nothing to do here
            return null;
        }


        if (locationPermission) {//if Location permission is true
            //you have permission, check SMS message to see if location was requested
            if (message.contains("where are you")) {
                //then we want to give that person your location
                returnMessage = "I am at Kinsley.";//TODO make this not static
                return returnMessage;
            }
            //TODO more examples here, maybe go to another class to check/compare SMS strings?
            //TODO might want to return a drop pin instead of a message??
        }

        if (activityPermission) {//if Activity permission is true
            //you have permission, check SMS message to see if activity was requested
            if (message.contains("are you busy")) {
                //then we want to give that person your calendar info
                returnMessage = "I'm in class from 12pm-12am.";//TODO make this not static
                return returnMessage;
            }
            //TODO more examples here, maybe go to another class to check/compare SMS strings?
        }

        return null;//if original message did not request location or activity
    }
}


