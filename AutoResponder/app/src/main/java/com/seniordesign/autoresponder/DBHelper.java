package com.seniordesign.autoresponder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Garlan on 9/29/2015.
 */
public class DBHelper extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "autoResponder.db";
    public static final int DATABASE_VERSION = 1; //**IMPORTANT** increment version if you make changes to table structure or add a new table

    //TABLE INFO HERE:
    //Column info is stored as a 2 element array with 0 the title and 1 the datatype
    //follow the below format for creating tables
    public static final String TABLE_HISTORY = "history";
    public static final String[] COLUMN_TIME = {"time_stamp", "DATE"};
    public static final String[] COLUMN_LOG  = {"log_entry", "TEXT"};
    private static final String CREATE_HISTORY = "CREATE TABLE " + TABLE_HISTORY +
            "(" + COLUMN_TIME[0] + " " + COLUMN_TIME[1] + ", " +
            COLUMN_LOG[0] + " " + COLUMN_LOG[1] + ");";

    public static final String TABLE_SETTINGS = "settings";
    public static final String[] COLUMN_NAME = {"setting_name", "VARCHAR(30)"};
    public static final String[] COLUMN_VALUE = {"value", "VARCHAR(30)"};
    private static final String CREATE_SETTINGS = "CREATE TABLE " + TABLE_SETTINGS +
            "(" + COLUMN_NAME[0] + " " + COLUMN_NAME[1] + ", " +
            COLUMN_VALUE[0] + " " + COLUMN_VALUE[1] + ");";
    //add settings here
    private static final String ROW_TIMEDELAY = "";
    private static final String ROW_REPLYALL = "";

    //all tables must be added to this list
    public static final String[] TABLE_LIST = {TABLE_HISTORY, TABLE_SETTINGS};

    //as well as all creation statements to this list
    private static final String[] CREATION_LIST = {CREATE_HISTORY, CREATE_SETTINGS};

    //all settings need to be added to this list
    private static final String[] SETTING_LIST = {ROW_TIMEDELAY, ROW_REPLYALL};
    ////////////

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //initializes tables
        for(String create : CREATION_LIST){
            db.execSQL(create);
        }

        //initialize settings
        for(String setting : SETTING_LIST){
            db.beginTransaction();
            try {

                db.setTransactionSuccessful();
            }
            catch (Exception e){
                db.setTransactionSuccessful();
            }
            finally {

            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for(String table : TABLE_LIST){
            db.execSQL("DROP IF TABLE EXISTS " + table);
        }
    }
}
