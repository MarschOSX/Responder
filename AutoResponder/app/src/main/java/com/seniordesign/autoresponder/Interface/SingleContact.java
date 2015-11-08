package com.seniordesign.autoresponder.Interface;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.seniordesign.autoresponder.DataStructures.Contact;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;
import com.seniordesign.autoresponder.R;

public class SingleContact extends AppCompatActivity {

    private DBInstance db;
    Button setTextButton;
    EditText setTextEdit;
    EditText setDelayNum;
    int responseDelay = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_contact);
        this.db = DBProvider.getInstance(false, getApplicationContext());
        Intent intent = getIntent();
        setUpContactInfo(intent);
        setEditText();
        setTimeRadioButton();

        setTextButton = (Button)findViewById(R.id.setContactTextButton);
        setTextEdit   = (EditText)findViewById(R.id.contactResponse_text);
        setDelayNum   = (EditText)findViewById(R.id.contactCustomMinText);
        setDelayNum.requestFocus();


        //This sends the General Response editText field into a string
        setTextButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        String generalReply = setTextEdit.getText().toString();
                        Log.v("General Reply:", generalReply);

                        if(generalReply == null || generalReply.matches("")){//Its blank, get default hint
                            generalReply = setTextEdit.getHint().toString();
                        }
                        //push generalReply to DB
                        //TODO db.setReplyAll(generalReply);

                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_single_contact, menu);
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

    public void setUpContactInfo(Intent intent){
        //get information that was passed from ContactsList
        String name = intent.getStringExtra("SINGLE_CONTACT_NAME");
        String phoneNumber = intent.getStringExtra("SINGLE_CONTACT_NUMBER");
        TextView contactName = (TextView) findViewById(R.id.contactName);
        TextView contactNumber = (TextView) findViewById(R.id.contactPhoneNumberTextView);
        contactName.setText(name);
        contactNumber.setText(phoneNumber);
        //Contact contactInfo = db.getContactInfo(phoneNumber);
        /**TODO MAJOR
         * Restructure this class for the contact info to be passed to and from the DB
         * using the contact itself. If they are blank that is ok too.
         */
    }

    private void setEditText(){
        //DBInstance db = DBProvider.getInstance(false, getApplicationContext());
        String replyAll = "I am busy right now";//TODO db.getReplyAll();
        TextView contactResponse = (TextView) findViewById(R.id.contactResponse_text);
        contactResponse.setHint(replyAll);
    }

    private void setTimeRadioButton(){
       // DBInstance db = DBProvider.getInstance(false, getApplicationContext());
        RadioButton timeDelayRB;
        Integer timeDelay = 20;//TODO db.getDelay();
        if(timeDelay == 5){
            timeDelayRB = (RadioButton) findViewById(R.id.contactFiveMin_radioButton);
            timeDelayRB.setChecked(true);
        }else if(timeDelay == 20){
            timeDelayRB = (RadioButton) findViewById(R.id.contactTwentyMin_radioButton);
            timeDelayRB.setChecked(true);
        }else if(timeDelay == 60){
            timeDelayRB = (RadioButton) findViewById(R.id.contactOneHour_radioButton);
            timeDelayRB.setChecked(true);
        }else{
            timeDelayRB = (RadioButton) findViewById(R.id.contactCustom_option);
            timeDelayRB.setChecked(true);
            EditText customMinText = (EditText) findViewById(R.id.contactCustomMinText);
            customMinText.setHint(timeDelay.toString());
        }

    }

    public void radioButtonDelaySet(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.contactFiveMin_radioButton:
                if (checked)
                    responseDelay = 5;
                break;
            case R.id.contactTwentyMin_radioButton:
                if (checked)
                    responseDelay = 20;
                break;
            case R.id.contactOneHour_radioButton:
                if (checked)
                    responseDelay = 60;
                break;
            case R.id.contactCustom_option:
                if (checked)
                    try{
                        responseDelay = Integer.parseInt(setDelayNum.getText().toString());
                    }catch(NumberFormatException e){
                        responseDelay = Integer.parseInt(setDelayNum.getHint().toString());
                    }
                break;
        }
        Log.v("Time Delay:", Integer.toString(responseDelay));
        //TODO db.setdelay to DB
        //Default is 20 and the RadioButton is set to this
        //DBInstance db = DBProvider.getInstance(false, getApplicationContext());
        //db.setDelay(responseDelay);
    }
}
