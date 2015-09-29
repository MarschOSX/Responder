package com.seniordesign.autoresponder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Garlan on 9/28/2015.
 */
public class DataBase {
    private DBHelper myLittleHelper;
    private SQLiteDatabase myDB;


    public DataBase(Context context) {
        //this.mDB = SQLiteDatabase.openOrCreateDatabase(DATABASE_NAME, null);
        this.myLittleHelper = new DBHelper(context);
        this.myDB = myLittleHelper.getWritableDatabase();
    }

    public void setReplyAll(String reply){
        final String response = reply;

    }

    public String getReplyAll(){
        return "working on it";
    }

    public void setDelay(int minutes){
        final int min = minutes;
    }

    public int getDelay(){
        return 20;
    }
}
