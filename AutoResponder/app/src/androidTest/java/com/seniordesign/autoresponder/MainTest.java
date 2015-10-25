package com.seniordesign.autoresponder;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;

import com.seniordesign.autoresponder.Interface.GeneralResponse;
import com.seniordesign.autoresponder.Interface.Main;

public class MainTest extends ActivityInstrumentationTestCase2<Main> {
    public MainTest() {
        super(Main.class);
    }

    public void testActivityExists() {
        Main activity = getActivity();
        assertNotNull(activity);
    }

    @UiThreadTest
    public void testOnOffToggle() {
        Main activity = getActivity();
        final Switch onOffToggle = (Switch) activity.findViewById(R.id.autoRespond_switch);
        assertNotNull(onOffToggle);
        activity.switchChecker(onOffToggle);
        assertTrue(onOffToggle.isChecked());
        onOffToggle.setChecked(false);
        activity.switchChecker(onOffToggle);
        assertFalse(onOffToggle.isChecked());
    }

    @UiThreadTest
    public void testGoToGeneralResponse() {
        Main activity = getActivity();
        final View genRespButton = activity.findViewById(R.id.generalReply_button);
        //activity.gotoGeneralResponse(genRespButton);
        //this causes to go to General Response activity, where it does nothing

    }


}