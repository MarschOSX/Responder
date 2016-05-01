package com.seniordesign.autoresponder.Interface.Settings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;
import com.seniordesign.autoresponder.R;
import com.seniordesign.autoresponder.Services.AlarmService;
import com.seniordesign.autoresponder.Services.TimeLimitExpired;

public class TimeLimit extends AppCompatActivity {
    private static final String TAG = "TimeLimit";
    int activeTime = 60;
    private DBInstance db;
    EditText setTimeLimit;

    RadioButton indefinite;
    RadioButton oneHourRB;
    RadioButton twoHourRB;
    RadioButton eightHourRB;
    RadioButton customHourRB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_limit);

        this.db = DBProvider.getInstance(false, getApplicationContext());

        this.indefinite = (RadioButton) findViewById(R.id.radioButton_IndefTimeLimit);
        this.oneHourRB = (RadioButton) findViewById(R.id.radioButton_60timeLimit);
        this.twoHourRB = (RadioButton) findViewById(R.id.radioButton_120timeLimit);
        this.eightHourRB = (RadioButton) findViewById(R.id.radioButton_480timeLimit);
        this.customHourRB = (RadioButton) findViewById(R.id.radioButton_customTimeLimit);

        //set listener to be called when text is changed
        setTimeLimit   = (EditText)findViewById(R.id.customValtimeLimit);
        setTimeLimit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String time = s.toString();
                if (!time.matches("")){
                    customHourRB.setChecked(true);
                    db.setTimeLimit(Integer.parseInt(time));
                }
            }
        });

        setTimeLimitRadioButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time_limit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setTimeLimitRadioButton() {
        //DBInstance db = DBProvider.getInstance(false, getApplicationContext());
        Integer timeDelay = db.getTimeLimit();
        Log.v(TAG, "time limit from DB is " + Integer.toString(timeDelay));
        if(timeDelay == 100){
            indefinite.setChecked(true);
        }else if(timeDelay == 1){
            oneHourRB.setChecked(true);
        }else if(timeDelay == 2){
            twoHourRB.setChecked(true);
        }else if(timeDelay == 8){
            eightHourRB.setChecked(true);
        }else{
            customHourRB.setChecked(true);
            EditText customValTimeLimit = (EditText) findViewById(R.id.customValtimeLimit);
            customValTimeLimit.setHint(Integer.toString(timeDelay));
        }

    }

    public void radioButtonTimeLimitSet(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButton_IndefTimeLimit:
                if (checked)
                    activeTime = 100;
                break;
            case R.id.radioButton_60timeLimit:
                if (checked)
                    activeTime = 1;
                break;
            case R.id.radioButton_120timeLimit:
                if (checked)
                    activeTime = 2;
                break;
            case R.id.radioButton_480timeLimit:
                if (checked)
                    activeTime = 8;
                break;
            case R.id.radioButton_customTimeLimit:
                if (checked)
                    try {
                        activeTime = Integer.parseInt(setTimeLimit.getText().toString());
                    } catch (NumberFormatException e) {
                        activeTime = Integer.parseInt(setTimeLimit.getHint().toString());
                    }
                break;
        }
        Log.v(TAG, "setting time limit to (in hours): " + Integer.toString(activeTime));
        db.setTimeLimit(activeTime);

        if(db.getResponseToggle() && db.getTimeLimit() != 100) {//if set to on, activate TimeLimitExpired alarm to turn off notification!
            Log.e(TAG, "Setting the alarm");
            int timeLimitInSeconds = db.getTimeLimit() * 3600;
            AlarmService alarmService = new AlarmService(getApplicationContext(), TimeLimitExpired.class);
            Log.e(TAG, "Service was created!");
            alarmService.setTimeLimitCountdown(timeLimitInSeconds);
            Log.e(TAG, "Alarm was started");
        }else{
            Log.e(TAG, "Alarm was NOT started, time limit is indefinite OR toggle is off!");
        }

        db.setTimeResponseToggleSet(System.currentTimeMillis());
        db.getResponseToggle();
    }

}
