package com.seniordesign.autoresponder;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.test.AndroidTestCase;


/**
 * By MarschOSX on 10/24/2015
 *
 * Help with the creation of this test referenced from:
 * http://stackoverflow.com/questions/20299854/android-junit-test-broadcastreceiver-fails-on-abortbroadcast
 *
 */


public class SMSListenerTest extends AndroidTestCase {

    //Intent intent;
    //Context context;


    public void testOnReceive() throws Exception {
        Intent intent = new Intent("android.provider.Telephony.SMS_RECEIVED");
        Bundle bundle = new Bundle();

        assertNotNull(bundle);
        assertNotNull(intent);

        /**This will simulate a recieved message and invoke the BroadcastReciever
         * causing the onRecieve method in the SMSListener to be called
         * the Context and Intent (parameters of the function) are filled by the
         * lines below--->
         */
        intent.putExtra("pdus", new Object[] { bundle.get("pdus") });
        intent.setType("vnd.android-dir/mms-sms");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0,intent ,PendingIntent.FLAG_ONE_SHOT);
        try {
            pendingIntent.send(0);
            assertTrue(true);
        } catch (PendingIntent.CanceledException e) {
            /**
             * If this is reached, then there was an exception when sending the intent
             */
            assertTrue(false);
        }
    }
}