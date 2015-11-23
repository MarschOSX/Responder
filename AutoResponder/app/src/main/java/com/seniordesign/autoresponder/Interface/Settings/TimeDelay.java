package com.seniordesign.autoresponder.Interface.Settings;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;
import com.seniordesign.autoresponder.R;

public class TimeDelay extends AppCompatActivity {
    int responseDelay = 20;
    private DBInstance db;
    EditText setDelayNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_delay);

        this.db = DBProvider.getInstance(false, getApplicationContext());
        setTimeRadioButton();

        setDelayNum   = (EditText)findViewById(R.id.customVal);
        setDelayNum.requestFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_settings, menu);
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

    private void setTimeRadioButton(){
        DBInstance db = DBProvider.getInstance(false, getApplicationContext());
        RadioButton timeDelayRB;
        Integer timeDelay = db.getDelay();
        if(timeDelay == 5){
            timeDelayRB = (RadioButton) findViewById(R.id.radioButton_5);
            timeDelayRB.setChecked(true);
        }else if(timeDelay == 20){
            timeDelayRB = (RadioButton) findViewById(R.id.radioButton_20);
            timeDelayRB.setChecked(true);
        }else if(timeDelay == 60){
            timeDelayRB = (RadioButton) findViewById(R.id.radioButton_60);
            timeDelayRB.setChecked(true);
        }else{
            timeDelayRB = (RadioButton) findViewById(R.id.radioButton_custom);
            timeDelayRB.setChecked(true);
            EditText customMinText = (EditText) findViewById(R.id.customVal);
            customMinText.setHint(timeDelay.toString());
        }

    }

    public void radioButtonDelaySet(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.fiveMin_radioButton:
                if (checked)
                    responseDelay = 5;
                break;
            case R.id.twentyMin_radioButton:
                if (checked)
                    responseDelay = 20;
                break;
            case R.id.oneHour_radioButton:
                if (checked)
                    responseDelay = 60;
                break;
            case R.id.custom_option:
                if (checked)
                    try{
                        responseDelay = Integer.parseInt(setDelayNum.getText().toString());
                    }catch(NumberFormatException e){
                        responseDelay = Integer.parseInt(setDelayNum.getHint().toString());
                    }
                break;
        }
        Log.v("Time Delay:", Integer.toString(responseDelay));
        //Default is 20 and the RadioButton is set to this
        //DBInstance db = DBProvider.getInstance(false, getApplicationContext());
        db.setDelay(responseDelay);
    }
}
