package com.seniordesign.autoresponder.Persistance;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.util.Log;

import com.seniordesign.autoresponder.DataStructures.Contact;
import com.seniordesign.autoresponder.DataStructures.DeveloperLog;
import com.seniordesign.autoresponder.DataStructures.Group;
import com.seniordesign.autoresponder.DataStructures.ResponseLog;
import com.seniordesign.autoresponder.DataStructures.Setting;

import java.sql.Date;
import java.util.ArrayList;
import java.util.InputMismatchException;

/**
 * Created by Garlan on 9/28/2015.
 */

//note for setting up transactions: goto https://docs.oracle.com/javase/tutorial/jdbc/basics/transactions.html
public class PermDBInstance implements DBInstance {
    private static final String TAG = "PermDBInstance";
    private SQLiteDatabase myDB;


    public PermDBInstance(Context context) {
        //this.mDB = SQLiteDatabase.openOrCreateDatabase(DATABASE_NAME, null);
        Log.d(TAG, "initializing database instance");

        //access DB via helper class
        DBHelper myLittleHelper = new DBHelper(context);
        this.myDB = myLittleHelper.getWritableDatabase();
    }


    ///////////////////////////
    //SETTING TABLE FUNCTIONS//
    ///////////////////////////

    public void setReplyAll(String reply){
        Log.d(TAG, "setting replyAll....");
        myDB.beginTransaction();
        try {
            //set criteria for selecting row
            String filter = DBHelper.SETTING_NAME[0] + "=" + "\"" + Setting.REPLY_ALL + "\"";

            //set new value for column to be updated
            ContentValues args = new ContentValues();
            args.put(DBHelper.SETTING_VALUE[0], reply);

            //update column and check to make sure only 1 row was updated
            int updateNum =  myDB.update(DBHelper.TABLE_SETTINGS, args, filter, null);
            if (updateNum == 1) {
                myDB.setTransactionSuccessful();
                Log.d(TAG, "replyAll set successfully");
            }
            else Log.d(TAG, updateNum + " rows were found to update");
        }
        catch (Exception e){
            Log.e(TAG, "ERROR: " + getMethodName() + " failed");
            throw e;
        }
        finally {
            myDB.endTransaction();
        }
    }

    public String getReplyAll(){
        final String query =
                "SELECT " + DBHelper.SETTING_VALUE[0] +
                 " FROM " + DBHelper.TABLE_SETTINGS +
                 " WHERE " + DBHelper.SETTING_NAME[0] + "=" + "\"" + Setting.REPLY_ALL + "\"";

        //query db and ensure object was returned
        Cursor result = myDB.rawQuery(query, null);
        String response;
        if ((result != null) && (result.moveToFirst())){ // move pointer to first row
            //load setting value into string
            response = result.getString(result.getColumnIndex(DBHelper.SETTING_VALUE[0]));

            //close the cursor
            result.close();

            //return the query result
            return response;
        }
        else {
            Log.e(TAG, "ERROR: " + getMethodName() + ": could not access cursor object from: " + query);
            return null;
        }
    }

    public void setDelay(int minutes){

        Log.d(TAG, "setting delay....");
        myDB.beginTransaction();
        try {
            //set filter to determine row to select
            String filter = DBHelper.SETTING_NAME[0] + "=\"" + Setting.TIME_DELAY + "\"";

            //load values to be stored in respective columns
            ContentValues args = new ContentValues();
            args.put(DBHelper.SETTING_VALUE[0], minutes);

            //update row and ensure only 1 row was updated
            int updateNum = myDB.update(DBHelper.TABLE_SETTINGS, args, filter, null);
            if (updateNum == 1) {
                myDB.setTransactionSuccessful();
                Log.d(TAG, "delay set successfully");
            }
            else Log.d(TAG, updateNum + " rows were found to update");
        }
        catch (Exception e){
            Log.e(TAG, "ERROR: " + getMethodName() + " failed");
            throw e;
        }
        finally {
            myDB.endTransaction();
        }
    }

