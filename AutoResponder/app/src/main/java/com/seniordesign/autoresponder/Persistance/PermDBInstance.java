package com.seniordesign.autoresponder.Persistance;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.util.Log;

import com.seniordesign.autoresponder.DataStructures.DeveloperLog;
import com.seniordesign.autoresponder.DataStructures.ResponseLog;
import com.seniordesign.autoresponder.DataStructures.Setting;

import java.sql.Date;
import java.sql.SQLException;
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
            String filter = DBHelper.COLUMN_NAME[0] + "=" + "\"" + Setting.REPLY_ALL + "\"";

            //set new value for column to be updated
            ContentValues args = new ContentValues();
            args.put(DBHelper.COLUMN_VALUE[0], reply);

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
                "SELECT " + DBHelper.COLUMN_VALUE[0] +
                 " FROM " + DBHelper.TABLE_SETTINGS +
                 " WHERE " + DBHelper.COLUMN_NAME[0] + "=" + "\"" + Setting.REPLY_ALL + "\"";

        //query db and ensure object was returned
        Cursor result = myDB.rawQuery(query, null);
        String response;
        if ((result != null) && (result.moveToFirst())){ // move pointer to first row
            //load setting value into string
            response = result.getString(result.getColumnIndex(DBHelper.COLUMN_VALUE[0]));

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
            String filter = DBHelper.COLUMN_NAME[0] + "=\"" + Setting.TIME_DELAY + "\"";

            //load values to be stored in respective columns
            ContentValues args = new ContentValues();
            args.put(DBHelper.COLUMN_VALUE[0], minutes);

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
                "SELECT " + DBHelper.COLUMN_VALUE[0] +
                 " FROM " + DBHelper.TABLE_SETTINGS +
                 " WHERE " + DBHelper.COLUMN_NAME[0] + " = " + "\"" + Setting.TIME_DELAY + "\"";

        //query db and ensure cursor object returned is valid
        Cursor result = myDB.rawQuery(query, null);
        int delay;
        String value;
        if ((result != null) && (result.moveToFirst())){

            //retrieve and return setting value
            value = result.getString(result.getColumnIndex(DBHelper.COLUMN_VALUE[0]));
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
            String filter = DBHelper.COLUMN_NAME[0] + "=\"" + Setting.RESPONSE_TOGGLE + "\"";
            ContentValues args = new ContentValues();
            args.put(DBHelper.COLUMN_VALUE[0], toggle);

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
                "SELECT " + DBHelper.COLUMN_VALUE[0] +
                 " FROM " + DBHelper.TABLE_SETTINGS +
                 " WHERE " + DBHelper.COLUMN_NAME[0] + " = " + "\"" + Setting.RESPONSE_TOGGLE + "\"";

        //query database and ensure cursor returned is valid
        Cursor result = myDB.rawQuery(query, null);
        boolean toggle;
        if ((result != null) && (result.moveToFirst())){

            //retrieve setting value
            String response = result.getString(result.getColumnIndex(DBHelper.COLUMN_VALUE[0]));

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
            args.put(DBHelper.COLUMN_TIMESTAMP[0], newLog.getTimeStamp().getTime());
            args.put(DBHelper.COLUMN_SENDERNUM[0], newLog.getSenderNumber());
            args.put(DBHelper.COLUMN_MESSAGERCV[0], newLog.getMessageReceived());
            args.put(DBHelper.COLUMN_MESSAGESNT[0], newLog.getMessageSent());

            //add the row to the table and checks if insert was succesfull
            long insert = myDB.insertOrThrow(DBHelper.TABLE_RESPONSELOG, null, args);
            if (insert != -1) {
                myDB.setTransactionSuccessful();
                Log.d(TAG, "entry added to ResponseLog");
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

    public ResponseLog getFirstResponse(){
        //TODO IMPLEMENT FOR 50%

        final String query =
                "SELECT * " +
                 " FROM " + DBHelper.TABLE_RESPONSELOG +
                 " WHERE " + DBHelper.COLUMN_TIMESTAMP[0] + "=MIN("+ DBHelper.COLUMN_TIMESTAMP[0] + ")";

        return null;
    }


    public ResponseLog getLastResponse(){
        //TODO IMPLEMENT FOR 50%
        final String query =
                "SELECT * " +
                 " FROM " + DBHelper.TABLE_RESPONSELOG +
                 " WHERE " + DBHelper.COLUMN_TIMESTAMP[0] + "=MAX("+ DBHelper.COLUMN_TIMESTAMP[0] + ")";

        return null;
    }

    public ResponseLog getResponse(int index){
        //TODO IMPLEMENT FOR 50%
        final String query =
                "SELECT * " +
                " FROM " + DBHelper.TABLE_RESPONSELOG +
                " WHERE ROWID=\"" + index + "\"";
        return null;
    }

    //TODO MAKE MORE EFFICIENT
    public ResponseLog getLastResponseByNum(String phoneNum){
        final String query_p2 =
                "SELECT * " +
                        " FROM " + DBHelper.TABLE_RESPONSELOG +
                        " WHERE "+ DBHelper.COLUMN_TIMESTAMP[0] + " =";

        final String query = "SELECT *" +
            " FROM " + DBHelper.TABLE_RESPONSELOG +
            " WHERE " + DBHelper.COLUMN_SENDERNUM[0] + "=\"" + phoneNum + "\" ORDER BY " + DBHelper.COLUMN_TIMESTAMP[0];

        String senderNum = phoneNum;
        String msgRcv = "";
        String msgSnt = "";
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
                //else if (numRows > 1)
                //    Log.d(TAG, getMethodName() + ": found more than " + numRows + " rows");

                //load query results
                date = new Date(result.getLong(result.getColumnIndex(DBHelper.COLUMN_TIMESTAMP[0])));
                senderNum = result.getString(result.getColumnIndex(DBHelper.COLUMN_SENDERNUM[0]));
                msgRcv = result.getString(result.getColumnIndex(DBHelper.COLUMN_MESSAGERCV[0]));
                msgSnt = result.getString(result.getColumnIndex(DBHelper.COLUMN_MESSAGESNT[0]));

                Date checkDate;

                for (int i = 1; i < numRows; i++){
                    result.moveToNext();
                    checkDate = new Date(result.getLong(result.getColumnIndex(DBHelper.COLUMN_TIMESTAMP[0])));
                    if (checkDate.after(date)){
                        date = checkDate;
                        senderNum = result.getString(result.getColumnIndex(DBHelper.COLUMN_SENDERNUM[0]));
                        msgRcv = result.getString(result.getColumnIndex(DBHelper.COLUMN_MESSAGERCV[0]));
                        msgSnt = result.getString(result.getColumnIndex(DBHelper.COLUMN_MESSAGESNT[0]));
                    }
                }


                Log.d(TAG, "ResponseLogRetrieved: "+ date + ", " + senderNum + ", " + msgSnt + "," + msgRcv);

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

    public ArrayList<ResponseLog> getResponseByDateRange(Date start, Date end){
        //TODO IMPLEMENT FOR 50%
        final String query =
                "SELECT * " +
                " FROM " + DBHelper.TABLE_RESPONSELOG +
                " WHERE " + DBHelper.COLUMN_TIMESTAMP[0] + " BETWEEN \"" + start + "\" AND \"" + end + "\"";
        return null;
    }

    public ArrayList<ResponseLog> getResponseRange(int start, int end){
        //TODO IMPLEMENT FOR 50%
        final String query =
                "SELECT * " +
                " FROM " + DBHelper.TABLE_RESPONSELOG +
                " WHERE ROWID BETWEEN \"" + start + "\" AND \"" + end + "\"";
        return null;
    }

    private static String getMethodName() {
        return Thread.currentThread().getStackTrace()[3].getMethodName();
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
