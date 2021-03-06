package com.seniordesign.autoresponder.Persistance;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.seniordesign.autoresponder.DataStructures.Contact;
import com.seniordesign.autoresponder.DataStructures.Group;
import com.seniordesign.autoresponder.DataStructures.ResponseLog;
import com.seniordesign.autoresponder.DataStructures.Setting;
import com.seniordesign.autoresponder.Services.AlarmService;
import com.seniordesign.autoresponder.Services.DrivingDetectionService;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Garlan on 9/28/2015.
 */

//note for setting up transactions: goto https://docs.oracle.com/javase/tutorial/jdbc/basics/transactions.html
public class PermDBInstance implements DBInstance {
    private static final String TAG = "PermDBInstance";
    private SQLiteDatabase myDB;
    private Context context;


    public PermDBInstance(Context context) {
        //this.mDB = SQLiteDatabase.openOrCreateDatabase(DATABASE_NAME, null);
        Log.d(TAG, "initializing database instance");

        //access DB via helper class
        DBHelper myLittleHelper = new DBHelper(context);
        this.myDB = myLittleHelper.getWritableDatabase();
        this.context = context;
    }


    ///////////////////////////
    //SETTING TABLE FUNCTIONS//
    ///////////////////////////


    public void setReplyAll(String reply){
        Log.d(TAG, "setting replyAll to " + reply + "....");
        this.setGroupResponse(Group.DEFAULT_GROUP, reply);
    }

    public String getReplyAll(){
        return this.getGroupInfo(Group.DEFAULT_GROUP).getResponse();
    }

    public void setUniversalReply(String reply){
        Log.d(TAG, "setting universalReply to " + reply + "....");
        update(DBHelper.TABLE_SETTINGS, DBHelper.SETTING_NAME[0], Setting.UNIVERSAL_REPLY, DBHelper.SETTING_VALUE[0], reply);
        //this.setGroupResponse(Group.DEFAULT_GROUP, reply);
    }

    public String getUniversalReply(){
        final String query =
                "SELECT " + DBHelper.SETTING_VALUE[0] +
                 " FROM " + DBHelper.TABLE_SETTINGS +
                 " WHERE " + DBHelper.SETTING_NAME[0] + "=" + "\"" + Setting.UNIVERSAL_REPLY + "\"";

        //query db and ensure object was returned
        Cursor result = myDB.rawQuery(query, null);
        String response;
        if ((result != null) && (result.moveToFirst())){ // move pointer to first row
            //load setting value into string
            response = result.getString(result.getColumnIndex(DBHelper.SETTING_VALUE[0]));

            //close the cursor
            result.close();

            //return the query result
            Log.d(TAG, getMethodName(0) + ": universal reply is " + response);
            return response;
        }
        else {
            Log.e(TAG, "ERROR: " + getMethodName(0) + ": could not access cursor object from: " + query);
            return null;
        }
    }

    public void setDelay(int minutes){
        Log.d(TAG, "setting delay to "+ minutes +"....");
        update(DBHelper.TABLE_SETTINGS, DBHelper.SETTING_NAME[0], Setting.TIME_DELAY, DBHelper.SETTING_VALUE[0], minutes);
    }

    //will return -1 if no result returned
    public int getDelay(){
        return getSetting_int(Setting.TIME_DELAY);
    }

    public void setDrivingDetectionInterval(float minutes){
        Log.d(TAG, "setting interval to "+ minutes +"....");
        update(DBHelper.TABLE_SETTINGS, DBHelper.SETTING_NAME[0], Setting.DRIVING_DETECTION_INTERVAL, DBHelper.SETTING_VALUE[0], minutes);
    }

    public float getDrivingDetectionInterval(){
        return getSetting_float(Setting.DRIVING_DETECTION_INTERVAL);
    }

    public void setTimeLimit(int hours){
        Log.d(TAG, "setting delay to "+ hours +"....");
        update(DBHelper.TABLE_SETTINGS, DBHelper.SETTING_NAME[0], Setting.TIME_LIMIT, DBHelper.SETTING_VALUE[0], hours);
    }

    //will return -1 if no result returned
    public int getTimeLimit(){
        return getSetting_int(Setting.TIME_LIMIT);
    }


    public void setTimeResponseToggleSet(long milliseconds){
        Log.d(TAG, "setting TimeResponseToggleSet to "+ milliseconds +" milliseconds....");
        updateForLong(DBHelper.TABLE_SETTINGS, DBHelper.SETTING_NAME[0], Setting.RESPONSE_TOGGLE_TIME_SET, DBHelper.SETTING_VALUE[0], milliseconds);
    }

    //will return -1 if no result returned
    public long getTimeResponseToggleSet(){
        return getSetting_long(Setting.RESPONSE_TOGGLE_TIME_SET);
    }

/*    public void setDrivingDetectionPeriod(int minutes){
        Log.d(TAG, "setting delay to "+ minutes +"....");
        update(DBHelper.TABLE_SETTINGS, DBHelper.SETTING_NAME[0], Setting.DRIVING_DETECTION_INTERVAL, DBHelper.SETTING_VALUE[0], minutes);
    }

    //will return -1 if no result returned
    public int getDrivingDetectionPeriod(){
        return getSetting_int(Setting.DRIVING_DETECTION_INTERVAL);
    }*/

    public void setResponseToggle(boolean responseToggle){
        Log.d(TAG, "setting  responseToggle to " + responseToggle + "....");
        update(DBHelper.TABLE_SETTINGS, DBHelper.SETTING_NAME[0], Setting.RESPONSE_TOGGLE, DBHelper.SETTING_VALUE[0], responseToggle);
    }

    public void setActivityToggle(boolean responseToggle){
        Log.d(TAG, "setting  activityToggle to " + responseToggle + "....");
        update(DBHelper.TABLE_SETTINGS, DBHelper.SETTING_NAME[0], Setting.ACTIVITY_TOGGLE, DBHelper.SETTING_VALUE[0], responseToggle);
    }