    //will return -1 if no result returned
    public int getDelay(){
        final String query =
                "SELECT " + DBHelper.SETTING_VALUE[0] +
                 " FROM " + DBHelper.TABLE_SETTINGS +
                 " WHERE " + DBHelper.SETTING_NAME[0] + " = " + "\"" + Setting.TIME_DELAY + "\"";

        //query db and ensure cursor object returned is valid
        Cursor result = myDB.rawQuery(query, null);
        int delay;
        String value;
        if ((result != null) && (result.moveToFirst())){

            //retrieve and return setting value
            value = result.getString(result.getColumnIndex(DBHelper.SETTING_VALUE[0]));
            delay = Integer.parseInt(value);
            result.close();
            return delay;
        }
        else {
            Log.e(TAG, "ERROR: " + getMethodName() + ": could not access cursor object from: " + query);
            return -1;
        }
    }

    public void setResponseToggle(boolean responseToggle){
        String toggle;

        //convert boolean to string
        if(responseToggle){
            toggle = "true";
        }
        else{
            toggle = "false";
        }

        Log.d(TAG, "setting  responseToggle....");
        myDB.beginTransaction();
        try {
            String filter = DBHelper.SETTING_NAME[0] + "=\"" + Setting.RESPONSE_TOGGLE + "\"";
            ContentValues args = new ContentValues();
            args.put(DBHelper.SETTING_VALUE[0], toggle);

            int updateNum = myDB.update(DBHelper.TABLE_SETTINGS, args, filter, null);
            if (updateNum == 1) {
                myDB.setTransactionSuccessful();
                Log.d(TAG, "responseToggle set successfully");
            }
            else{
                Log.d(TAG, updateNum + " rows were found to update");
            }
        }
        catch (Exception e){
            Log.e(TAG, "ERROR: " + getMethodName() + " failed");
            throw e;
        }
        finally {
            myDB.endTransaction();
        }
    }

