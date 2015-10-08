package com.seniordesign.autoresponder.DataStructures;

import java.util.Date;

/**
 * Created by Garlan on 9/28/2015.
 */
public class Log {
    private Date timeStamp;

    private String logEntry;
    public Log(Date timeStamp, String logEntry) {
        this.timeStamp = timeStamp;
        this.logEntry = logEntry;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getLogEntry() {
        return logEntry;
    }

    public void setLogEntry(String logEntry) {
        this.logEntry = logEntry;
    }
}
