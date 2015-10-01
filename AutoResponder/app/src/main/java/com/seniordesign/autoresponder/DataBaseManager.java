package com.seniordesign.autoresponder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Garlan on 9/28/2015.
 */

//note for setting up transactions: goto https://docs.oracle.com/javase/tutorial/jdbc/basics/transactions.html
public class DataBaseManager {
    private DBHelper myLittleHelper;
    private SQLiteDatabase myDB;


    public DataBaseManager(Context context) {
        //this.mDB = SQLiteDatabase.openOrCreateDatabase(DATABASE_NAME, null);
        this.myLittleHelper = new DBHelper(context);
        this.myDB = myLittleHelper.getWritableDatabase();
    }

    public void setReplyAll(String reply){
        final String query =
                "UPDATE " + DBHelper.TABLE_SETTINGS +
                " SET " + DBHelper.COLUMN_VALUE[0] +" = " + reply +
                " WHERE " + DBHelper.COLUMN_NAME[0] + " = " + Setting.REPLY_ALL;
        try{

        }
        finally {

        }
    }

    public String getReplyAll(){
        final String query = "SELECT " + DBHelper.COLUMN_VALUE[0] + " FROM " + DBHelper.TABLE_SETTINGS + " WHERE " + DBHelper.COLUMN_NAME[0] + " = " + Setting.REPLY_ALL;
        return "working on it";
    }

    public void setDelay(int minutes){
        final String query =
                "UPDATE " + DBHelper.TABLE_SETTINGS +
                " SET " + DBHelper.COLUMN_VALUE[0] +" = " + minutes +
                " WHERE " + DBHelper.COLUMN_NAME[0] + " = " + Setting.TIME_DELAY;

        try{

        }
        finally {

        }
    }

    public int getDelay(){
        final String query = "SELECT " + DBHelper.COLUMN_VALUE[0] + " FROM " + DBHelper.TABLE_SETTINGS + " WHERE " + DBHelper.COLUMN_NAME[0] + " = " + Setting.TIME_DELAY;
        return 20;
    }
}
