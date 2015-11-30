package com.seniordesign.autoresponder.Receiver;


import android.app.ListActivity;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.telephony.SmsManager;

import com.seniordesign.autoresponder.DataStructures.Contact;
import com.seniordesign.autoresponder.DataStructures.ResponseLog;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.R;

import java.sql.Date;

/*
 * Created by MarschOSX on 10/8/2015.
 */
public class EventHandler extends ListActivity{

    private DBInstance db;
    //String[] possibleRequests;

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        possibleRequests = getResources().getStringArray(R.array.activity_requests_list);
    }*/

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
                    Boolean locationPermission = contact.isLocationPermission();
                    Boolean activityPermission = contact.isActivityPermission();

                    if (locationPermission && activityPermission) {//if they are both true
                        String locMessage = getLocationInfo(message);
                        String actMessage = getActivityInfo(message);
                        //if both requested, send two texts as long as they are not null
                        if(locMessage !=  null) {
                            sendSMS(phoneNumber, locMessage);
                        }
                        if(actMessage != null) {
                            sendSMS(phoneNumber, actMessage);
                        }
                        //if both are null send normal response
                        if(locMessage == null && actMessage == null){
                            sendSMS(phoneNumber, contactResponse);
                        }
                    }else if(locationPermission) {//if just Location permission is true
                        String locMessage = getLocationInfo(message);
                        if(locMessage != null){
                            sendSMS(phoneNumber, locMessage);
                        }else{
                            sendSMS(phoneNumber,contactResponse);//if null, send the normal message
                        }
                    } else if (activityPermission) {//if just Activity permission is true
                        String actMessage = getActivityInfo(message);
                        if(actMessage != null){
                            sendSMS(phoneNumber, actMessage);
                        }else{
                            sendSMS(phoneNumber,contactResponse);//if null, send the normal message
                        }
                    }else {//both permissions are false, go to normal response
                        sendSMS(phoneNumber, contactResponse);//if null, send the normal message
                    }
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

    public void sendSMS(String phoneNumber, String message){
        ResponseLog updateLog = db.getLastResponseByNum(phoneNumber);
        if (updateLog == null) {
            android.util.Log.v("EventHandler,", "Invalid, UpdateLog is NULL");
            return;
        }

        //Send the GeneralResponse Message
        SmsManager sms = SmsManager.getDefault();

        android.util.Log.v("EventHandler,", "Message successfully sent to: " + phoneNumber + " Message Body: " + message);
        sms.sendTextMessage(phoneNumber, null, message, null, null);

        //Add number, message sent, message recieved, and lastRecieved time to DB
        db.addToResponseLog(updateLog);
        updateLog.setMessageSent(message);
    }

    public String getActivityInfo(String message){
        //Load in different types of activity requests in string form
        /*Resources res = getResources();
        String[] possibleRequests = res.getStringArray(R.array.activity_requests_list);*/

        //TODO this is not efficient, but it works for now, try to get above method working with extends at the top of file
        String[] possibleRequests = {"are you busy", "are you free", "whats up", "you doing anything", "what are you up to"};

        //Return message based on request, if null then no request made
        String returnMessage = null;

        //goes through a list of possible location responses
        for (String possibleRequest : possibleRequests) {
            //if there was a match
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
                        android.util.Log.v("EventHandler,", "Accessing Calendar Successful and busy");
                        /*
                        //get event ID so we can get more info on it
                        int eventID = cursor.getColumnIndex(CalendarContract.Instances.EVENT_ID);
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
                    }else{
                        returnMessage = null;
                        android.util.Log.v("EventHandler,", "Accessing Calendar Successful, and free");
                    }

                } catch (Exception e) {//if exception, just
                    android.util.Log.v("EventHandler,", "Accessing Calendar Failed");
                }

                //return the message
                return returnMessage;
            }
        }
        return null;
    }

    public String getLocationInfo(String message){
        //TODO use this function to respond with the location, remove static fields
        String returnMessage;
        //you have permission, check SMS message to see if location was requested
        if (message.contains("where are you")) {
            //then we want to give that person your location
            returnMessage = "I am at Kinsley.";
            return returnMessage;
        }
        return null;
    }

    public String getActInfoPrim(String message){
        //TODO use this function to respond with the location, remove static fields
        String returnMessage;
        //you have permission, check SMS message to see if location was requested
        if (message.contains("are you free")) {
            //then we want to give that person your location
            returnMessage = "I am in class.";
            return returnMessage;
        }
        return null;
    }

}


