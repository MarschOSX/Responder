package com.seniordesign.autoresponder;

import android.app.Application;
import android.content.Context;
import android.test.ApplicationTestCase;

import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;

import org.mockito.Mock;
import org.mockito.Mockito;






public class EventHandlerTest extends ApplicationTestCase<Application> {


    @Mock
    Context context;

    public EventHandlerTest() {
        super(Application.class);
    }

    public void testRespondToText() throws Exception {
        DBProvider dbp = Mockito.mock(DBProvider.class);
        DBInstance dbi = Mockito.mock(DBInstance.class);
       //AndroidTestCase:getContext();
        Mockito.when(dbp.getInstance(false, context)).thenReturn(dbi);
        //assertSame(EventHandler.respondToText(null, "", 0L, context, true), -1);
        assertSame(EventHandler.tester(null, "", 0L, context, true), -1);

    }
}