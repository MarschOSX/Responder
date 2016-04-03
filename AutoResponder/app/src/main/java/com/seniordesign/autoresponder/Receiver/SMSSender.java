package com.seniordesign.autoresponder.Receiver;

import android.telephony.SmsManager;
import com.seniordesign.autoresponder.DataStructures.ResponseLog;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by MarschOSX on 3/23/2016
 *
 *
 */
public class SMSSender {

    private DBInstance db;
    public static final String TAG = "SMSSender";


    public SMSSender(DBInstance db) {
        this.db = db;
    }

    public void sendSMS(String messageSent, String messageRecieved, String phoneNumber, Long timeRecieved, Boolean locShared, Boolean actShared){
        android.util.Log.v(TAG, "sendSMS recieved: mesSent " + messageSent + " messageRecieved " + messageRecieved + " phoneNumber " + phoneNumber + " timeRecieved " + timeRecieved + " locShared " + locShared + " actShared " + actShared);

        //Update Response Log
        String timeRecievedReadable = getDate(timeRecieved);
        String timeSentReadable = getDate(System.currentTimeMillis());
        ResponseLog updateLog = new ResponseLog(messageSent, messageRecieved, phoneNumber, timeRecievedReadable, timeSentReadable, locShared, actShared);
        android.util.Log.v(TAG, "New ResponseLog: "+messageSent+ " " +messageRecieved+ " " +phoneNumber+ " " +timeRecievedReadable+ " " +timeSentReadable+ " " +locShared+ " " +actShared);
        db.addToResponseLog(updateLog);

        //Send the Message
        SmsManager sms = SmsManager.getDefault();
        android.util.Log.v(TAG, "Message successfully sent to: " + phoneNumber + " Message Body: " + messageSent);
        sms.sendTextMessage(phoneNumber, null, messageSent, null, null);
    }
    //convert milliseconds into a date readable format
    public static String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "MM/dd/yyyy hh:mm:ss a");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

}
