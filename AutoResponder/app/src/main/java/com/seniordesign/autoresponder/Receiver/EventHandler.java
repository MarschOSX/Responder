package com.seniordesign.autoresponder.Receiver;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.provider.CalendarContract;
import android.telephony.SmsManager;
import android.util.Log;
import com.seniordesign.autoresponder.DataStructures.Contact;
import com.seniordesign.autoresponder.DataStructures.ResponseLog;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/*
 * Created by MarschOSX on 10/8/2015.
 */


public class EventHandler extends ListActivity{
    private static final String TAG = "EventHandler";
    private DBInstance db;
    private Context context;
    private GoogleLocator locator;
    private String phoneNumber;
    private String messageReceived;
    private Long timeReceived;
    Context getContext;

    public EventHandler(DBInstance db) {
        this.db = db;
    }

    public int respondToText(String phoneNumber, String messageRecieved, Long timeRecieved, Context context) {
        //EventListener passes info to EventHandler
        this.context = context;
        this.phoneNumber = phoneNumber;
        this.messageReceived = messageRecieved;
        this.timeReceived = timeRecieved;

        getContext = context;
        android.util.Log.v("EventHandler,", "EventHandler is active!");
        if (phoneNumber == null){
            android.util.Log.v("EventHandler,", "Invalid PhoneNumber Recieved");
            return -1;
        }
        Contact contact = db.getContactInfo(phoneNumber);
        if (db.getResponseToggle() && contact != null) {
            //get lastRecieved from database
            ResponseLog lastLog = db.getLastResponseByNum(phoneNumber);
            if (lastLog == null) {
                android.util.Log.v("EventHandler,", "Invalid, UpdateLog is NULL");
                return -1;
            }
            Long lastRecieved = lastLog.getTimeStamp().getTime();
            //get delaySet from database
            Long delaySet = 60000 * (long) db.getDelay();//convert minutes to milliseconds
            if (timeRecieved != null && timeRecieved >= 0L) {
                if (lastRecieved == 0 || lastRecieved + delaySet < timeRecieved) {
                    //get response from Database and set as message
                    String contactResponse = contact.getResponse();
                    Boolean locationPermission = contact.isLocationPermission();
                    Boolean activityPermission = contact.isActivityPermission();

                    if(!db.getLocationToggle()){
                        locationPermission = false;
                    }
                    if(!db.getActivityToggle()){
                        activityPermission = false;
                    }

                    if(locationPermission) {//if just Location permission is true
                        if (this.messageReceived.toLowerCase().contains("where are you") ||
                                this.messageReceived.toLowerCase().contains("where r you") ||
                                this.messageReceived.toLowerCase().contains("where are u") ||
                                this.messageReceived.toLowerCase().contains("where r u"))
                            //create locator and pass a reference of this object
                            locator = new GoogleLocator(context, this);
                    }
                    if (activityPermission) {//if just Activity permission is true
                        String actMessage = getActivityInfo(messageRecieved);
                        if(actMessage != null){
                            sendSMS(actMessage, messageRecieved, phoneNumber, new Date(timeRecieved));
                        }else{
                            sendSMS(contactResponse, messageRecieved, phoneNumber, new Date(timeRecieved));
                        }
                    }
                    if (!locationPermission && !activityPermission){//both permissions are false, go to normal response
                        sendSMS(contactResponse, messageRecieved, phoneNumber, new Date(timeRecieved));
                    }
                    android.util.Log.v("EventHandler,", "Finished EventHandler");
                    return 0;
                } else {
                    android.util.Log.v("EventHandler,", "Cannot send a response yet due to delay!");
                    return -1;
                }
            } else {
                android.util.Log.v("EventHandler,", "Invalid Time Recieved");
                return -1;
            }
        }
        android.util.Log.v("EventHandler,", "No Text Sent! Toggle OFF or Null Contact");
        return -1;
    }

    //Sends out an SMS from the device and records it in the ResponseLog
    public void sendSMS(String messageSent, String messageRecieved, String phoneNumber, Date timeRecieved){
        //Send the GeneralResponse Message
        SmsManager sms = SmsManager.getDefault();
        android.util.Log.v("EventHandler,", "Message successfully sent to: " + phoneNumber + " Message Body: " + messageSent);
        sms.sendTextMessage(phoneNumber, null, messageSent, null, null);

        //Update Response Log
        ResponseLog updateLog = new ResponseLog(messageSent, messageRecieved, phoneNumber, timeRecieved);
        db.addToResponseLog(updateLog);
    }

    public void sendLocationInfo() {
        String addressText;
        String link;
        String message;

        //once called by the locator, retrieve the raw location and human friendly address
        Location currentLocation = locator.getCurrentLocation();
        Address currentAddress = locator.getCurrentAddress();

        //locator is no longer needed, close it.
        locator.close();

        if (currentLocation == null) {
            sendSMS("my location cannot be determined at this time", this.messageReceived, this.phoneNumber, new Date(this.timeReceived));
        } else {

            //build the location message w/ address and URL to google maps
            addressText = currentAddress.getAddressLine(0) + " " + currentAddress.getAddressLine(1) + " " + currentAddress.getAddressLine(2);
            link = "http://maps.google.com/?q=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude();
            message = "I am at: \n" + addressText + "\n\n" + link;

            //send the message
            sendSMS(message, this.messageReceived, this.phoneNumber, new Date(this.timeReceived));
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
                    ContentResolver contentResolver = getContext.getContentResolver();
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


