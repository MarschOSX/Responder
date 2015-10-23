package com.seniordesign.autoresponder.activityTests;

import android.app.Application;
import android.content.Context;
import android.test.ApplicationTestCase;

import com.seniordesign.autoresponder.Persistance.DBProvider;
import com.seniordesign.autoresponder.Persistance.PermDBInstance;
import com.seniordesign.autoresponder.Receiver.EventHandler;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class PermDBInstanceTest extends ApplicationTestCase<Application> {
    public PermDBInstanceTest() {
        super(Application.class);
    }

    private Context context;

    public void testStandard() throws Exception {
        PermDBInstance pdbi = new PermDBInstance(context);
        pdbi.setReplyAll("Hello");
    }
}