    public boolean getResponseToggle(){
        final String query =
                "SELECT " + DBHelper.SETTING_VALUE[0] +
                 " FROM " + DBHelper.TABLE_SETTINGS +
                 " WHERE " + DBHelper.SETTING_NAME[0] + " = " + "\"" + Setting.RESPONSE_TOGGLE + "\"";

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
                Log.e(TAG, "ERROR: " + getMethodName() + ": found " + response + " when a value of true or false was expected from: " + query);
                throw new InputMismatchException();
            }
            result.close();
        } else {
            Log.e(TAG, "ERROR: " + getMethodName() + ": could not get/access cursor object from: " + query);
            throw new NullPointerException();
        }
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
            args.put(DBHelper.RESPONSELOG_TIMESTAMP[0], newLog.getTimeStamp().getTime());
            args.put(DBHelper.RESPONSELOG_SENDERNUM[0], newLog.getSenderNumber());
            args.put(DBHelper.RESPONSELOG_MESSAGERCV[0], newLog.getMessageReceived());
            args.put(DBHelper.RESPONSELOG_MESSAGESNT[0], newLog.getMessageSent());

            //add the row to the table and checks if insert was succesfull
            long insert = myDB.insertOrThrow(DBHelper.TABLE_RESPONSELOG, null, args);
            if (insert != -1) {
                myDB.setTransactionSuccessful();
                Log.d(TAG, "entry added to ResponseLog " + newLog.toString());
            }
        }
        catch (Exception e){
            Log.e(TAG, "ERROR: " + getMethodName() + " failed");
            throw e;
        }
        finally {
            myDB.endTransaction();
        }
    }

    //TODO TEST THIS FUNCTION
    public ResponseLog getFirstResponse(){
        final String query =
                "SELECT MIN(" + DBHelper.RESPONSELOG_TIMESTAMP[0] + "), * " +
                 " FROM " + DBHelper.TABLE_RESPONSELOG;

        Cursor result = myDB.rawQuery(query, null);

        //fields to be returned
        Date timeStamp;
        String senderNumber;
        String messageRecv;
        String messageSent;

        if ((result != null) && (result.moveToFirst())){

            //retrieve and return setting value
            timeStamp = new Date(result.getLong(result.getColumnIndex(DBHelper.RESPONSELOG_TIMESTAMP[0])));
            senderNumber = result.getString(result.getColumnIndex(DBHelper.RESPONSELOG_SENDERNUM[0]));
            messageRecv = result.getString(result.getColumnIndex(DBHelper.RESPONSELOG_MESSAGERCV[0]));
            messageSent = result.getString(result.getColumnIndex(DBHelper.RESPONSELOG_MESSAGESNT[0]));
            result.close();

            return new ResponseLog(messageSent, messageRecv, senderNumber, timeStamp);
        }
        else {
            Log.e(TAG, "ERROR: " + getMethodName() + ": could not access cursor object from: " + query);
            return null;
        }
    }


    //TODO TEST THIS FUNCTION
    public ResponseLog getLastResponse(){
        final String query =
                "SELECT MAX(" + DBHelper.RESPONSELOG_TIMESTAMP[0] + "), * " +
                        " FROM " + DBHelper.TABLE_RESPONSELOG;

        Cursor result = myDB.rawQuery(query, null);

        //fields to be returned
        Date timeStamp;
        String senderNumber;
        String messageRecv;
        String messageSent;

        if ((result != null) && (result.moveToFirst())){

            //retrieve and return setting value
            timeStamp = new Date(result.getLong(result.getColumnIndex(DBHelper.RESPONSELOG_TIMESTAMP[0])));
            senderNumber = result.getString(result.getColumnIndex(DBHelper.RESPONSELOG_SENDERNUM[0]));
            messageRecv = result.getString(result.getColumnIndex(DBHelper.RESPONSELOG_MESSAGERCV[0]));
            messageSent = result.getString(result.getColumnIndex(DBHelper.RESPONSELOG_MESSAGESNT[0]));
            result.close();

            return new ResponseLog(messageSent, messageRecv, senderNumber, timeStamp);
        }
        else {
            Log.e(TAG, "ERROR: " + getMethodName() + ": could not access cursor object from: " + query);
            return null;
        }
    }

    //TODO TEST THIS FUNCTION
    public ResponseLog getResponse(int index){
        final String query =
                "SELECT * " +
                " FROM " + DBHelper.TABLE_RESPONSELOG +
                " WHERE ROWID = \"" + index + "\"";

        Cursor result = myDB.rawQuery(query, null);

        //fields to be returned
        Date timeStamp;
        String senderNumber;
        String messageRecv;
        String messageSent;

        if ((result != null) && (result.moveToFirst())){

            //retrieve and return setting value
            timeStamp = new Date(result.getLong(result.getColumnIndex(DBHelper.RESPONSELOG_TIMESTAMP[0])));
            senderNumber = result.getString(result.getColumnIndex(DBHelper.RESPONSELOG_SENDERNUM[0]));
            messageRecv = result.getString(result.getColumnIndex(DBHelper.RESPONSELOG_MESSAGERCV[0]));
            messageSent = result.getString(result.getColumnIndex(DBHelper.RESPONSELOG_MESSAGESNT[0]));
            result.close();

            return new ResponseLog(messageSent, messageRecv, senderNumber, timeStamp);
        }
        else {
            Log.e(TAG, "ERROR: " + getMethodName() + ": could not access cursor object from: " + query);
            return null;
        }
    }

    public ResponseLog getLastResponseByNum(String phoneNum){
        final String query =
                "SELECT MAX(" + DBHelper.RESPONSELOG_TIMESTAMP[0] + "), " + DBHelper.RESPONSELOG_SENDERNUM[0] + ", " + DBHelper.RESPONSELOG_MESSAGERCV[0] + ", " + DBHelper.RESPONSELOG_MESSAGESNT[0] +
                        " FROM " + DBHelper.TABLE_RESPONSELOG +
                        " WHERE "+ DBHelper.RESPONSELOG_SENDERNUM[0] + " = \"" + phoneNum + "\"";

        final String query_2 = "SELECT *" +
            " FROM " + DBHelper.TABLE_RESPONSELOG +
            " WHERE " + DBHelper.RESPONSELOG_SENDERNUM[0] + "=\"" + phoneNum + "\" ORDER BY " + DBHelper.RESPONSELOG_TIMESTAMP[0];

        String senderNum = phoneNum;
        String msgRcv = "hi";
        String msgSnt = "hi";
        Date date = new Date(0);

        try {
            Cursor result = myDB.rawQuery(query, null);
            if (result != null) {
                //check and see how many rows were returned
                int numRows = result.getCount();
                if (numRows == 0) {
                    Log.d(TAG, "ResponseLogRetrieved: "+ date + ", " + senderNum + ", " + msgSnt + ", " + msgRcv);
                    return new ResponseLog(msgSnt, msgRcv, senderNum, date); //returns a false record from the beginning of time if no record found
                }
                else{
                    result.moveToFirst();
                }

                //load query results
                date = new Date(result.getLong(0));
                senderNum = result.getString(1);
                msgRcv = result.getString(2);
                msgSnt = result.getString(3);

                //Date checkDate;

                /*for (int i = 1; i < numRows; i++){
                    result.moveToNext();
                    checkDate = new Date(result.getLong(result.getColumnIndex(DBHelper.RESPONSELOG_TIMESTAMP[0])));
                    if (checkDate.after(date)){
                        date = checkDate;
                        senderNum = result.getString(result.getColumnIndex(DBHelper.RESPONSELOG_SENDERNUM[0]));
                        msgRcv = result.getString(result.getColumnIndex(DBHelper.RESPONSELOG_MESSAGERCV[0]));
                        msgSnt = result.getString(result.getColumnIndex(DBHelper.RESPONSELOG_MESSAGESNT[0]));
                    }
                }*/


                Log.d(TAG, "ResponseLogRetrieved:("+ numRows +") "+ date + ", " + senderNum + ", " + msgSnt + "," + msgRcv);

                result.close();

                return new ResponseLog(msgSnt, msgRcv, senderNum, date);
            } else {
                Log.e(TAG, "ERROR: " + getMethodName() + ": could not access cursor object from: " + query);
                return null;
            }
        }
        catch (Exception e){
            Log.e(TAG, "ERROR: " + getMethodName() + " could not retrieve data");
            throw e;
        }
    }

    //TODO TEST THIS FUNCTION
    public ArrayList<ResponseLog> getResponseByDateRange(Date start, Date end){
        final String query =
                "SELECT * " +
                " FROM " + DBHelper.TABLE_RESPONSELOG +
                " WHERE " + DBHelper.RESPONSELOG_TIMESTAMP[0] + " BETWEEN \"" + start.getTime() + "\" AND \"" + end.getTime() + "\"";

        Cursor result = myDB.rawQuery(query, null);

        ArrayList<ResponseLog> range = new ArrayList<>();
        Date timeStamp;
        String senderNumber;
        String messageRecv;
        String messageSent;

        if ((result != null) && (result.moveToFirst())){

            for (int i = 1; i < result.getCount(); i++){
                timeStamp = new Date(result.getLong(result.getColumnIndex(DBHelper.RESPONSELOG_TIMESTAMP[0])));
                senderNumber = result.getString(result.getColumnIndex(DBHelper.RESPONSELOG_SENDERNUM[0]));
                messageRecv = result.getString(result.getColumnIndex(DBHelper.RESPONSELOG_MESSAGERCV[0]));
                messageSent = result.getString(result.getColumnIndex(DBHelper.RESPONSELOG_MESSAGESNT[0]));

                range.add(new ResponseLog(messageSent, messageRecv, senderNumber, timeStamp));

                result.moveToNext();
            }
            //retrieve and return setting value

            result.close();

            return range;
        }
        else {
            Log.e(TAG, "ERROR: " + getMethodName() + ": could not access cursor object from: " + query);
            return null;
        }
    }

    //TODO TEST THIS FUNCTION
    public ArrayList<ResponseLog> getResponseRange(int start, int end){
        final String query =
                "SELECT * " +
                " FROM " + DBHelper.TABLE_RESPONSELOG +
                " WHERE ROWID BETWEEN \"" + start + "\" AND \"" + end + "\" ORDER BY("+ DBHelper.RESPONSELOG_TIMESTAMP[0] +") ASC";

        Cursor result = myDB.rawQuery(query, null);

        ArrayList<ResponseLog> range = new ArrayList<>();
        Date timeStamp;
        String senderNumber;
        String messageRecv;
        String messageSent;

        if ((result != null) && (result.moveToFirst())){

            for (int i = 1; i < result.getCount(); i++){
                timeStamp = new Date(result.getLong(result.getColumnIndex(DBHelper.RESPONSELOG_TIMESTAMP[0])));
                senderNumber = result.getString(result.getColumnIndex(DBHelper.RESPONSELOG_SENDERNUM[0]));
                messageRecv = result.getString(result.getColumnIndex(DBHelper.RESPONSELOG_MESSAGERCV[0]));
                messageSent = result.getString(result.getColumnIndex(DBHelper.RESPONSELOG_MESSAGESNT[0]));

                range.add(new ResponseLog(messageSent, messageRecv, senderNumber, timeStamp));

                result.moveToNext();
            }
            //retrieve and return setting value

            result.close();

            return range;
        }
        else {
            Log.e(TAG, "ERROR: " + getMethodName() + ": could not access cursor object from: " + query);
            return null;
        }
    }

    ///////////////////////////
    //CONTACT TABLE FUNCTIONS//
    ///////////////////////////

    //TODO TEST THIS FUNCTION
    public int addContact(Contact newContact){
        Log.d(TAG, "adding contact....");
        myDB.beginTransaction();
        try {

            //load columns in args
            ContentValues args = new ContentValues();
            args.put(DBHelper.CONTACT_NAME[0], newContact.getName());
            args.put(DBHelper.CONTACT_PHONENUM[0], newContact.getPhoneNumber());
            args.put(DBHelper.CONTACT_GROUP[0], newContact.getGroupName());
            args.put(DBHelper.CONTACT_RESPONSE[0], newContact.getResponse());
            args.put(DBHelper.CONTACT_ACTIVITYPERM[0], newContact.isActivityPermission());
            args.put(DBHelper.CONTACT_LOCATIONPERM[0], newContact.isLocationPermission());

            //add the row to the table and checks if insert was succesfull
            long insert = myDB.insertOrThrow(DBHelper.TABLE_RESPONSELOG, null, args);
            if (insert != -1) {
                myDB.setTransactionSuccessful();
                Log.d(TAG, getMethodName() + ":"  + newContact.toString());
            }
        }
        catch (Exception e){
            Log.e(TAG, "ERROR: " + getMethodName() + " failed");
            throw e;
        }
        finally {
            myDB.endTransaction();
        }

        return 0;
    }

    //TODO IMPLEMENT
    public int removeContact(String phoneNum){
        return  -1;
    }

    //TODO IMPLEMENT
    public int setContactName(String phoneNum, String newName){
        return  -1;
    }

    //TODO IMPLEMENT
    public int setContactNumber(String oldPhoneNum, String newPhoneNum){
        return  -1;
    }

    //TODO IMPLEMENT
    public int setContactLocationPermission(String phoneNum, boolean permission){
        return  -1;
    }

    //TODO IMPLEMENT
    public int setContactActivityPermission(String phoneNum, boolean permission){
        return  -1;
    }

    //TODO IMPLEMENT
    public int setContactGroup(String phoneNum, String groupName){
        return  -1;
    }

    //TODO IMPLEMENT
    public Contact getContactInfo(String phoneNum){
        return  null;
    }

    //TODO IMPLEMENT
    //returns sorted A - Z by name
    public ArrayList<Contact> getContactList(){
        return  null;
    }

    //TODO IMPLEMENT
    public ArrayList<Contact> getGroup(String groupName){
        return  null;
    }

    /////////////////////////
    //GROUP TABLE FUNCTIONS//
    /////////////////////////

    //TODO IMPLEMENT
    public int addGroup(Group newGroup){
        return  -1;
    }

    //TODO IMPLEMENT
    public int removeGroup(String groupName){
        return  -1;
    }

    public int changeGroupName(String oldName, String newName){
        return  -1;
    }

    //TODO IMPLEMENT
    public int setGroupLocationPermission(String groupName, boolean permission){
        return  -1;
    }

    //TODO IMPLEMENT
    public int setGroupActivityPermission(String groupName, boolean permission){
        return  -1;
    }

    //TODO IMPLEMENT
    public Group getGroupInfo(String groupName){
        return  null;
    }

    //TODO IMPLEMENT
    //returns sorted A-Z by group name
    public ArrayList<Group> getGroupList(){
        return  null;
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

    private static String getMethodName() {
        return Thread.currentThread().getStackTrace()[3].getMethodName();
    }
}
