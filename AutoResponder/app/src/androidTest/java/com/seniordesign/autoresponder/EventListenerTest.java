package com.seniordesign.autoresponder;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.test.ApplicationTestCase;

import com.seniordesign.autoresponder.Persistance.DBProvider;

import org.junit.Before;
import org.junit.Ignore;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;


public class EventListenerTest extends ApplicationTestCase<Application> {
    public EventListenerTest() {
        super(Application.class);
    }

    @Mock
    Intent intent;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        intent = PowerMockito.mock(Intent.class);
        PowerMockito.mockStatic(Intent.class);
    }

    @Mock
    Context context;

    /*
    public void testonReceive() throws Exception {
        EventListener el = new EventListener();
        PowerMockito.when(intent.getAction()).thenReturn("");
        el.onReceive(context, intent);
    }*/
}