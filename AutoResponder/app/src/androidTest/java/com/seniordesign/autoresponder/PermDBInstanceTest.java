package com.seniordesign.autoresponder;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.InstrumentationTestCase;
import android.test.mock.MockContext;

import com.seniordesign.autoresponder.DataStructures.ResponseLog;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;

import org.junit.Before;

import java.sql.Date;

/**
 * By MarschOSX on 10/24/2015
 */

public class PermDBInstanceTest extends AndroidTestCase {
    //Perm database
    private DBInstance database;

    public void testDBisNotNull()throws Exception {
        database = DBProvider.getInstance(false, getContext());
        assertNotNull(database);
    }

    public void testDbSetsAndGetsIssues()throws Exception {
        database = DBProvider.getInstance(false, getContext());
        String toSend = "HelloJUnit";
        database.setReplyAll(toSend);
        String fromDB = database.getReplyAll();
        /**
         * assertEquals does not work for DB calls
         * which is why we do it this way. We can uncomment for proof
         */
        //assertEquals(toSend, fromDB);

        if(fromDB.matches(toSend)){
            assertTrue(true);
        }else{
            assertTrue(false);
        }
    }

    public void testDbSetsAndGets()throws Exception {
        database = DBProvider.getInstance(false, getContext());

        //Test Response Toggle
        database.setResponseToggle(true);
        if(database.getResponseToggle()){
            assertTrue(true);
        }else{
            assertTrue(false);
        }
        database.setResponseToggle(false);
        if(!database.getResponseToggle()){
            assertTrue(true);
        }else{
            assertTrue(false);
        }

        //Test delay set
        database.setDelay(15);
        if(database.getDelay() == 15){
            assertTrue(true);
        }else{
            assertTrue(false);
        }
        database.setDelay(0);
        if(database.getDelay() == 0){
            assertTrue(true);
        }else{
            assertTrue(false);
        }
    }

    public void testDbSetsAndGetsResponseLogs()throws Exception {
        database = DBProvider.getInstance(false, getContext());

        //Create ResponseLog to test
        long millis = System.currentTimeMillis() % 1000;
        ResponseLog responseLog = new ResponseLog("JUnitA","JUnitB","+18568327320", new Date(millis));

        database.addToResponseLog(responseLog);
        assertEquals(database.getFirstEntry(), responseLog);

        database.getLastEntryByNum("+18568327320");
    }



}