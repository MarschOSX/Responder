package com.seniordesign.autoresponder;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.seniordesign.autoresponder.Interface.ContactsList;
import com.seniordesign.autoresponder.Interface.SingleContact;
import com.seniordesign.autoresponder.Interface.SingleGroup;

public class SingleGroupTest extends ActivityInstrumentationTestCase2<SingleGroup> {
    public SingleGroupTest() {
        super(SingleGroup.class);
    }

    public void testActivityExists() {
        SingleGroup activity = new SingleGroup();
        assertNotNull(activity);
    }

}