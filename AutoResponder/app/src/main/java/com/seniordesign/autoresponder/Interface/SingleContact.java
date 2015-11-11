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

        setTextButton = (Button)findViewById(R.id.setContactTextButton);
        setTextEdit   = (EditText)findViewById(R.id.contactResponse_text);


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
}
