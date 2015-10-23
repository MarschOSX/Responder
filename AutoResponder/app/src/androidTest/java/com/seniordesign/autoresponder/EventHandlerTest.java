package com.seniordesign.autoresponder;

import android.app.Application;
import android.content.Context;
import android.telephony.SmsManager;
import android.test.ApplicationTestCase;

import com.seniordesign.autoresponder.DataStructures.Log;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;
import com.seniordesign.autoresponder.Persistance.PermDBInstance;
import com.seniordesign.autoresponder.Persistance.TestDBInstance;
import com.seniordesign.autoresponder.Receiver.EventHandler;


import org.junit.Before;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DBProvider.class)
public class EventHandlerTest extends ApplicationTestCase<Application> {
    @Mock
    private DBProvider dbp;
    private SmsManager sms;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        dbp = PowerMockito.mock(DBProvider.class);
        PowerMockito.mockStatic(DBProvider.class);
        sms = PowerMockito.mock(SmsManager.class);
        PowerMockito.mockStatic(SmsManager.class);
    }

    @Mock
    Context context;



    public EventHandlerTest() {
        super(Application.class);
    }

    public void testRespondToText() throws Exception {
        //DBProvider dbp = Mockito.mock(DBProvider.class);
        //DBInstance dbi = Mockito.mock(DBInstance.class);
       //AndroidTestCase:getContext();
        //PowerMockito.when(dbp.getInstance(false, context)).thenReturn(null);
        //assertSame(EventHandler.respondToText(null, "", 0L, context, true), -1);
        EventHandler ev= new EventHandler(DBProvider.getInstance(true, context));
        assertSame(ev.respondToText(null, "", 0L, true), -1);
        assertSame(ev.respondToText("", "", 0L, true), -1);
        assertSame(ev.respondToText("1", "", 0L, true), -1);
        assertSame(ev.respondToText("123z135", "", 0L, true), -1);
        assertSame(ev.respondToText("856832732011", "", 0L, true), -1);
        assertSame(ev.respondToText("+18568327320", "", null, true), -1);
        assertSame(ev.respondToText("+18568327320", "", -1L, true), -1);
        assertSame(ev.respondToText("+18568327320", "", 0L, true), 0);
        assertSame(ev.respondToText("+18568327320", "", 1L, true), 0);
    }
}