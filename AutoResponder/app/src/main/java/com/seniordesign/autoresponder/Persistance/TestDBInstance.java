package com.seniordesign.autoresponder.Persistance;

import android.nfc.Tag;
import android.util.Log;

import com.seniordesign.autoresponder.DataStructures.DeveloperLog;
import com.seniordesign.autoresponder.DataStructures.ResponseLog;
import com.seniordesign.autoresponder.DataStructures.Setting;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Set;

/**
 * Created by Garlan on 10/5/2015.
 */
public class TestDBInstance implements DBInstance {
    private static final String TAG = "TestDBInstance";
    private HashMap<String,String> settings;
    private ArrayList<ResponseLog> responseLog;

    public TestDBInstance(){
        Log.d(TAG, "initializing mock database");
        this.settings = new HashMap<>();
        this.responseLog = new ArrayList<>();
        for (String[] defaultSetting : Setting.DEFAULT_SETTINGS){
            settings.put(defaultSetting[0], defaultSetting[1]);
        }
    }

    ///////////////////////////
    //SETTING TABLE FUNCTIONS//
    ///////////////////////////
    public void setReplyAll(String reply){
        this.settings.put(Setting.REPLY_ALL, reply);
    }

    public String getReplyAll(){
       return this.settings.get(Setting.REPLY_ALL);
    }

    public void setDelay(int minutes){
        this.settings.put(Setting.TIME_DELAY, Integer.toString(minutes));
    }

    public int getDelay(){
        return Integer.parseInt(this.settings.get(Setting.TIME_DELAY));
    }

    public void setResponseToggle(boolean responseToggle){
        String responseToggleText;
        if(responseToggle){
            responseToggleText = "true";
        }
        else{
            responseToggleText = "false";
        }
        this.settings.put(Setting.RESPONSE_TOGGLE, responseToggleText);
    }

    public boolean getResponseToggle(){
        String value = this.settings.get(Setting.RESPONSE_TOGGLE);
        if(value.compareTo("true") == 0){
            return true;
        }
        else if(value.compareTo("false") == 0){
            return false;
        }
        else{
            Log.e(TAG, "ERROR: getResponseToggle: found " + Setting.RESPONSE_TOGGLE + " set to " + value + " when true/false was expected");
            throw new InputMismatchException();
        }
    }

    ////////////////////////////////
    //RESPONSE LOG TABLE FUNCTIONS//
    ////////////////////////////////
    public void addToResponseLog(ResponseLog newLog){
        this.responseLog.add(newLog);
    }

    public ResponseLog getFirstResponse(){
        return this.responseLog.get(0);
    }

    public ResponseLog getLastResponse(){
        return this.responseLog.get(responseLog.size()-1);
    }

    public ResponseLog getResponse(int index){
        if (index < responseLog.size() && index >= 0){
            return this.responseLog.get(index);
        }
        else{
            Log.e(TAG, "ERROR: getResponse(): attempted to access index out of bounds: " + index);
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public ResponseLog getLastResponseByNum(String phoneNum){
        for (int i = this.responseLog.size() - 1; i >= 0; i--){
            if (responseLog.get(i).getSenderNumber().compareTo(phoneNum) == 0){
                return responseLog.get(i);
            }
        }
        //if not nothing found, returns null
        return null;
    }

    //TODO: MAKE SEARCH FOR BEGINNING AND END BINARY SEARCH FOR BETTER RUNTIME, currently O(x)
    public ArrayList<ResponseLog> getResponseByDateRange(Date start, Date end){
        ArrayList<ResponseLog> range = new ArrayList<>();

        //ERROR CHECKING
        if (start.after(end)){
            Log.e(TAG, "ERROR: getResponseByDateRange(): start date comes after end date");
            throw new InputMismatchException();
        }

        for(int i = 0; i < this.responseLog.size(); i++){
            if (this.responseLog.get(i).getTimeStamp().after(start) && this.responseLog.get(i).getTimeStamp().before(end)){
                range.add(this.responseLog.get(i));
            }
        }
        return range;
    }

    public ArrayList<ResponseLog> getResponseRange(int start, int end){
        ArrayList<ResponseLog> range = new ArrayList<>();

        //ERROR CHECKING
        if (start > end){
            Log.e(TAG, "ERROR: getResponseRange(): start index came after end index");
            throw new InputMismatchException();
        }
        //ERROR CHECKING
        if (start < 0 || end < 0 || start >= this.responseLog.size() || end >= this.responseLog.size()){
            Log.e(TAG, "ERROR: getResponseRange(): attempted to access index out of bounds: " + start + " " + end);
            throw new ArrayIndexOutOfBoundsException();
        }

        for(int i = start; i <= end; i++){
            range.add(this.responseLog.get(i));
        }

        return range;
    }

    private static String getMethodName() {
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }

    /////////////////////////////////
    //DEVELOPER LOG TABLE FUNCTIONS//
    /////////////////////////////////

    //TODO IMPLEMENT
    public void addDevLog(Date timeStamp, String entry){

    }

    //TODO IMPLEMENT
    public DeveloperLog getDevLog(int index){
        return null;
    }

    //TODO IMPLEMENT
    public ArrayList<DeveloperLog> getDevLogRange(int first, int last){
        return null;
    }

    //TODO IMPLEMENT
    public ArrayList<DeveloperLog> getDevLogRangeByDate(Date start, Date end){
        return null;
    }
}
