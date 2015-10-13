package com.seniordesign.autoresponder.Persistance;

import com.seniordesign.autoresponder.DataStructures.ResponseLog;

import java.sql.Date;
import java.util.ArrayList;

/**
 * Created by Garlan on 10/5/2015.
 */
public interface DBInstance {

    ///////////////////////////
    //SETTING TABLE FUNCTIONS//
    ///////////////////////////
    void setReplyAll(String reply);

    String getReplyAll();

    void setDelay(int minutes);

    int getDelay();

    void setResponseToggle(boolean toggle);

    boolean getResponseToggle();

    ////////////////////////////////
    //RESPONSE LOG TABLE FUNCTIONS//
    ////////////////////////////////
    void addToResponseLog(ResponseLog newLog);

    ResponseLog getFirstEntry();

    ResponseLog getLastEntry();

    ResponseLog getEntry(int index);

    ResponseLog getLastEntryByNum(String phoneNum);

    ArrayList<ResponseLog> getEntryByDateRange(Date start, Date end);

    ArrayList<ResponseLog> getEntryRange(int start, int end);
}
