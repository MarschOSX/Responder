package com.seniordesign.autoresponder;


import android.content.Context;
import android.content.Intent;
import android.test.InstrumentationTestCase;


import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;


public class EventListenerTest extends InstrumentationTestCase {

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