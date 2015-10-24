package com.seniordesign.autoresponder;

import android.app.Application;
import android.content.Context;
import android.telephony.SmsManager;
import android.test.ApplicationTestCase;
import android.test.InstrumentationTestCase;

import com.seniordesign.autoresponder.DataStructures.Log;
import com.seniordesign.autoresponder.DataStructures.ResponseLog;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;
import com.seniordesign.autoresponder.Persistance.PermDBInstance;
import com.seniordesign.autoresponder.Persistance.TestDBInstance;
import com.seniordesign.autoresponder.Receiver.EventHandler;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.Date;
import java.util.ArrayList;

//@RunWith(PowerMockRunner.class)
//@PrepareForTest(DBProvider.class)
public class EventHandlerTest extends InstrumentationTestCase {
    @Mock
    private DBInstance dbi;

    public void testRespondToText() throws Exception {
        dbi = new DBInstance() {
            @Override
            public void setReplyAll(String reply) {

            }

            @Override
            public String getReplyAll() {
                return "I am busy right now JUNIT";
            }

            @Override
            public void setDelay(int minutes) {

            }

            @Override
            public int getDelay() {
                return 0;
            }

            @Override
            public void setResponseToggle(boolean toggle) {

            }

            @Override
            public boolean getResponseToggle() {
                return true;
            }

            @Override
            public void addToResponseLog(ResponseLog newLog) {

            }

            @Override
            public ResponseLog getFirstEntry() {
                return null;
            }

            @Override
            public ResponseLog getLastEntry() {
                return null;
            }

            @Override
            public ResponseLog getEntry(int index) {
                return null;
            }

            @Override
            public ResponseLog getLastEntryByNum(String phoneNum) {
                Date testDate = new Date(0);
                ResponseLog updateLog = new ResponseLog("JUNIT MESSAGE SENT",
                        "JUNIT MESSAGE RECIEVED", "+18568327320", testDate);
                return updateLog;
            }

            @Override
            public ArrayList<ResponseLog> getEntryByDateRange(Date start, Date end) {
                return null;
            }

            @Override
            public ArrayList<ResponseLog> getEntryRange(int start, int end) {
                return null;
            }
        };
        assertNotNull(dbi);
        EventHandler ev= new EventHandler(dbi);
        assertSame(ev.respondToText(null, "", 0L, true), -1);
        assertSame(ev.respondToText("", "", 0L, true), -1);
        assertSame(ev.respondToText("1", "", 0L, true), -1);
        assertSame(ev.respondToText("123z135", "", 0L, true), -1);
        assertSame(ev.respondToText("85683273201111", "", 0L, true), -1);
        assertSame(ev.respondToText("+18568327320", "", -1L, true), -1);
        assertSame(ev.respondToText("+18568327320", "", 0L, true), 0);
    }
}