    public void setLocationToggle(boolean responseToggle){
        Log.d(TAG, "setting  locationToggle to " + responseToggle + "....");
        update(DBHelper.TABLE_SETTINGS, DBHelper.SETTING_NAME[0], Setting.LOCATION_TOGGLE, DBHelper.SETTING_VALUE[0], responseToggle);
    }

    public void setUniversalToggle(boolean responseToggle){
        Log.d(TAG, "setting  universal toggle to " + responseToggle + "....");
        update(DBHelper.TABLE_SETTINGS, DBHelper.SETTING_NAME[0], Setting.UNIVERSAL_TOGGLE, DBHelper.SETTING_VALUE[0], responseToggle);
    }

    public void setWorldToggle(boolean worldToggle){
        Log.d(TAG, "setting  world toggle to " + worldToggle + "....");
        update(DBHelper.TABLE_SETTINGS, DBHelper.SETTING_NAME[0], Setting.WORLD_TOGGLE, DBHelper.SETTING_VALUE[0], worldToggle);
    }

    public boolean getIsDriving(){
        if (DrivingDetectionService.isRunning(context)) {
            return getSetting_bool(Setting.IS_DRIVING);
        }
        else{
            this.setIsDriving(false);
            return false;
        }
    }

    public void setIsDriving(boolean status){
        Log.d(TAG, "setting  isDriving to " + status + "....");
        update(DBHelper.TABLE_SETTINGS, DBHelper.SETTING_NAME[0], Setting.IS_DRIVING, DBHelper.SETTING_VALUE[0], status);
    }

    /**
    Parental Controls Setters
     */
    public void setParentalControlsToggle(boolean parentalControlsToggle){
        Log.d(TAG, "setting  parentalControlsToggle to " + parentalControlsToggle + "....");
        update(DBHelper.TABLE_SETTINGS, DBHelper.SETTING_NAME[0], Setting.PARENTAL_CONTROLS_TOGGLLE, DBHelper.SETTING_VALUE[0], parentalControlsToggle);
    }

    public void setDrivingDetectionToggle(boolean toggle){
        Log.d(TAG, "setting  DRIVING_DETECTION_TOGGLE to " + toggle + "....");
        update(DBHelper.TABLE_SETTINGS, DBHelper.SETTING_NAME[0], Setting.DRIVING_DETECTION_TOGGLE, DBHelper.SETTING_VALUE[0], toggle);
    }

    public void setParentalControlsNumber(String parentalControlsNumber){
        Log.d(TAG, "setting  parentalControlsNumber to " + parentalControlsNumber + "....");
        update(DBHelper.TABLE_SETTINGS, DBHelper.SETTING_NAME[0], Setting.PARENTAL_CONTROLS_NUMBER, DBHelper.SETTING_VALUE[0], parentalControlsNumber);
    }

    public void setParentalControlsPassword(String password){
        Log.d(TAG, "setting  parentalControlsPasssword to " + password + "....");
        update(DBHelper.TABLE_SETTINGS, DBHelper.SETTING_NAME[0], Setting.PARENTAL_CONTROLS_PASSWORD, DBHelper.SETTING_VALUE[0], password);
    }

    public boolean getResponseToggle(){
        Log.d(TAG, "Getting Response Toggle");
        return getSetting_bool(Setting.RESPONSE_TOGGLE);
    }

    public boolean getActivityToggle(){
        return getSetting_bool(Setting.ACTIVITY_TOGGLE);
    }

    public boolean getLocationToggle(){
        return getSetting_bool(Setting.LOCATION_TOGGLE);
    }

    public void setDailyNoticeTime(int hour, int minute){
        update(DBHelper.TABLE_SETTINGS, DBHelper.SETTING_NAME[0], Setting.DAILY_NOTICE_TIME_HR, DBHelper.SETTING_VALUE[0], hour);
        update(DBHelper.TABLE_SETTINGS, DBHelper.SETTING_NAME[0], Setting.DAILY_NOTICE_TIME_MIN, DBHelper.SETTING_VALUE[0], minute);

    }

    public int getDailyNoticeTime_hour(){
        return getSetting_int(Setting.DAILY_NOTICE_TIME_HR);
    }

    public int getDailyNoticeTime_minute(){
        return getSetting_int(Setting.DAILY_NOTICE_TIME_MIN);
    }

    /**
     Parental Controls getters
     */
    public boolean getParentalControlsToggle(){
        return getSetting_bool(Setting.PARENTAL_CONTROLS_TOGGLLE);
    }

    public boolean getDrivingDetectionToggle(){
        return getSetting_bool(Setting.DRIVING_DETECTION_TOGGLE);
    }

    public String getParentalControlsNumber(){
        return getSetting_str(Setting.PARENTAL_CONTROLS_NUMBER);
    }

    public String getParentalControlsPassword(){
        return getSetting_str(Setting.PARENTAL_CONTROLS_PASSWORD);
    }


    public boolean getUniversalToggle(){
        final String query =
                "SELECT " + DBHelper.SETTING_VALUE[0] +
                        " FROM " + DBHelper.TABLE_SETTINGS +
                        " WHERE " + DBHelper.SETTING_NAME[0] + " = " + "\"" + Setting.UNIVERSAL_TOGGLE + "\"";

        //query database and ensure cursor returned is valid
        Cursor result = myDB.rawQuery(query, null);
        boolean toggle;
        if ((result != null) && (result.moveToFirst())){

            //retrieve setting value
            String response = result.getString(result.getColumnIndex(DBHelper.SETTING_VALUE[0]));

            //determine if value is true or false and returns as translates into a boolean
            if( response.compareTo("true") == 0){
                toggle = true;
            }
            else if ( response.compareTo("false") == 0){
                toggle = false;
            }
            else{
                Log.e(TAG, "ERROR: " + getMethodName(0) + ": found " + response + " when a value of true or false was expected from: " + query);
                throw new InputMismatchException();
            }
            result.close();
        } else {
            Log.e(TAG, "ERROR: " + getMethodName(0) + ": could not get/access cursor object from: " + query);
            throw new NullPointerException();
        }
        Log.d(TAG, getMethodName(0) + ": toggle is " + toggle);
        return toggle;
    }



