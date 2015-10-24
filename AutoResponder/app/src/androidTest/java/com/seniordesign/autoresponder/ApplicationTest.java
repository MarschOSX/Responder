package com.seniordesign.autoresponder;

import android.test.InstrumentationTestCase;

public class ApplicationTest extends InstrumentationTestCase {

    public void testStandard() throws Exception {
        final int expected = 1;
        final int reality = 5;
        assertNotSame(expected, reality);
    }
}