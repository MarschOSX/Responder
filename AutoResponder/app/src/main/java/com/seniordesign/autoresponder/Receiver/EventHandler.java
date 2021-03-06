package com.seniordesign.autoresponder.Receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.IBinder;
import android.provider.CalendarContract;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.seniordesign.autoresponder.DataStructures.Contact;
import com.seniordesign.autoresponder.DataStructures.Group;
import com.seniordesign.autoresponder.DataStructures.ResponseLog;
import com.seniordesign.autoresponder.Interface.Main;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.R;
import com.seniordesign.autoresponder.Services.DrivingDetectionService;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/*
 * Created by MarschOSX on 10/8/2015.
 */


public class EventHandler implements Runnable{
    private static final String TAG = "EventHandler";
    private DBInstance db;
    private Context context;
    private GoogleLocator locator;
    private String phoneNumber;
    private String messageReceived;
    private Long timeReceived;
    private SMSSender sender;

    public EventHandler(DBInstance db, Context context, String phoneNumber, String messageReceived, Long timeReceived) {
        this.db = db;
        this.context = context;
        this.phoneNumber = phoneNumber;
        this.messageReceived = messageReceived;
        this.timeReceived = timeReceived;
        sender = new SMSSender(db);
    }

    //runs as a new thread
    public void run(){

        respondToText();
    }

    public int respondToText() {
        //SMSListener passes info to EventHandler
        android.util.Log.v("EventHandler,", "EventHandler is active!");
        if (phoneNumber == null){
            android.util.Log.e("EventHandler,", "No PhoneNumber found");
            return -1;
        }



        //try to retrieve information for that phone number fromm the database
        Contact contact = db.getContactInfo(phoneNumber);

        //For World Reply
        if(db.getWorldToggle() && contact == null){
            //We need to populate with default group info
            contact = new Contact(phoneNumber, phoneNumber, Group.DEFAULT_GROUP, db.getReplyAll(), false, false, true);
        }


        //The function will only continue if the user has set the response toggle to on
        // and the phone number has a corresponding contact stored in the database
        if ((db.getResponseToggle() || db.getIsDriving()) && contact != null) {

            //get last message received for that contact from the database
            ResponseLog lastLog = db.getLastResponseByNum(phoneNumber);
            if (lastLog == null) {
                android.util.Log.v(TAG, "Invalid, UpdateLog is NULL");
                return -1;
            }
            android.util.Log.v(TAG, "UpdateLog is NOT NULL");

            //retrieve the time that the last message (that was responded to) was received
            Long lastTimeReceived;
            try{
                lastTimeReceived = Long.parseLong(lastLog.getTimeReceived());
                android.util.Log.v(TAG, "LastLog Time is "+ Long.toString(lastTimeReceived));
            }catch(Exception e){
                lastTimeReceived = 0L;
                android.util.Log.e(TAG, "EXCEPTION-> LastLog Time is "+ Long.toString(lastTimeReceived));
            }

            android.util.Log.v(TAG, "This is the time from the last log: " + lastLog.getTimeReceived());

            //get gets the delay (in minutes) as specified in the Settings section of the database and converts it to ms
            Long delaySet = 60000 * (long) db.getDelay();

            //verify that a timestamp was attached to the incoming message
            if (timeReceived != null && timeReceived >= 0L) {
                //get response from Database and set as message
                String contactResponse;

                //If universal reply is true, have that as normal message instead of contact preset one
                if(db.getUniversalToggle()){
                    contactResponse = db.getUniversalReply();
                }
                else{ //otherwise use the response for that contact
                    contactResponse = contact.getResponse();
                }

                //retrieve the contact's permissions
                Boolean locationPermission = contact.isLocationPermission();
                Boolean activityPermission = contact.isActivityPermission();

                //for parental override
                if(db.getParentalControlsToggle() && db.getParentalControlsNumber().contains(phoneNumber)){
                    locationPermission = true;
                    activityPermission = true;
                }else{
                    //verify permissions can be used
                    if(!db.getLocationToggle()){
                        locationPermission = false;
                    }
                    if(!db.getActivityToggle()){
                        activityPermission = false;
                    }
                }

                //if Location permission is true
                if(locationPermission) {
                    if (this.messageReceived.toLowerCase().contains("where are you") ||
                            this.messageReceived.toLowerCase().contains("where r you") ||
                            this.messageReceived.toLowerCase().contains("where are u") ||
                            this.messageReceived.toLowerCase().contains("where r u")) {
                        //create locator and pass a reference of this object
                        locator = new GoogleLocator(context, this);
                        android.util.Log.v("EventHandler,", "Sent location info");
                    }
                }
                //if Activity permission is true
                if (activityPermission) {
                    String actMessage = getActivityInfo(messageReceived);
                    if(actMessage != null){
                        sender.sendSMS(actMessage, messageReceived, phoneNumber, timeReceived, false, true, context);
                        android.util.Log.v("EventHandler,", "Sent activity info");
                    }
                }
                //checks to make sure that message received does not fall within the delay period
                if (lastTimeReceived == 0 || lastTimeReceived + delaySet < timeReceived) {
                    android.util.Log.v("EventHandler,", "Last Time Recieved: " + Long.toString(lastTimeReceived) + " delay set: "+  Long.toString(delaySet)+ " added: "+  Long.toString(lastTimeReceived + delaySet) + " time recieved: "+  Long.toString(timeReceived));
                    sender.sendSMS(contactResponse, messageReceived, phoneNumber, timeReceived, false, false, context);
                    android.util.Log.v("EventHandler,", "Sent normal text");
                    return 0;
                } else {
                    android.util.Log.v("EventHandler,", "Cannot send a response yet due to delay, and not a Loc/Act request!");
                    return -1;
                }
            } else {
                android.util.Log.v("EventHandler,", "Invalid Time Received");
                return -1;
            }
        }
        android.util.Log.v("EventHandler,", "No Text Sent! Toggle OFF or Null Contact");
        return -1;
    }