    public boolean getWorldToggle(){
        final String query =
                "SELECT " + DBHelper.SETTING_VALUE[0] +
                        " FROM " + DBHelper.TABLE_SETTINGS +
                        " WHERE " + DBHelper.SETTING_NAME[0] + " = " + "\"" + Setting.WORLD_TOGGLE + "\"";

        //query database and ensure cursor returned is valid
        Cursor result = myDB.rawQuery(query, null);
        boolean toggle;
        if ((result != null) && (result.moveToFirst())){

            //retrieve setting value
            String response = result.getString(result.getColumnIndex(DBHelper.SETTING_VALUE[0]));

            //determine if value is true or false and returns as translates into a boolean
            if( response.compareTo("true") == 0){
                toggle = true;
            }
            else if ( response.compareTo("false") == 0){
                toggle = false;
            }
            else{
                Log.e(TAG, "ERROR: " + getMethodName(0) + ": found " + response + " when a value of true or false was expected from: " + query);
                throw new InputMismatchException();
            }
            result.close();
        } else {
            Log.e(TAG, "ERROR: " + getMethodName(0) + ": could not get/access cursor object from: " + query);
            throw new NullPointerException();
        }
        Log.d(TAG, getMethodName(0) + ": toggle is " + toggle);
        return toggle;
    }
    ////////////////////////////////
    //RESPONSE LOG TABLE FUNCTIONS//
    ////////////////////////////////

    public void addToResponseLog(ResponseLog newLog){
        Log.d(TAG, "adding entry to ResponseLog....");
        myDB.beginTransaction();
        try {
            //load columns in args
            ContentValues args = new ContentValues();
            args.put(DBHelper.RESPONSELOG_TIMERECEIVED[0], newLog.getTimeReceived());
            args.put(DBHelper.RESPONSELOG_TIMESENT[0], newLog.getTimeSent());
            args.put(DBHelper.RESPONSELOG_SENDERNUM[0], newLog.getSenderNumber());
            args.put(DBHelper.RESPONSELOG_MESSAGERCV[0], newLog.getMessageReceived());
            args.put(DBHelper.RESPONSELOG_MESSAGESNT[0], newLog.getMessageSent());
            args.put(DBHelper.RESPONSELOG_LOCATIONSHARED[0], convertBool(newLog.getLocationShared()));
            args.put(DBHelper.RESPONSELOG_ACTIVITYSHARED[0], convertBool(newLog.getActivityShared()));

            //add the row to the table and checks if insert was succesfull
            long insert = myDB.insertOrThrow(DBHelper.TABLE_RESPONSELOG, null, args);
            if (insert != -1) {
                myDB.setTransactionSuccessful();
                Log.d(TAG, "entry added to ResponseLog " + newLog.toString());
            }
        }
        catch (Exception e){
            Log.e(TAG, "ERROR: " + getMethodName(0) + " failed");
            throw e;
        }
        finally {
            myDB.endTransaction();
        }
    }

    public ResponseLog getLastResponseByNum(String phoneNum){

        String senderNum = phoneNum;
        String msgRcv = "hi";
        String msgSnt = "hi";
        String dateRecieved = "";
        String dateSent = "";
        Boolean locShared = false;
        Boolean actShared = false;

        String table = DBHelper.TABLE_RESPONSELOG;
        String columns[] = {"MAX(" + DBHelper.RESPONSELOG_TIMERECEIVED[0] + ")",
                DBHelper.RESPONSELOG_TIMESENT[0],
                DBHelper.RESPONSELOG_SENDERNUM[0],
                DBHelper.RESPONSELOG_MESSAGERCV[0],
                DBHelper.RESPONSELOG_MESSAGESNT[0],
                DBHelper.RESPONSELOG_LOCATIONSHARED[0],
                DBHelper.RESPONSELOG_ACTIVITYSHARED[0]};
        String selection = DBHelper.RESPONSELOG_SENDERNUM[0] + "=?";
        String selectionArgs[] = {phoneNum};


        try {
            Cursor result = this.myDB.query(table, columns, selection, selectionArgs, null, null, null);
            if (result != null) {
                //check and see how many rows were returned
                int numRows = result.getCount();
                Log.d(TAG, getMethodName(0) + ": number of rows found is " + numRows);
                if (numRows ==  0) {
                    Log.d(TAG, "ResponseLogRetrieved: "+ dateRecieved + ", " + dateSent +", " + senderNum + ", " + msgSnt + ", " + msgRcv +", " + locShared +", " + actShared);
                    return new ResponseLog(msgSnt, msgRcv, senderNum, dateRecieved, dateSent, locShared, actShared); //returns a false record from the beginning of time if no record found
                }
                else{
                    result.moveToFirst();
                }

                if (result.getLong(0) == 0L) {
                    Log.d(TAG, "ResponseLogRetrieved: " + dateRecieved + ", " + dateSent + ", " + senderNum + ", " + msgSnt + ", " + msgRcv + ", " + locShared + ", " + actShared);
                    return new ResponseLog(msgSnt, msgRcv, senderNum, dateRecieved, dateSent, locShared, actShared); //returns a false record from the beginning of time if no record found
                }

                //load query result
                dateRecieved = result.getString(0);
                dateSent = result.getString(1);
                senderNum = result.getString(2);
                msgRcv = result.getString(3);
                msgSnt = result.getString(4);
                locShared = convertToBool(result.getString(5));
                actShared = convertToBool(result.getString(6));

                Log.d(TAG, "ResponseLogRetrieved:("+ numRows +") "+ dateRecieved + ", " + dateSent +", " + senderNum + ", " + msgSnt + ", " + msgRcv +", " + locShared +", " + actShared);

                result.close();

                return new ResponseLog(msgSnt, msgRcv, senderNum, dateRecieved, dateSent, locShared, actShared);
            } else {
                Log.e(TAG, "ERROR: " + getMethodName(0) + ": could not access cursor object" );
                return null;
            }
        }
        catch (Exception e){
            Log.e(TAG, "ERROR: " + getMethodName(0) + " could not retrieve data");
            throw e;
        }
    }

