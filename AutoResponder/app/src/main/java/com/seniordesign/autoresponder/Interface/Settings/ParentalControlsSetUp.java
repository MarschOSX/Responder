package com.seniordesign.autoresponder.Interface.Settings;

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

import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;
import com.seniordesign.autoresponder.R;

public class ParentalControlsSetUp extends AppCompatActivity {
    private DBInstance db;
    Switch parentalControlsEnabler;
    Button setParentalPhoneNumber;
    Button deleteParentalPhoneNumber;
    EditText parentalPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Set Up the basics
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parental_controls_set_up);
        //Intent intent = getIntent();
        this.db = DBProvider.getInstance(false, getApplicationContext());

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
        //TODO from DB
        //parentalControlsEnabler.setChecked();
        //parentalPhoneNumber.setHint();
    }

    public void setParentPhoneNumber(){
        String parentNumber = parentalPhoneNumber.getText().toString();

        if (parentNumber.matches("")) {//Its blank, get default hint
            parentNumber = parentalPhoneNumber.getHint().toString();
        }
        //we don't wanna set the hint as a number in the DB
        if(!parentNumber.matches(parentalPhoneNumber.getHint().toString())){
            parentalPhoneNumber.setText(null);
            //TODO send out 2 SMS, to previous number and new number
            //only if they are not null!!
            //TODO set the parentalPhoneNumber in the DB
        }
    }

    public void deleteParentPhoneNumber(){
        parentalPhoneNumber.setHint("Enter Phone Number");
        //TODO Send out SMS to old number if not null
        //TODO delete parent phone number from DB

    }
}
