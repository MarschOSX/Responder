package com.seniordesign.autoresponder.Persistance;

import com.seniordesign.autoresponder.DataStructures.DeveloperLog;
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

    ResponseLog getFirstResponse();

    ResponseLog getLastResponse();

    ResponseLog getResponse(int index);

    ResponseLog getLastResponseByNum(String phoneNum);

    ArrayList<ResponseLog> getResponseByDateRange(Date start, Date end);

    ArrayList<ResponseLog> getResponseRange(int start, int end);

    ///////////////////////////
    //CONTACT TABLE FUNCTIONS//
    ///////////////////////////

    /////////////////////////
    //GROUP TABLE FUNCTIONS//
    /////////////////////////

    /////////////////////////////////
    //DEVELOPER LOG TABLE FUNCTIONS//
    /////////////////////////////////
    void addDevLog(Date timeStamp, String entry);

    DeveloperLog getDevLog(int index);

    ArrayList<DeveloperLog> getDevLogRange(int first, int last);

    ArrayList<DeveloperLog> getDevLogRangeByDate(Date start, Date end);
}