    public ResponseLog getLastResponse(){

        String senderNumber;
        String messageRecieved;
        String messageSent;
        String timeRecieved;
        String timeSent;
        Boolean locShared;
        Boolean actShared;

        String table = DBHelper.TABLE_RESPONSELOG;
        String columns[] = {"MAX(" + DBHelper.RESPONSELOG_TIMESENT[0] + ")",
                DBHelper.RESPONSELOG_TIMERECEIVED[0],
                DBHelper.RESPONSELOG_SENDERNUM[0],
                DBHelper.RESPONSELOG_MESSAGERCV[0],
                DBHelper.RESPONSELOG_MESSAGESNT[0],
                DBHelper.RESPONSELOG_LOCATIONSHARED[0],
                DBHelper.RESPONSELOG_ACTIVITYSHARED[0]};

        try {
            Cursor result = this.myDB.query(table, columns, null, null, null, null, null);
            if (result != null) {
                int numRows = result.getCount();
                if (numRows ==  0) {
                    Log.d(TAG, getMethodName(0) + "No ResponseLogs Found!");
                    return null;
                }else{
                    result.moveToFirst();
                }
                if (result.getLong(0) == 0L) {
                    Log.d(TAG, getMethodName(0) + "First ResponseLog is Empty!");
                    return null;
                }

                //load query result
                timeRecieved = result.getString(1);
                timeSent = result.getString(0);
                senderNumber = result.getString(2);
                messageRecieved = result.getString(3);
                messageSent = result.getString(4);
                locShared = convertToBool(result.getString(5));
                actShared = convertToBool(result.getString(6));

                //Generate Response Log
                ResponseLog responseLog = new ResponseLog(messageSent, messageRecieved, senderNumber, timeRecieved, timeSent, locShared, actShared);
                Log.d(TAG, getMethodName(0) + ": " + responseLog.toString());
                result.close();
                return responseLog;
            } else {
                Log.e(TAG, "ERROR: " + getMethodName(0) + ": could not access cursor object" );
                return null;
            }
        }
        catch (Exception e){
            Log.e(TAG, "ERROR: " + getMethodName(0) + " could not retrieve data");
            throw e;
        }
    }


    //returns sorted responseLogs by timeStamp
    public ArrayList<ResponseLog> getResponseLogList(){

        String table = DBHelper.TABLE_RESPONSELOG;
        String orderBy = "(" + DBHelper.RESPONSELOG_TIMERECEIVED[0] + ") ASC";

        try {
            Cursor result = this.myDB.query(table, null, null, null, null, null, orderBy);

            ArrayList<ResponseLog> range = new ArrayList<>();
            String timeRecieved;
            String timeSent;
            String senderNumber;
            String messageRecieved;
            String messageSent;
            Boolean locShared;
            Boolean actShared;

            if (result != null) {

                //Checks for Cursor 0, Issue #77
                int numRows = result.getCount();
                Log.d(TAG, getMethodName(0) + ": number of rows found is " + numRows);
                if (numRows ==  0) {
                    Log.d(TAG, getMethodName(0) + "No ResponseLogs Found! Returning Empty Array");
                    return range;
                }
                else{
                    result.moveToFirst();
                }
                /*if (result.getLong(0) == 0L) {
                    Log.d(TAG, getMethodName(0) + "First ResponseLog is Empty! Returning Empty Array");
                    return range;
                }*/

                for (int i = 0; i < numRows; i++) {
                    //load query result
                    timeRecieved = result.getString(0);
                    timeSent = result.getString(1);
                    senderNumber = result.getString(2);
                    messageRecieved = result.getString(3);
                    messageSent = result.getString(4);
                    locShared = convertToBool(result.getString(5));
                    actShared = convertToBool(result.getString(6));

                    //Generate Response Log
                    ResponseLog responseLog = new ResponseLog(messageSent, messageRecieved, senderNumber, timeRecieved, timeSent, locShared, actShared);
                    Log.d(TAG, getMethodName(0) + ": " + responseLog.toString());
                    range.add(responseLog);
                    result.moveToNext();
                }
                result.close();

                Log.d(TAG, getMethodName(0) + "Range Size is " + range.size());
                Log.d(TAG, getMethodName(0) + "Returning Range of Response Logs-->");

                for(ResponseLog log : range){
                    Log.e(TAG, log.toString());
                }

                return range;
            } else {
                Log.e(TAG, "ERROR: " + getMethodName(0) + ": could not access cursor object");
                return null;
            }
        }catch(Exception e){
            Log.e(TAG, "ERROR: " + getMethodName(0) + " could not retrieve Response Logs");
            throw e;
        }
    }


    @Override
    public void deleteResponseLogs() {
        myDB.execSQL("DELETE FROM " +  DBHelper.TABLE_RESPONSELOG );
    }



    ///////////////////////////
    //CONTACT TABLE FUNCTIONS//
    ///////////////////////////

    public int addContact(Contact newContact){
        Log.d(TAG, "adding contact(" + newContact.toString() + ")....");

        //verify first that the group does exist
        Group g = this.getGroupInfo(newContact.getGroupName());
        if (g == null){
            return -1;
        }

        myDB.beginTransaction();
        try {

            //load columns in args
            ContentValues args = new ContentValues();
            args.put(DBHelper.CONTACT_NAME[0], newContact.getName());
            args.put(DBHelper.CONTACT_PHONENUM[0], newContact.getPhoneNumber());
            args.put(DBHelper.CONTACT_GROUP[0], newContact.getGroupName());
            args.put(DBHelper.CONTACT_RESPONSE[0], newContact.getResponse());
            args.put(DBHelper.CONTACT_ACTIVITYPERM[0], convertBool(newContact.isActivityPermission()));
            args.put(DBHelper.CONTACT_LOCATIONPERM[0], convertBool(newContact.isLocationPermission()));
            args.put(DBHelper.CONTACT_INHERITANCE[0], convertBool(newContact.isInheritance()));

            //add the row to the table and checks if insert was succesfull
            long insert = myDB.insertOrThrow(DBHelper.TABLE_CONTACT, null, args);
            if (insert != -1) {
                myDB.setTransactionSuccessful();
                Log.d(TAG, getMethodName(0) + ":"  + newContact.toString() + " added successfully");
            }
        }
        catch (Exception e){
            Log.e(TAG, "ERROR: " + getMethodName(0) + " failed");
            throw e;
        }
        finally {
            myDB.endTransaction();
        }

        return 0;
    }

