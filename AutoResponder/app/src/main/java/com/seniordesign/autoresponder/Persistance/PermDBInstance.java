package com.seniordesign.autoresponder.Persistance;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.util.Log;

import com.seniordesign.autoresponder.DataStructures.Setting;

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
        this.myLittleHelper = new DBHelper(context);
        this.myDB = myLittleHelper.getWritableDatabase();
    }

    public void setReplyAll(String reply){
        /*final String query =
                "UPDATE " + DBHelper.TABLE_SETTINGS +
                " SET " + DBHelper.COLUMN_VALUE[0] +" = " + reply +
                " WHERE " + DBHelper.COLUMN_NAME[0] + " = " + Setting.REPLY_ALL;*/

        myDB.beginTransaction();
        try {
            String filter = DBHelper.COLUMN_NAME[0] + "=" + "\"" + Setting.REPLY_ALL + "\"";
            ContentValues args = new ContentValues();
            args.put(DBHelper.COLUMN_VALUE[0], reply);
            myDB.update(DBHelper.TABLE_SETTINGS, args, filter, null);
            myDB.setTransactionSuccessful();
        }
        catch (Exception e){
            myDB.endTransaction();
            Log.d(TAG, "ERROR: setReplyAll() failed");
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

        try {
            Cursor result = myDB.rawQuery(query, null);
            String response;
            if ((result != null) && (result.moveToFirst())){
                response = result.getString(result.getColumnIndex(DBHelper.COLUMN_VALUE[0]));
                result.close();
            }
            else {
                Log.d(TAG, "could not access cursor object from: " + query);
                throw new NullPointerException();
            }
            return response;
        }
        catch (Exception e){
            Log.d(TAG, "ERROR: getReplyAll() failed");
            throw e;
        }
    }

    public void setDelay(int minutes){
        /*final String query =
                "UPDATE " + DBHelper.TABLE_SETTINGS +
                " SET " + DBHelper.COLUMN_VALUE[0] +" = " + minutes +
                " WHERE " + DBHelper.COLUMN_NAME[0] + " = " + Setting.TIME_DELAY;*/

        myDB.beginTransaction();
        try {
            String filter = DBHelper.COLUMN_NAME[0] + "=" + Setting.TIME_DELAY;
            ContentValues args = new ContentValues();
            args.put(DBHelper.COLUMN_VALUE[0], minutes);
            myDB.update(DBHelper.TABLE_SETTINGS, args, filter, null);
            myDB.setTransactionSuccessful();
        }
        catch (Exception e){
            Log.d(TAG, "ERROR: setDelay() failed");
            myDB.endTransaction();
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
                Log.d(TAG, "could not access cursor object from: " + query);
                throw new NullPointerException();
            }
            return delay;
        }
        catch (Exception e){
            Log.d(TAG, "ERROR: getDelay() failed");
            myDB.endTransaction();
            throw e;
        }
    }

    public void setResponseToggle(boolean responseToggle){
        myDB.beginTransaction();
        String toggle;

        //convert boolean to string
        if(responseToggle){
            toggle = "true";
        }
        else{
            toggle = "false";
        }

        try {
            String filter = DBHelper.COLUMN_NAME[0] + "=" + Setting.RESPONSE_TOGGLE;
            ContentValues args = new ContentValues();
            args.put(DBHelper.COLUMN_VALUE[0], toggle);
            myDB.update(DBHelper.TABLE_SETTINGS, args, filter, null);
            myDB.setTransactionSuccessful();
        }
        catch (Exception e){
            Log.d(TAG, "ERROR: setResponseToggle() failed");
            myDB.endTransaction();
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
                    Log.d(TAG, "found " + response + " when a value of true or false was expected from: " + query);
                    throw new InputMismatchException();
                }
                result.close();
            }
            else {
                Log.d(TAG, "could not get/access cursor object from: " + query);
                throw new NullPointerException();
            }
            return toggle;
        }
        catch (Exception e){
            Log.d(TAG, "ERROR: getResponseToggle() failed");
            throw e;
        }
    }
}