    /**
     * ----------------------------------------All of the functions below are used for accessing location--------------------------------------------------------------
     */

    public void sendLocationInfo() {
        String addressText;
        String link;
        String message;

        //once called by the locator, retrieve the raw locationgetContext and human friendly address
        Location currentLocation = locator.getCurrentLocation();
        Address currentAddress = locator.getCurrentAddress();

        //locator is no longer needed, close it.
        locator.close();

        if (currentLocation == null) {
            sender.sendSMS("my location cannot be determined at this time", this.messageReceived, this.phoneNumber, this.timeReceived, false, false, context);
        } else {

            //build the location message w/ address and URL to google maps
            addressText = currentAddress.getAddressLine(0) + " " + currentAddress.getAddressLine(1) + " " + currentAddress.getAddressLine(2);
            link = "http://maps.google.com/?q=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude();
            message = "I am at: \n" + addressText + "\n\n" + link;

            //send the message
            sender.sendSMS(message, this.messageReceived, this.phoneNumber, this.timeReceived, true, false, context);
        }
    }


    /**
     * ----------------------------------------All of the functions below are used for accessing the calendar--------------------------------------------------------------
     */

    public String getActivityInfo(String message){
        //Load in different types of activity requests in string form
        /*Resources res = getResources();
        String[] possibleRequests = res.getStringArray(R.array.activity_requests_list);*/
        Cursor calCursor;
        Long timeManager;
        Long startTime = null;
        Long endTime = null;
        Long currentTime;
        Long past24;
        Long future24;
        Long twentyFourHours = 86400000l;

        //This is not efficient, but it works for now, try to get above method working with extends at the top of file
        String[] possibleRequests = {"you busy", "you free", "whats up", "doing anything", "what are you up to", "you available"};

        //Return message based on request, if null then no request made
        String returnMessage = null;

        //goes through a list of possible location responses
        for (String possibleRequest : possibleRequests) {
            //if there was a match
            if (message.toLowerCase().contains(possibleRequest)) {
                //then we want to give that person your calendar info
                try {//attempt to access calendar information
                    ContentResolver contentResolver = context.getContentResolver();
                    android.util.Log.v("EventHandler,", "Created Cal Info Getters");

                    //look at currentTime
                    currentTime = System.currentTimeMillis();
                    past24 = currentTime - twentyFourHours;
                    future24 = currentTime + twentyFourHours;

                    //Query data from the Android Calendar, events for 24 hours in the past and in the future
                    Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI
                            .buildUpon();
                    ContentUris.appendId(eventsUriBuilder, past24);
                    ContentUris.appendId(eventsUriBuilder, future24);
                    Uri eventsUri = eventsUriBuilder.build();
                    //query the calendar, more information can be used in future updates
                    calCursor = contentResolver.query(eventsUri, new String[]{"calendar_id", "title", "description", "begin", "end", "duration", "allDay", "eventLocation", "eventStatus"}, null, null, CalendarContract.Instances.BEGIN  + " ASC");
                    android.util.Log.v("EventHandler,", "Successful Cal Query");

                    //Start at first Event
                    calCursor.moveToFirst();
                    android.util.Log.v(TAG, "Populated CNames with: " + calCursor.getCount() + " Events");

                    //Loop through event times and compare to current times
                    for (int i = 0; i < calCursor.getCount(); i++) {
                        //info of the event that is being looked at
                        Log.d(TAG, calCursor.getString(0) + " " + calCursor.getString(1) + " " + calCursor.getString(2) + " " + calCursor.getString(3) + " " + calCursor.getString(4)  + " " + calCursor.getString(5) + " " + calCursor.getString(6) + " " + calCursor.getString(7) + " " + calCursor.getString(8));

                        //make sure event is not an all day event
                        if(calCursor.getString(6).matches("0")) { // 0 = not and all day event
                            //look at startTime
                            if (calCursor.getString(3) != null) {
                                timeManager = Long.parseLong(calCursor.getString(3));
                                Log.d(TAG, "start time: " + getDate(timeManager));
                                //its within 48 hours
                                if (timeManager > past24 && timeManager < future24) {
                                    startTime = timeManager;
                                }
                            }

                            //look at endTime
                            if (calCursor.getString(4) != null) {
                                timeManager = Long.parseLong(calCursor.getString(4));
                                Log.d(TAG, "end time: " + getDate(timeManager));
                                //its within 48 hours
                                if (timeManager > past24 && timeManager < future24) {
                                    endTime = timeManager;
                                }
                            }

                            //if times are not null
                            if (startTime != null && endTime != null) {
                                //See times in Date Readable Format
                                Log.d(TAG, "start: " + getDate(startTime) + " end: " + getDate(endTime));
                                Log.d(TAG, "current time: " + getDate(currentTime));
                                //Compare times to see if busy
                                if (currentTime > startTime && currentTime < endTime) {
                                    //we are currently at an event, populate return string:
                                    String title = calCursor.getString(1);
                                    String endTimeDate = getEndTime(endTime);
                                    returnMessage = "I'm busy with \"" + title + "\" until " + endTimeDate;
                                    android.util.Log.v("EventHandler,", "Accessing Calendar Successful and busy!");
                                    break;
                                } else {
                                    android.util.Log.v(TAG, "This Event does not take place right now!");
                                }
                            } else {
                                Log.d(TAG, "startTime or endTime was null for this event!");
                            }
                        }
                        //Cycle to next Event
                        calCursor.moveToNext();
                    }

                    //Close calCursor
                    calCursor.close();

                    //If not modified, we are free!
                    if(returnMessage == null){
                        android.util.Log.v("EventHandler,", "Accessing Calendar Successful and free!");
                    }

                } catch (Exception e) {//if exception, just
                    android.util.Log.v("EventHandler,", "Accessing Calendar Failed");
                    throw e;
                }

                //return the message
                return returnMessage;
            }
        }
        return null;
    }

    /**
     * ----------------------------------------All of the functions below are used for converting to date readable format--------------------------------------------------------------
     */

    //convert milliseconds into a date readable format
    public static String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "MM/dd/yyyy hh:mm:ss a");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    //convert milliseconds into a hour readable format
    public static String getEndTime(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "hh:mm a");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }



}