    public int removeContact(String phoneNum){
        Log.d(TAG, "removing " + phoneNum + " from contacts....");
        return remove(DBHelper.TABLE_CONTACT, DBHelper.CONTACT_PHONENUM[0] + "=\"" + phoneNum + "\"");
    }

    public int setContactName(String phoneNum, String newName){
        Log.d(TAG, "setting name of " + phoneNum + " to " + newName + ".....");
        return update(DBHelper.TABLE_CONTACT, DBHelper.CONTACT_PHONENUM[0], phoneNum, DBHelper.CONTACT_NAME[0], newName);
    }

    public int setContactNumber(String oldPhoneNum, String newPhoneNum){
        Log.d(TAG, "changing phone number " + oldPhoneNum + " to " + newPhoneNum + ".....");
        return update(DBHelper.TABLE_CONTACT, DBHelper.CONTACT_PHONENUM[0], oldPhoneNum ,DBHelper.CONTACT_PHONENUM[0], newPhoneNum);
    }

    public int setContactLocationPermission(String phoneNum, boolean permission){
        Log.d(TAG, "setting location permission of " + phoneNum + " to " + permission + ".....");
        return update(DBHelper.TABLE_CONTACT, DBHelper.CONTACT_PHONENUM[0], phoneNum ,DBHelper.CONTACT_LOCATIONPERM[0], permission);
    }

    public int setContactActivityPermission(String phoneNum, boolean permission){
        Log.d(TAG, "setting activity permission of " + phoneNum + " to " + permission + ".....");
        return update(DBHelper.TABLE_CONTACT, DBHelper.CONTACT_PHONENUM[0], phoneNum ,DBHelper.CONTACT_ACTIVITYPERM[0], permission);
    }

    public int setContactGroup(String phoneNum, String groupName){
        Log.d(TAG, "setting group of " + phoneNum + " to " + groupName + ".....");
        if (this.getGroupInfo(groupName) != null) return update(DBHelper.TABLE_CONTACT, DBHelper.CONTACT_PHONENUM[0], phoneNum ,DBHelper.CONTACT_GROUP[0], groupName);
        else return -1;
    }

    public int setContactInheritance(String phoneNum, boolean inheritance){
        Log.d(TAG, "setting inheritance of " + phoneNum + " to " + inheritance + ".....");
        return update(DBHelper.TABLE_CONTACT, DBHelper.CONTACT_PHONENUM[0], phoneNum ,DBHelper.CONTACT_INHERITANCE[0], inheritance);
    }

    public int setContactResponse(String phoneNum, String response){
        Log.d(TAG, "setting response of " + phoneNum + " to " + response + ".....");
        return update(DBHelper.TABLE_CONTACT, DBHelper.CONTACT_PHONENUM[0], phoneNum ,DBHelper.CONTACT_RESPONSE[0], response);
    }

    public Contact getContactInfo(String phoneNum){

        String table = DBHelper.TABLE_CONTACT;
        String selection = DBHelper.CONTACT_PHONENUM[0] + "=?";
        String selectionArgs[] = {phoneNum};

        Cursor result = this.myDB.query(table, null, selection, selectionArgs, null, null, null);

        Contact c;

        String name;
        String phoneNumber;
        String groupName;
        String response;
        boolean locationPermission;
        boolean activityPermission;
        boolean inheritance;

        if ((result != null) && (result.moveToFirst())){

            name = result.getString(result.getColumnIndex(DBHelper.CONTACT_NAME[0]));
            phoneNumber = result.getString(result.getColumnIndex(DBHelper.CONTACT_PHONENUM[0]));
            groupName = result.getString(result.getColumnIndex(DBHelper.CONTACT_GROUP[0]));
            inheritance =  convertToBool(result.getString(result.getColumnIndex(DBHelper.CONTACT_INHERITANCE[0])));

            if (inheritance){
                Group parent = this.getGroupInfo(groupName);
                response = parent.getResponse();
                locationPermission = parent.isLocationPermission();
                activityPermission = parent.isActivityPermission();
            }
            else {
                response = result.getString(result.getColumnIndex(DBHelper.CONTACT_RESPONSE[0]));
                locationPermission = convertToBool(result.getString(result.getColumnIndex(DBHelper.CONTACT_LOCATIONPERM[0])));
                activityPermission = convertToBool(result.getString(result.getColumnIndex(DBHelper.CONTACT_ACTIVITYPERM[0])));
            }

            c = new Contact(name, phoneNumber, groupName, response, locationPermission, activityPermission, inheritance);

            result.close();
            Log.d(TAG, getMethodName(0) + " contact info for " + phoneNum + " is " + c.toString());
            return c;
        }
        else {
            Log.e(TAG, "ERROR: " + getMethodName(0) + ": no rows returned ");
            return null;
        }
    }

    //returns sorted A - Z by name
    public ArrayList<Contact> getContactList(){

        String table = DBHelper.TABLE_CONTACT;
        String orderBy = "(" + DBHelper.CONTACT_NAME[0] + ") ASC";

        Cursor result = this.myDB.query(table, null, null, null, null, null, orderBy);

        ArrayList<Contact> range = new ArrayList<>();
        String name;
        String phoneNumber;
        String groupName;
        String response;
        boolean locationPermission;
        boolean activityPermission;
        boolean inheritance;

        if ((result != null) && (result.moveToFirst())){

            for (int i = 0; i < result.getCount(); i++){
                name = result.getString(result.getColumnIndex(DBHelper.CONTACT_NAME[0]));
                phoneNumber = result.getString(result.getColumnIndex(DBHelper.CONTACT_PHONENUM[0]));
                groupName = result.getString(result.getColumnIndex(DBHelper.CONTACT_GROUP[0]));
                inheritance =  convertToBool(result.getString(result.getColumnIndex(DBHelper.CONTACT_INHERITANCE[0])));
                Log.d(TAG, "Contact Info: " + name +" " + phoneNumber +" " + groupName +" " + inheritance);


                if (inheritance){
                    Group parent = this.getGroupInfo(groupName);
                    response = parent.getResponse();
                    locationPermission = parent.isLocationPermission();
                    activityPermission = parent.isActivityPermission();
                }
                else {
                    response = result.getString(result.getColumnIndex(DBHelper.CONTACT_RESPONSE[0]));
                    locationPermission = convertToBool(result.getString(result.getColumnIndex(DBHelper.CONTACT_LOCATIONPERM[0])));
                    activityPermission = convertToBool(result.getString(result.getColumnIndex(DBHelper.CONTACT_ACTIVITYPERM[0])));
                }


                Contact c = new Contact(name, phoneNumber, groupName, response, locationPermission, activityPermission, inheritance);
                Log.d(TAG, getMethodName(0) +": "+ c.toString());
                range.add(c);
                result.moveToNext();
            }

            result.close();

            return range;
        }
        else {
            Log.e(TAG, "ERROR: " + getMethodName(0) + ": could not access cursor object");
            return null;
        }
    }

