package com.seniordesign.autoresponder;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.seniordesign.autoresponder.Interface.GeneralResponse;

public class GeneralResponseTest extends ActivityInstrumentationTestCase2<GeneralResponse> {
    public GeneralResponseTest() {
        super(GeneralResponse.class);
    }

    public void testActivityExists() {
        GeneralResponse activity = getActivity();
        assertNotNull(activity);
    }

    @UiThreadTest
    public void testRadioButtonDelaySet() {
        GeneralResponse activity = getActivity();

        //check initial conditions of the buttons
        final RadioButton fiveMin = (RadioButton) activity.findViewById(R.id.fiveMin_radioButton);
        assertNotNull(fiveMin);
        assertFalse(fiveMin.isChecked());
        final RadioButton twentyMin = (RadioButton) activity.findViewById(R.id.twentyMin_radioButton);
        assertNotNull(twentyMin);
        assertTrue(twentyMin.isChecked());
        final RadioButton oneHour = (RadioButton) activity.findViewById(R.id.oneHour_radioButton);
        assertNotNull(oneHour);
        assertFalse(oneHour.isChecked());
        final RadioButton customMin = (RadioButton) activity.findViewById(R.id.custom_option);
        assertNotNull(customMin);
        assertFalse(customMin.isChecked());

        //Original preset
        assertEquals(activity.checkResponseDelay(), 20);

        //Change the Preset
        fiveMin.setChecked(true);
        twentyMin.setChecked(false);
        final View fiveMinView = activity.findViewById(R.id.fiveMin_radioButton);
        activity.radioButtonDelaySet(fiveMinView);
        assertEquals(activity.checkResponseDelay(), 5);

        //Change back to the Preset
        twentyMin.setChecked(true);
        fiveMin.setChecked(false);
        final View twentyMinView = activity.findViewById(R.id.twentyMin_radioButton);
        activity.radioButtonDelaySet(twentyMinView);
        assertEquals(activity.checkResponseDelay(), 20);
    }

    @UiThreadTest
    public void testReplyAll() {
        GeneralResponse activity = getActivity();
        final EditText genResTxt = (EditText) activity.findViewById(R.id.generalResponse_text);
        assertEquals(genResTxt.getHint(), "I am busy right now");
        String input = "test";
        genResTxt.setText(input);
        assertNotSame(genResTxt.getText().toString(), input);
    }



}