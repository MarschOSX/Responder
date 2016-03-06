package com.seniordesign.autoresponder.DataStructures;

import java.sql.Date;

/**
 * Created by Garlan on 10/8/2015.
 */
public class ResponseLog {
    private String timeReceived;
    private String timeSent;
    private String senderNumber;
    private String messageReceived;
    private String messageSent;
    boolean locationShared;
    boolean activityShared;

    public ResponseLog(String messageSent, String messageReceived, String senderNumber, String timeReceived, String timeSent, boolean locationShared, boolean activityShared) {
        this.messageSent = messageSent;
        this.messageReceived = messageReceived;
        this.senderNumber = senderNumber;
        this.timeReceived = timeReceived;
        this.timeSent = timeSent;
        this.locationShared = locationShared;
        this.activityShared = activityShared;
    }

    public String getMessageSent() {
        return messageSent;
    }

    public void setMessageSent(String messageSent) {
        this.messageSent = messageSent;
    }

    public String getMessageReceived() {
        return messageReceived;
    }

    public void setMessageReceived(String messageReceived) {
        this.messageReceived = messageReceived;
    }

    public String getSenderNumber() {
        return senderNumber;
    }

    public void setSenderNumber(String senderNumber) {
        this.senderNumber = senderNumber;
    }

    public String getTimeReceived() {
        return timeReceived;
    }

    public void setTimeReceived(String timeStamp) {
        this.timeReceived = timeStamp;
    }

    public String getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(String timeStamp) {
        this.timeSent = timeStamp;
    }

    public boolean getLocationShared() {
        return locationShared;
    }

    public void setLocationShared(Boolean location) {
        this.locationShared = location;
    }

    public boolean getActivityShared() {
        return activityShared;
    }

    public void setActivityShared(Boolean activity) {
        this.activityShared = activity;
    }



    @Override
    public String toString(){
        return this.timeReceived.toString() +", "+ this.timeSent +", " + this.senderNumber + ", " + this.messageReceived + ", " + this.messageSent +", " + this.locationShared +", "+this.activityShared;
    }
}
