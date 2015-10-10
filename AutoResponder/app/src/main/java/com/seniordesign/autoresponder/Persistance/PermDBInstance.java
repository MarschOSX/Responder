package com.seniordesign.autoresponder.Persistance;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.util.Log;

import com.seniordesign.autoresponder.DataStructures.Setting;

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
        Log.d(TAG, "constructing instance");
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

        //myDB.beginTransaction();
        try {
            Cursor result = myDB.rawQuery(query, null);
            String col = result.getColumnName(0);
            Log.d(TAG, "Query: "+ query);
            Log.d(TAG, "Column count is: " + result.getColumnCount());
            Log.d(TAG, "Column name is: " + col);
            Log.d(TAG, "Column index is: " + result.getColumnIndex(col));

            String response = result.getString(0);
            //myDB.setTransactionSuccessful();
            return response;
        }
        catch (Exception e){
           // myDB.endTransaction();
            throw e;
        }
        finally {
           // myDB.endTransaction();
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
            myDB.endTransaction();
            throw e;
        }
        finally {
            //
            myDB.endTransaction();
        }
    }

    public int getDelay(){
        final String query =
                "SELECT " + DBHelper.COLUMN_VALUE[0] +
                 " FROM " + DBHelper.TABLE_SETTINGS +
                 " WHERE " + DBHelper.COLUMN_NAME[0] + " = " + "\"" + Setting.TIME_DELAY + "\"";

        myDB.beginTransaction();
        try {
            Cursor result = myDB.rawQuery(query, null);
            int delay = result.getInt(0);
            myDB.setTransactionSuccessful();
            return delay;
        }
        catch (Exception e){
            myDB.endTransaction();
            throw e;
        }
        finally {
            myDB.endTransaction();
        }
    }

    public void setResponseToggle(boolean responseToggle){
        myDB.beginTransaction();
        try {

            myDB.setTransactionSuccessful();
        }
        catch (Exception e){
            myDB.endTransaction();
            throw e;
        }
        finally {
            myDB.endTransaction();
        }
    }

    public boolean getResponseToggle(){
        myDB.beginTransaction();
        try {

            myDB.setTransactionSuccessful();
        }
        catch (Exception e){
            myDB.endTransaction();
            throw e;
        }
        finally {
            myDB.endTransaction();
        }
        return false;
    }
}
