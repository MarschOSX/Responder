package com.seniordesign.autoresponder.Persistance;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.util.Log;

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
    private DBHelper myLittleHelper;
    private SQLiteDatabase myDB;


    public PermDBInstance(Context context) {
        //this.mDB = SQLiteDatabase.openOrCreateDatabase(DATABASE_NAME, null);
        Log.d(TAG, "initializing database instance");
        this.myLittleHelper = new DBHelper(context);
        this.myDB = myLittleHelper.getWritableDatabase();
    }


    ///////////////////////////
    //SETTING TABLE FUNCTIONS//
    ///////////////////////////
    public void setReplyAll(String reply){
        /*final String query =
                "UPDATE " + DBHelper.TABLE_SETTINGS +
                " SET " + DBHelper.COLUMN_VALUE[0] +" = " + reply +
                " WHERE " + DBHelper.COLUMN_NAME[0] + " = " + Setting.REPLY_ALL;*/
        Log.d(TAG, "setting replyAll....");
        myDB.beginTransaction();
        try {
            String filter = DBHelper.COLUMN_NAME[0] + "=" + "\"" + Setting.REPLY_ALL + "\"";
            ContentValues args = new ContentValues();
            args.put(DBHelper.COLUMN_VALUE[0], reply);

            int updateNum =  myDB.update(DBHelper.TABLE_SETTINGS, args, filter, null);
            if (updateNum == 1) {
                myDB.setTransactionSuccessful();
            }
            else Log.d(TAG, updateNum + " rows were found to update");
        }
        catch (Exception e){
            myDB.endTransaction();
            Log.e(TAG, "ERROR: " + getMethodName() + " failed");
            throw e;
        }
        finally {
            Log.d(TAG, "replyAll set successfully");
            myDB.endTransaction();
        }
    }

    public String getReplyAll(){
        final String query =
                "SELECT " + DBHelper.COLUMN_VALUE[0] +
                 " FROM " + DBHelper.TABLE_SETTINGS +
                 " WHERE " + DBHelper.COLUMN_NAME[0] + "=" + "\"" + Setting.REPLY_ALL + "\"";
        //SELECT value FROM  settings WHERE setting_name = "reply_all"

        try {
            Cursor result = myDB.rawQuery(query, null);
            String response;
            if ((result != null) && (result.moveToFirst())){
                response = result.getString(result.getColumnIndex(DBHelper.COLUMN_VALUE[0]));
                result.close();
            }
            else {
                Log.e(TAG, "ERROR: " + getMethodName() + ": could not access cursor object from: " + query);
                throw new NullPointerException();
            }
            return response;
        }
        catch (Exception e){
            Log.e(TAG, "ERROR: " + getMethodName() + ": getReplyAll() failed");
            throw e;
        }
    }

    public void setDelay(int minutes){
        /*final String query =
                "UPDATE " + DBHelper.TABLE_SETTINGS +
                " SET " + DBHelper.COLUMN_VALUE[0] +" = " + minutes +
                " WHERE " + DBHelper.COLUMN_NAME[0] + " = " + Setting.TIME_DELAY;*/

        Log.d(TAG, "setting delay....");
        myDB.beginTransaction();
        try {
            String filter = DBHelper.COLUMN_NAME[0] + "=" + Setting.TIME_DELAY;
            ContentValues args = new ContentValues();
            args.put(DBHelper.COLUMN_VALUE[0], minutes);
            int updateNum = myDB.update(DBHelper.TABLE_SETTINGS, args, filter, null);
            if (updateNum == 1) {
                myDB.setTransactionSuccessful();
            }
            else Log.d(TAG, updateNum + " rows were found to update");
        }
        catch (Exception e){
            Log.e(TAG, "ERROR: " + getMethodName() + " failed");
            myDB.endTransaction();
            throw e;
        }
        finally {
            Log.d(TAG, "delay set successfully");
            myDB.endTransaction();
        }
    }

    //will return -1 if no result returned
    public int getDelay(){
        final String query =
                "SELECT " + DBHelper.COLUMN_VALUE[0] +
                 " FROM " + DBHelper.TABLE_SETTINGS +
                 " WHERE " + DBHelper.COLUMN_NAME[0] + " = " + "\"" + Setting.TIME_DELAY + "\"";

        try {
            Cursor result = myDB.rawQuery(query, null);
            int delay;
            String value;
            if ((result != null) && (result.moveToFirst())){
                value = result.getString(result.getColumnIndex(DBHelper.COLUMN_VALUE[0]));
                delay = Integer.parseInt(value);
                result.close();
            }
            else {
                Log.e(TAG, "ERROR: " + getMethodName() + ": could not access cursor object from: " + query);
                throw new NullPointerException();
            }
            return delay;
        }
        catch (Exception e){
            Log.e(TAG, "ERROR: " + getMethodName() + " failed");
            myDB.endTransaction();
            throw e;
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
            String filter = DBHelper.COLUMN_NAME[0] + "=" + Setting.RESPONSE_TOGGLE;
            ContentValues args = new ContentValues();
            args.put(DBHelper.COLUMN_VALUE[0], toggle);

            int updateNum = myDB.update(DBHelper.TABLE_SETTINGS, args, filter, null);
            if (updateNum == 1) {
                myDB.setTransactionSuccessful();
            }
            else{
                Log.d(TAG, updateNum + " rows were found to update");
            }
        }
        catch (Exception e){
            Log.e(TAG, "ERROR: " + getMethodName() + " failed");
            myDB.endTransaction();
            throw e;
        }
        finally {
            Log.d(TAG, "responseToggle set successfully");
            myDB.endTransaction();
        }
    }

    public boolean getResponseToggle(){
        final String query =
                "SELECT " + DBHelper.COLUMN_VALUE[0] +
                 " FROM " + DBHelper.TABLE_SETTINGS +
                 " WHERE " + DBHelper.COLUMN_NAME[0] + " = " + "\"" + Setting.RESPONSE_TOGGLE + "\"";

        try {
            Cursor result = myDB.rawQuery(query, null);
            boolean toggle;
            if ((result != null) && (result.moveToFirst())){
                String response = result.getString(result.getColumnIndex(DBHelper.COLUMN_VALUE[0]));
                //determine if value is true or false
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
            }
            else {
                Log.e(TAG, "ERROR: " + getMethodName() + ": could not get/access cursor object from: " + query);
                throw new NullPointerException();
            }
            return toggle;
        }
        catch (Exception e){
            Log.e(TAG, "ERROR: " + getMethodName() + " failed");
            throw e;
        }
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
            args.put(DBHelper.COLUMN_TIMESTAMP[0], newLog.getTimeStamp().toString());
            args.put(DBHelper.COLUMN_SENDERNUM[0], newLog.getSenderNumber());
            args.put(DBHelper.COLUMN_MESSAGERCV[0], newLog.getMessageReceived());
            args.put(DBHelper.COLUMN_MESSAGESNT[0], newLog.getMessageSent());

            long insert = myDB.insertOrThrow(DBHelper.TABLE_RESPONSELOG, null, args);
            if (insert != -1) {
                myDB.setTransactionSuccessful();
            }
        }
        catch (Exception e){
            Log.e(TAG, "ERROR: " + getMethodName() + " failed");
            myDB.endTransaction();
            throw e;
        }
        finally {
            Log.d(TAG, "entry added to ResponseLog");
            myDB.endTransaction();
        }
    }

    public ResponseLog getFirstEntry(){
        //TODO IMPLEMENT FOR 50%

        final String query =
                "SELECT * " +
                 " FROM " + DBHelper.TABLE_RESPONSELOG +
                 " WHERE " + DBHelper.COLUMN_TIMESTAMP[0] + "=MIN("+ DBHelper.COLUMN_TIMESTAMP[0] + ")";

        return null;
    }


    public ResponseLog getLastEntry(){
        //TODO IMPLEMENT FOR 50%
        final String query =
                "SELECT * " +
                 " FROM " + DBHelper.TABLE_RESPONSELOG +
                 " WHERE " + DBHelper.COLUMN_TIMESTAMP[0] + "=MAX("+ DBHelper.COLUMN_TIMESTAMP[0] + ")";

        return null;
    }

    public ResponseLog getEntry(int index){
        //TODO IMPLEMENT FOR 50%
        final String query =
                "SELECT * " +
                " FROM " + DBHelper.TABLE_RESPONSELOG +
                " WHERE ROWID=\"" + index + "\"";
        return null;
    }

    public ResponseLog getLastEntryByNum(String phoneNum){
        final String query =
                "SELECT * " +
                        " FROM " + DBHelper.TABLE_RESPONSELOG +
                        " WHERE "+ DBHelper.COLUMN_TIMESTAMP[0] + " = MAX(SELECT " + DBHelper.COLUMN_TIMESTAMP[0] +
                        " FROM " + DBHelper.TABLE_RESPONSELOG +
                        " WHERE " + DBHelper.COLUMN_SENDERNUM[0] + "=\"" + phoneNum + "\")";
        Date date;
        String senderNum;
        String msgRcv;
        String msgSnt;

        Cursor result = myDB.rawQuery(query, null);
        if ((result != null) && (result.moveToFirst())){
            //check and see how many rows were returned
            int numRows = result.getCount();
            if (numRows > 1) Log.d(TAG, getMethodName() + ": found more than " + numRows + " rows");

            //load query results
            date = new Date(result.getLong(result.getColumnIndex(DBHelper.COLUMN_TIMESTAMP[0])));
            senderNum = result.getString(result.getColumnIndex(DBHelper.COLUMN_SENDERNUM[0]));
            msgRcv = result.getString(result.getColumnIndex(DBHelper.COLUMN_MESSAGERCV[0]));
            msgSnt = result.getString(result.getColumnIndex(DBHelper.COLUMN_MESSAGESNT[0]));

            result.close();

            return new ResponseLog(msgSnt, msgRcv, senderNum, date);
        }
        else {
            Log.e(TAG, "ERROR: " + getMethodName() + ": could not access cursor object from: " + query);
            return null;
        }
    }

    public ArrayList<ResponseLog> getEntryByDateRange(Date start, Date end){
        //TODO IMPLEMENT FOR 50%
        final String query =
                "SELECT * " +
                " FROM " + DBHelper.TABLE_RESPONSELOG +
                " WHERE " + DBHelper.COLUMN_TIMESTAMP[0] + " BETWEEN \"" + start + "\" AND \"" + end + "\"";
        return null;
    }

    public ArrayList<ResponseLog> getEntryRange(int start, int end){
        //TODO IMPLEMENT FOR 50%
        final String query =
                "SELECT * " +
                " FROM " + DBHelper.TABLE_RESPONSELOG +
                " WHERE ROWID BETWEEN \"" + start + "\" AND \"" + end + "\"";
        return null;
    }

    private static String getMethodName() {
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }
}
