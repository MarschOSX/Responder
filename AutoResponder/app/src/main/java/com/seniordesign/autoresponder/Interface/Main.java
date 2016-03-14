package com.seniordesign.autoresponder.Interface;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.seniordesign.autoresponder.Interface.Contacts.ContactsList;
import com.seniordesign.autoresponder.Interface.Groups.GroupList;
import com.seniordesign.autoresponder.Interface.Settings.UserSettings;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;
import com.seniordesign.autoresponder.R;


public class Main extends AppCompatActivity {
    private Switch mLocationToggle;
    private Switch mCalenderToggle;
    private Switch mResponseToggle;
    int CALENDAR_PERMISSIONS = 0;
    int LOACTION_PERMISSIONS = 0;
    int SEND_SMS_PERMISSIONS = 0;
    int RECEIVE_SMS_PERMISSIONS = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        //Get Receive SMS Permissions at startup
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Main.this, new String[]{Manifest.permission.RECEIVE_SMS}, RECEIVE_SMS_PERMISSIONS);
        }else{
            RECEIVE_SMS_PERMISSIONS = 1;
        }

        //Get Send SMS Permissions at startup
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Main.this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSIONS);
        }else{
            SEND_SMS_PERMISSIONS = 1;
        }

        //build the all the toggles
        buildSwitches();
    }

    //build the switches and add the listeners
    private void buildSwitches(){
        DBInstance db = DBProvider.getInstance(false, getApplicationContext());

        mResponseToggle = (Switch)findViewById(R.id.autoRespond_switch);
        mResponseToggle.setChecked(db.getResponseToggle());
        mResponseToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBInstance db = DBProvider.getInstance(false, getApplicationContext());
                db.setResponseToggle(mResponseToggle.isChecked());
                db.setTimeResponseToggleSet(System.currentTimeMillis());
            }
        });

        mCalenderToggle = (Switch)findViewById(R.id.calendar_switch);
        mCalenderToggle.setChecked(db.getActivityToggle());
        mCalenderToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get Calendar Permissions
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Main.this, new String[]{Manifest.permission.READ_CALENDAR}, CALENDAR_PERMISSIONS);
                }

                //Check Again
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                    DBInstance db = DBProvider.getInstance(false, getApplicationContext());
                    db.setActivityToggle(mCalenderToggle.isChecked());
                }else{
                    DBInstance db = DBProvider.getInstance(false, getApplicationContext());
                    db.setActivityToggle(false);
                    mCalenderToggle.setChecked(false);
                }
            }
        });


        mLocationToggle = (Switch)findViewById(R.id.location_switch);
        mLocationToggle.setChecked(db.getLocationToggle());
        mLocationToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get Location Permissions
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Main.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOACTION_PERMISSIONS);
                }

                //Check Again
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    DBInstance db = DBProvider.getInstance(false, getApplicationContext());
                    db.setLocationToggle(mLocationToggle.isChecked());
                } else {
                    DBInstance db = DBProvider.getInstance(false, getApplicationContext());
                    db.setLocationToggle(false);
                    mLocationToggle.setChecked(false);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void onBackPressed() {//on main screen, pressing back will take back to home!
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    /** Called when the user clicks the General Response Button*/
    public void gotoUniversalReply(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, UniversalReply.class);
        startActivity(intent);
    }

    public void goToLocationOutput(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, LocationOutput.class);
        startActivity(intent);
    }

    public void gotoSettings(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, UserSettings.class);
        startActivity(intent);
    }

    public void gotoContactsList(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, ContactsList.class);
        startActivity(intent);
    }

    public void gotoManageGroups(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, GroupList.class);
        startActivity(intent);
    }
}
