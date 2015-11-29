package com.seniordesign.autoresponder.Receiver;


import android.app.ListActivity;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.telephony.SmsManager;

import com.seniordesign.autoresponder.DataStructures.Contact;
import com.seniordesign.autoresponder.DataStructures.Group;
import com.seniordesign.autoresponder.DataStructures.ResponseLog;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.R;

import java.sql.Date;

/*
 * Created by MarschOSX on 10/8/2015.
 */
public class EventHandler extends ListActivity{

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
                    String permissionsMessage = permissionsRequested(contact,message);

                    if(permissionsMessage == null) {//no permissions, get normal response
                        message = contactResponse;
                    }else{
                        message = permissionsMessage;
                    }
                    //Send the GeneralResponse Message
                    SmsManager sms = SmsManager.getDefault();

                    android.util.Log.v("EventHandler,", "Message successfully sent to: " + phoneNumber + " Message Body: " + message);
                    sms.sendTextMessage(phoneNumber, null, message, null, null);

                    //Add number, message sent, message recieved, and lastRecieved time to DB
                    db.addToResponseLog(updateLog);
                    updateLog.setMessageSent(message);

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

    public String permissionsRequested(Contact contact, String message) {
        String returnMessage;

        //TODO why do these functions go to group? Why is activity permission always true?
        Boolean locationPermission = contact.isLocationPermission();
        Boolean activityPermission = contact.isActivityPermission();

        if(!locationPermission && !activityPermission){//if both are false, nothing to do here
            return null;
        }else if(locationPermission && activityPermission){//if they are both true
            return null;//TODO how do we handle if both are requested
        }else if (locationPermission) {//if just Location permission is true
            returnMessage = getLocationInfo(message);
            return returnMessage;
            //TODO Why is activity permission always true?
        }else if (activityPermission) {//if just Activity permission is true
            returnMessage = getActivityInfo(message);
            return returnMessage;
        }
        return null;
    }


    public String getActivityInfo(String message){
        //you have permission, check SMS message to see if activity was requested
        String[] possibleRequests = getResources().getStringArray(R.array.activity_requests_list);
        String returnMessage = null;
        //goes through a list of possible location responses
        for (String possibleRequest : possibleRequests)
            if (message.contains(possibleRequest)) {
                //then we want to give that person your calendar info
                try {//attempt to access calendar information
                    long begin = System.currentTimeMillis() % 1000;
                    //long end = begin;// ending time in milliseconds
                            String[] query =
                            new String[]{
                                    CalendarContract.Instances._ID,
                                    CalendarContract.Instances.BEGIN,
                                    CalendarContract.Instances.END,
                                    CalendarContract.Instances.EVENT_ID};
                    Cursor cursor =
                            CalendarContract.Instances.query(getContentResolver(), query, begin, begin);
                    if (cursor.getCount() > 0) {//yes we are busy
                        returnMessage = "I'm at an event right now";

                        //get event ID so we can get more info on it
                        /*int eventID = cursor.getColumnIndex(CalendarContract.Instances.EVENT_ID);
                        String[] proj =
                                new String[]{
                                        CalendarContract.Events._ID,
                                        CalendarContract.Events.DTSTART,
                                        CalendarContract.Events.DTEND,
                                        CalendarContract.Events.RRULE,
                                        CalendarContract.Events.TITLE};
                        Cursor cursor2 =
                                getContentResolver().
                                        query(CalendarContract.Events.CONTENT_URI, proj, CalendarContract.Events._ID + " = ? ", new String[]{Long.toString(eventID)}, null);
                        if (cursor2.moveToFirst()) {
                            // read event data
                        }*/
                    }

                } catch (Exception e) {//if exception, just
                    returnMessage = null;
                }

                return returnMessage;
            }
        return null;
    }

    public String getLocationInfo(String message){
        String returnMessage;
        //you have permission, check SMS message to see if location was requested
        if (message.contains("where are you")) {
            //then we want to give that person your location
            returnMessage = "I am at Kinsley.";//TODO make this not static
            return returnMessage;
        }
        //TODO more examples here, maybe go to another class to check/compare SMS strings?
        //TODO might want to return a drop pin instead of a message??
        return null;
    }


}


