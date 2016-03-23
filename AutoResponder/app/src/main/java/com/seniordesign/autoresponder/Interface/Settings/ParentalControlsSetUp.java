package com.seniordesign.autoresponder.Interface.Settings;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;
import com.seniordesign.autoresponder.R;
import com.seniordesign.autoresponder.Receiver.SMSSender;

public class ParentalControlsSetUp extends AppCompatActivity {
    private DBInstance db;
    Switch parentalControlsEnabler;
    Button setParentalPhoneNumber;
    Button deleteParentalPhoneNumber;
    EditText parentalPhoneNumber;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Set Up the basics
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parental_controls_set_up);
        //Intent intent = getIntent();
        this.db = DBProvider.getInstance(false, getApplicationContext());
        context = getApplicationContext();


        //Set up the features and set them according to the DB
        parentalControlsEnabler = (Switch) findViewById(R.id.switchParentalControls);
        parentalPhoneNumber = (EditText) findViewById(R.id.parentalPhoneNumber);
        setParentalPhoneNumber = (Button)findViewById(R.id.changeParentalAlertNumber);
        deleteParentalPhoneNumber = (Button)findViewById(R.id.deleteParentalAlertNumber);
        setUpFeatures();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_parental_controls_set_up, menu);
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

    private void setUpFeatures(){
        if(!db.getParentalControlsNumber().matches("0")){
            parentalPhoneNumber.setHint(db.getParentalControlsNumber());
        }
        buildSwitches();
    }


    public void setParentPhoneNumber(View view){
        //Send out SMS to old number if not 0
        SMSSender sender = new SMSSender(db);
        if(!db.getParentalControlsNumber().matches("0")) {
            sender.sendSMS("This number has just been removed from AutoResponder parental controls", "",
                    db.getParentalControlsNumber(), 0L, false, false);
        }

        //Set new parental controls number
        String parentPhoneNumber = parentalPhoneNumber.getText().toString();
        Log.v("Parent Phone Number:", parentPhoneNumber);
        if (parentPhoneNumber.contains(" ")  || parentPhoneNumber.matches("0")) {//Its blank, get default hint
            db.setParentalControlsNumber("0");
            db.setParentalControlsToggle(false);
        }else{
            //push universalReply to DB
            db.setParentalControlsNumber(parentPhoneNumber);
            //Send out SMS to new number
            sender.sendSMS("This number has just been added from AutoResponder parental controls", "",
                        db.getParentalControlsNumber(), 0L, false, false);
            db.setParentalControlsToggle(true);
        }
        setUpFeatures();
    }

    public void deleteParentPhoneNumber(View view){
        //Send out SMS to old number if not null
        SMSSender sender = new SMSSender(db);
        if(!db.getParentalControlsNumber().matches("0")) {
            sender.sendSMS("This number has just been removed from AutoResponder parental controls", "",
                    db.getParentalControlsNumber(), 0L, false, false);
        }
        //Delete parent phone number from DB
        parentalPhoneNumber.setHint("Enter Phone Number");
        db.setParentalControlsNumber("0");
        //Turn off switch
        db.setParentalControlsToggle(false);
        setUpFeatures();
    }

    //build the switches
    private void buildSwitches(){
        parentalControlsEnabler.setChecked(db.getParentalControlsToggle());
        parentalControlsEnabler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!db.getParentalControlsNumber().matches("0")) {
                    db.setParentalControlsToggle(parentalControlsEnabler.isChecked());
                }else{
                    parentalControlsEnabler.setChecked(false);
                    int duration = Toast.LENGTH_LONG;
                    CharSequence toastText;
                    Toast toast;
                    toastText = "Must Enter a Phone Number!";
                    toast = Toast.makeText(context, toastText, duration);
                    toast.show();
                }
            }
        });
    }
}
