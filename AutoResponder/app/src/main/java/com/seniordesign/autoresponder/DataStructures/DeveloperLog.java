package com.seniordesign.autoresponder.DataStructures;

import java.util.Date;

/**
 * Created by Garlan on 10/29/2015.
 */
public class DeveloperLog {
    private Date timeStamp;
    private String entry;

    private String logEntry;
    public DeveloperLog(Date timeStamp, String logEntry) {
        this.timeStamp = timeStamp;
        this.entry = logEntry;
    }

    public Date getTimeStamp() {
        return this.timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String setEntry() {
        return this.entry;
    }

    public void setEntry(String logEntry) {
        this.entry = logEntry;
    }
}
