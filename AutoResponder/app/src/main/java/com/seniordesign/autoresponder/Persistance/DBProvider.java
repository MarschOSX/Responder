package com.seniordesign.autoresponder.Persistance;

import android.content.Context;

/**
 * Created by Garlan on 10/5/2015.
 */
public class DBProvider {
    private static DBInstance testDB;
    private static DBInstance permDB;

    public static DBInstance getInstance(boolean testMode, Context context){
        if (testMode){
            if (testDB == null){
                testDB = new TestDBInstance();
            }
            return testDB;
        }
        else{
            permDB = new PermDBInstance(context);
            return permDB;
        }
    }
}
