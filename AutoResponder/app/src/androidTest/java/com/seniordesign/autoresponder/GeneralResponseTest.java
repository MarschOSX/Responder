package com.seniordesign.autoresponder;

import android.test.ActivityInstrumentationTestCase2;
import com.seniordesign.autoresponder.Interface.GeneralResponse;


public class GeneralResponseTest extends ActivityInstrumentationTestCase2<GeneralResponse> {
    public GeneralResponseTest() {
        super(GeneralResponse.class);
    }

    public void testActivityExists() {
        GeneralResponse activity = getActivity();
        assertNotNull(activity);
    }

    public void testA() {
        GeneralResponse activity = getActivity();
        assertNotNull(activity);
    }

}