    public ArrayList<Contact> getGroup(String group){

        String table = DBHelper.TABLE_CONTACT;
        String selection = DBHelper.CONTACT_GROUP[0] + "=?";
        String selectionArgs[] = {group};
        String orderBy = "(" + DBHelper.CONTACT_NAME[0] + ") ASC";

        Cursor result = this.myDB.query(table, null, selection, selectionArgs, null, null, orderBy);

        ArrayList<Contact> range = new ArrayList<>();
        String name;
        String phoneNumber;
        String groupName;
        String response;

        boolean locationPermission;
        boolean activityPermission;
        boolean inheritance;

        if ((result != null) && (result.moveToFirst())){

            for (int i = 0; i < result.getCount(); i++){
                name = result.getString(result.getColumnIndex(DBHelper.CONTACT_NAME[0]));
                phoneNumber = result.getString(result.getColumnIndex(DBHelper.CONTACT_PHONENUM[0]));
                groupName = result.getString(result.getColumnIndex(DBHelper.CONTACT_GROUP[0]));
                inheritance =  convertToBool(result.getString(result.getColumnIndex(DBHelper.CONTACT_INHERITANCE[0])));

                if (inheritance){
                    Group parent = this.getGroupInfo(groupName);
                    response = parent.getResponse();
                    locationPermission = parent.isLocationPermission();
                    activityPermission = parent.isActivityPermission();
                }
                else {
                    response = result.getString(result.getColumnIndex(DBHelper.CONTACT_RESPONSE[0]));
                    locationPermission = convertToBool(result.getString(result.getColumnIndex(DBHelper.CONTACT_LOCATIONPERM[0])));
                    activityPermission = convertToBool(result.getString(result.getColumnIndex(DBHelper.CONTACT_ACTIVITYPERM[0])));
                }

                Contact c = new Contact(name, phoneNumber, groupName, response, locationPermission, activityPermission, inheritance);
                Log.d(TAG, getMethodName(0) +": "+ c.toString());
                range.add(c);

                result.moveToNext();
            }

            result.close();

            return range;
        }
        else {
            Log.e(TAG, "ERROR: " + getMethodName(0) + ": could not access cursor object");
            return null;
        }
    }

    public int changeContactName(String oldName, String newName){
        Log.d(TAG, "changing name of contact: " + oldName + " to: " + newName + ".....");
        return update(DBHelper.TABLE_CONTACT, DBHelper.CONTACT_NAME[0], oldName ,DBHelper.CONTACT_NAME[0], newName);
    }

    /////////////////////////
    //GROUP TABLE FUNCTIONS//
    /////////////////////////

    public int addGroup(Group newGroup){
        Log.d(TAG, getMethodName(0) + ": adding " + newGroup.toString() + "....");
        myDB.beginTransaction();
        try {

            //load columns in args
            ContentValues args = new ContentValues();
            args.put(DBHelper.GROUP_NAME[0], newGroup.getGroupName());
            args.put(DBHelper.GROUP_RESPONSE[0], newGroup.getResponse());
            args.put(DBHelper.GROUP_ACTIVITYPERM[0], convertBool(newGroup.isActivityPermission()));
            args.put(DBHelper.GROUP_LOCATIONPERM[0], convertBool(newGroup.isLocationPermission()));


            //add the row to the table and checks if insert was succesfull
            long insert = myDB.insertOrThrow(DBHelper.TABLE_GROUP, null, args);
            if (insert != -1) {
                myDB.setTransactionSuccessful();
                Log.d(TAG, getMethodName(0) + ": added"  + newGroup.toString());
            }
        }
        catch (Exception e){
            Log.e(TAG, "ERROR: " + getMethodName(0) + " failed");
            throw e;
        }
        finally {
            myDB.endTransaction();
        }

        return 0;
    }


    public int removeGroup(String groupName){
        Log.d(TAG, "removing " + groupName + " from groups....");
        return remove(DBHelper.TABLE_GROUP, DBHelper.GROUP_NAME[0] + "=\"" + groupName + "\"");
    }


    public int changeGroupName(String oldName, String newName){
        Log.d(TAG, "changing name of " + oldName + " to " + newName + ".....");
        ArrayList<Contact> contacts = getGroup(oldName);
        int returner = update(DBHelper.TABLE_GROUP, DBHelper.GROUP_NAME[0], oldName ,DBHelper.GROUP_NAME[0], newName);
        Log.d(TAG, "return result = " + Integer.toString(returner));
        if(returner == 1 && contacts != null) {
            Log.d(TAG, "returner was 1 and contacts list was not null");
            for (int i = 0; i < contacts.size(); i++) {
                Contact contact = contacts.get(i);
                Log.d(TAG, "Original Contact Info: " + contact.getName() + " " + contact.getPhoneNumber() + " " + contact.getGroupName() + " " + contact.getResponse());
                setContactGroup(contact.getPhoneNumber(), newName);
                contact = getContactInfo(contact.getPhoneNumber());
                Log.d(TAG, "Changed Contact Info: " + contact.getName() + " " + contact.getPhoneNumber() + " " + contact.getGroupName() + " " + contact.getResponse());
            }
        }else{
            Log.d(TAG, "returner either was not 1 or contacts list was null");
        }
        return returner;
    }


