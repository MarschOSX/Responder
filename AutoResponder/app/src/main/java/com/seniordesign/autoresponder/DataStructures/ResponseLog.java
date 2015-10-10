package com.seniordesign.autoresponder.DataStructures;

import java.util.Date;

/**
 * Created by Garlan on 10/8/2015.
 */
public class ResponseLog {
    private Date timeStamp;
    private String senderNumber;
    private String messageReceived;
    private String messageSent;

    public ResponseLog(String messageSent, String messageReceived, String senderNumber, Date timeStamp) {
        this.messageSent = messageSent;
        this.messageReceived = messageReceived;
        this.senderNumber = senderNumber;
        this.timeStamp = timeStamp;
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

    public Date getTimeStamp() {
        return timeStamp;
    }


    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}