    public int setGroupLocationPermission(String groupName, boolean permission){
        Log.d(TAG, "changing activity of permission of " + groupName + " to " + permission + ".....");
        return update(DBHelper.TABLE_GROUP, DBHelper.GROUP_NAME[0], groupName ,DBHelper.GROUP_LOCATIONPERM[0], permission);
    }


    public int setGroupActivityPermission(String groupName, boolean permission){
        Log.d(TAG, "changing activity permission of " + groupName + " to " + permission + ".....");
        return update(DBHelper.TABLE_GROUP, DBHelper.GROUP_NAME[0], groupName ,DBHelper.GROUP_ACTIVITYPERM[0], permission);
    }


    public int setGroupResponse(String groupName, String response){
        Log.d(TAG, "setting group response of " + groupName + " to " + response + ".....");
        return update(DBHelper.TABLE_GROUP, DBHelper.GROUP_NAME[0], groupName ,DBHelper.GROUP_RESPONSE[0], response);
    }

    public Group getGroupInfo(String groupName){

        String table = DBHelper.TABLE_GROUP;
        String selection = DBHelper.GROUP_NAME[0] + "=?";
        String selectionArgs[] = {groupName};

        Cursor result = this.myDB.query(table, null, selection, selectionArgs, null, null, null);


        Group c;

        String name;
        String response;
        boolean locationPermission;
        boolean activityPermission;

        if ((result != null) && (result.moveToFirst())){

            name = result.getString(result.getColumnIndex(DBHelper.GROUP_NAME[0]));
            response = result.getString(result.getColumnIndex(DBHelper.GROUP_RESPONSE[0]));

            locationPermission = convertToBool(result.getString(result.getColumnIndex(DBHelper.GROUP_LOCATIONPERM[0])));
            activityPermission = convertToBool(result.getString(result.getColumnIndex(DBHelper.GROUP_ACTIVITYPERM[0])));

            c = new Group(name, response, locationPermission, activityPermission);

            result.close();

            Log.d(TAG, getMethodName(0) + ": retrieved " + c.toString());

            return c;
        }
        else {
            Log.e(TAG, "ERROR: " + getMethodName(0) + ": could not access cursor object ");
            return null;
        }
    }

    //returns sorted A-Z by group name
    public ArrayList<Group> getGroupList(){

        String table = DBHelper.TABLE_GROUP;
        String orderBy = "(" + DBHelper.GROUP_NAME[0] + ") ASC";

        Cursor result = this.myDB.query(table, null, null, null, null, null, orderBy);

        ArrayList<Group> range = new ArrayList<>();
        String name;
        String response;
        boolean locationPermission;
        boolean activityPermission;


        if ((result != null) && (result.moveToFirst())){

            for (int i = 0; i < result.getCount(); i++){
                name = result.getString(result.getColumnIndex(DBHelper.GROUP_NAME[0]));
                response = result.getString(result.getColumnIndex(DBHelper.GROUP_RESPONSE[0]));

                locationPermission = convertToBool(result.getString(result.getColumnIndex(DBHelper.GROUP_LOCATIONPERM[0])));
                activityPermission = convertToBool(result.getString(result.getColumnIndex(DBHelper.GROUP_ACTIVITYPERM[0])));

                Group g = new Group(name, response, locationPermission, activityPermission);
                Log.d(TAG, getMethodName(0) + ": " + g.toString());
                range.add(g);

                result.moveToNext();
            }

            result.close();

            return range;
        }
        else {
            Log.e(TAG, "ERROR: " + getMethodName(0) + ": could not access cursor object");
            return null;
        }
    }

    /////////////////////////////////
    //DEVELOPER LOG TABLE FUNCTIONS//
    /////////////////////////////////

   /*
    public void addDevLog(Date timeStamp, String entry){

    }

    public DeveloperLog getDevLog(int index){
        return null;
    }

    public ArrayList<DeveloperLog> getDevLogRange(int first, int last){
        return null;
    }

    public ArrayList<DeveloperLog> getDevLogRangeByDate(Date start, Date end){
        return null;
    }
    */
    /////////////////////////
    //convenience functions//
    /////////////////////////


    private static String getMethodName(int level) {
        return Thread.currentThread().getStackTrace()[3 + level].getMethodName();
    }

    private int update(String table, String matchColumn, String matchValue, String updateColumn, String updateValue){
        myDB.beginTransaction();
        try {
            //set criteria for selecting row
            String filter = matchColumn + "=" + "\"" + matchValue + "\"";

            //set new value for column to be updated
            ContentValues args = new ContentValues();
            args.put(updateColumn, updateValue);

            //update column and check to make sure only 1 row was updated
            myDB.setTransactionSuccessful();
            Log.d(TAG, getMethodName(1) + ": update succeeded");
            return  myDB.update(table, args, filter, null);
        }
        catch (Exception e){
            Log.e(TAG, "ERROR: " + getMethodName(1) + " failed");
            throw e;
        }
        finally {
            myDB.endTransaction();
        }
    }

    private int update(String table, String matchColumn, String matchValue, String updateColumn, boolean updateValue){
        myDB.beginTransaction();
        try {
            //set criteria for selecting row
            String filter = matchColumn + "=" + "\"" + matchValue + "\"";

            String value;

            if (updateValue) value = "true";
            else value = "false";

            //set new value for column to be updated
            ContentValues args = new ContentValues();
            args.put(updateColumn, value);

            //update column and check to make sure only 1 row was updated
            myDB.setTransactionSuccessful();
            Log.d(TAG, getMethodName(1) + ": update succeeded");
            return  myDB.update(table, args, filter, null);
        }
        catch (Exception e){
            Log.e(TAG, "ERROR: " + getMethodName(1) + " failed");
            throw e;
        }
        finally {
            myDB.endTransaction();
        }
    }

    private int update(String table, String matchColumn, String matchValue, String updateColumn, int updateValue){
        myDB.beginTransaction();

        try {
            //set criteria for selecting row
            String filter = matchColumn + "=" + "\"" + matchValue + "\"";

            //set new value for column to be updated
            ContentValues args = new ContentValues();
            args.put(updateColumn, Integer.toString(updateValue));

            //update column and check to make sure only 1 row was updated
            myDB.setTransactionSuccessful();
            Log.d(TAG, getMethodName(1) + ": update succeeded");
            return  myDB.update(table, args, filter, null);
        }
        catch (Exception e){
            Log.e(TAG, "ERROR: " + getMethodName(1) + " failed");
            throw e;
        }
        finally {
            myDB.endTransaction();
        }
    }

    private int update(String table, String matchColumn, String matchValue, String updateColumn, float updateValue){
        myDB.beginTransaction();

        try {
            //set criteria for selecting row
            String filter = matchColumn + "=" + "\"" + matchValue + "\"";

            //set new value for column to be updated
            ContentValues args = new ContentValues();
            args.put(updateColumn, Float.toString(updateValue));

            //update column and check to make sure only 1 row was updated
            myDB.setTransactionSuccessful();
            Log.d(TAG, getMethodName(1) + ": update succeeded");
            return  myDB.update(table, args, filter, null);
        }
        catch (Exception e){
            Log.e(TAG, "ERROR: " + getMethodName(1) + " failed");
            throw e;
        }
        finally {
            myDB.endTransaction();
        }
    }

    private int updateForLong(String table, String matchColumn, String matchValue, String updateColumn, long updateValue){
        myDB.beginTransaction();

        try {
            //set criteria for selecting row
            String filter = matchColumn + "=" + "\"" + matchValue + "\"";

            //set new value for column to be updated
            ContentValues args = new ContentValues();
            args.put(updateColumn, Long.toString(updateValue));

            //update column and check to make sure only 1 row was updated
            myDB.setTransactionSuccessful();
            Log.d(TAG, getMethodName(1) + ": update succeeded");
            return  myDB.update(table, args, filter, null);
        }
        catch (Exception e){
            Log.e(TAG, "ERROR: " + getMethodName(1) + " failed");
            throw e;
        }
        finally {
            myDB.endTransaction();
        }
    }

    private int remove(String table, String whereClause){
        myDB.beginTransaction();
        try {
            //update column and check to make sure only 1 row was updated
            int count =  myDB.delete(table, whereClause, null);
            myDB.setTransactionSuccessful();
            Log.d(TAG, getMethodName(1) + ": removal succeeded("+count+")");
            return count;
        }
        catch (Exception e){
            Log.e(TAG, "ERROR: " + getMethodName(1) + " failed");
            throw e;
        }
        finally {
            myDB.endTransaction();
        }
    }

    private String convertBool(boolean b){
        if (b) return "true";
        else return "false";
    }

    private boolean convertToBool(String b){
        return b.compareTo("true") == 0;
    }

    private String getSetting_str(String name){
        SQLiteStatement query;

        final String form =
                "SELECT " + DBHelper.SETTING_VALUE[0] +
                        " FROM " + DBHelper.TABLE_SETTINGS +
                        " WHERE " + DBHelper.SETTING_NAME[0] + " = ?";

        query = this.myDB.compileStatement(form);
        query.bindString(1, name);

        //query db and return the results
        String value =  query.simpleQueryForString();
        Log.d(TAG, getMethodName(1) + ": " + name + " is " + value);
        return value;
    }

    private boolean getSetting_bool(String name){
        SQLiteStatement query;

        final String form =
                "SELECT " + DBHelper.SETTING_VALUE[0] +
                        " FROM " + DBHelper.TABLE_SETTINGS +
                        " WHERE " + DBHelper.SETTING_NAME[0] + " = ?";

        query = this.myDB.compileStatement(form);
        query.bindString(1, name);

        //query db and return the results
        boolean value = convertToBool(query.simpleQueryForString());
        Log.d(TAG, getMethodName(1) + ": " + name + " is " + value);
        return value;
    }

    private int getSetting_int(String name){
        SQLiteStatement query;

        final String form =
                "SELECT " + DBHelper.SETTING_VALUE[0] +
                        " FROM " + DBHelper.TABLE_SETTINGS +
                        " WHERE " + DBHelper.SETTING_NAME[0] + " = ?";

        query = this.myDB.compileStatement(form);
        query.bindString(1, name);

        //query db and return the results
        try{
            int value = Integer.parseInt(query.simpleQueryForString());
            Log.d(TAG, getMethodName(1) + ": " + name + " is " + value);
            return value;
        }
        catch (NumberFormatException e){ //if there is an error report it and return -1
            Log.e(TAG, getMethodName(0) + ": setting value is not a number");
            return -1;
        }
    }

    private float getSetting_float(String name){
        SQLiteStatement query;

        final String form =
                "SELECT " + DBHelper.SETTING_VALUE[0] +
                        " FROM " + DBHelper.TABLE_SETTINGS +
                        " WHERE " + DBHelper.SETTING_NAME[0] + " = ?";

        query = this.myDB.compileStatement(form);
        query.bindString(1, name);

        //query db and return the results
        try{
            float value = Float.parseFloat(query.simpleQueryForString());
            Log.d(TAG, getMethodName(1) + ": " + name + " is " + value);
            return value;
        }
        catch (NumberFormatException e){ //if there is an error report it and return -1
            Log.e(TAG, getMethodName(0) + ": setting value is not a number");
            return -1;
        }
    }

    private long getSetting_long(String name){
        SQLiteStatement query;

        final String form =
                "SELECT " + DBHelper.SETTING_VALUE[0] +
                        " FROM " + DBHelper.TABLE_SETTINGS +
                        " WHERE " + DBHelper.SETTING_NAME[0] + " = ?";

        query = this.myDB.compileStatement(form);
        query.bindString(1, name);

        //query db and return the results
        try{
            long value = Long.parseLong(query.simpleQueryForString());
            Log.d(TAG, getMethodName(1) + ": " + name + " is " + value);
            return value;
        }
        catch (NumberFormatException e){ //if there is an error report it and return -1
            Log.e(TAG, getMethodName(0) + ": setting value is not a number");
            return -1;
        }
    }
}

