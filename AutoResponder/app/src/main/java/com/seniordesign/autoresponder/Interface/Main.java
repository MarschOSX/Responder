package com.seniordesign.autoresponder.Interface;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;

import com.seniordesign.autoresponder.Interface.Contacts.ContactsList;
import com.seniordesign.autoresponder.Interface.Groups.GroupList;
import com.seniordesign.autoresponder.Interface.Settings.UserSettings;
import com.seniordesign.autoresponder.Permissions.PermissionsChecker;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;
import com.seniordesign.autoresponder.R;
import com.seniordesign.autoresponder.Services.DrivingDetectionService;
import com.seniordesign.autoresponder.Services.ParentalControlsWatcher;

import java.security.InvalidParameterException;


public class Main extends AppCompatActivity {
    private Switch mLocationToggle;
    private Switch mCalenderToggle;
    private Switch mResponseToggle;
    private Switch mDrivingDetectionToggle;
    private Main me;
    private DBInstance db;
    private final int ACTIVITY_PERMISSIONS = 1;
    private final int LOCATION_PERMISSIONS = 2;
    private final int SEND_SMS_PERMISSIONS = 3;
    private final int RECEIVE_SMS_PERMISSIONS = 4;
    private final int READ_SMS_PERMISSIONS = 5;
    private final int DRIVING_DETECTION = 6;
    public static final String TAG = "Main";


    @Override
    protected void onResume(){
        super.onResume();
        db = DBProvider.getInstance(false, getApplicationContext());
        buildSwitches();
        checkParentalControls();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = DBProvider.getInstance(false, getApplicationContext());
        me = this;

        setContentView(R.layout.activity_main);

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        //Get Receive SMS Permissions at startup
        PermissionsChecker.checkReceiveSMSPermission(me, getApplicationContext(), RECEIVE_SMS_PERMISSIONS);

        //Get Send SMS Permissions at startup
        PermissionsChecker.checkSendSMSPermission(me, getApplicationContext(), SEND_SMS_PERMISSIONS);


        //build the all the toggles
        checkParentalControls();
        buildSwitches();


    }

    private void checkParentalControls(){
        //if Parental Controls are on
        //Get Read SMS Permissions
        PermissionsChecker.checkReadSMSPermission(me, getApplicationContext(), READ_SMS_PERMISSIONS);

        //builds at start of application
        if(db.getParentalControlsToggle()){
            //Start service to monitor text messages
            if (!ParentalControlsWatcher.isRunning(getApplicationContext())) {
                Log.d(TAG, "ParentalControls Service is starting!");
                getApplicationContext().startService(new Intent(getApplicationContext(), ParentalControlsWatcher.class));
            }else{
                Log.d(TAG, "ParentalControls Service is already running!");
            }
        }
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
                db.getResponseToggle();//to see if timeLimit is se;
            }
        });

        mCalenderToggle = (Switch)findViewById(R.id.calendar_switch);
        mCalenderToggle.setChecked(db.getActivityToggle());
        if(PermissionsChecker.checkReadCalendarPermission(null, getApplicationContext(), ACTIVITY_PERMISSIONS)){
            mCalenderToggle.setChecked(true);
        }

        mCalenderToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBInstance db = DBProvider.getInstance(false, getApplicationContext());
                //Get Activity Permissions
                if (!PermissionsChecker.checkReadCalendarPermission(me, getApplicationContext(), ACTIVITY_PERMISSIONS)) {
                    db.setActivityToggle(false);
                    mCalenderToggle.setChecked(false);
                } else {
                    db.setActivityToggle(mCalenderToggle.isChecked());
                }
            }
        });


        mLocationToggle = (Switch)findViewById(R.id.location_switch);
        mLocationToggle.setChecked(db.getLocationToggle());

        mLocationToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBInstance db = DBProvider.getInstance(false, getApplicationContext());
                //Get Location Permissions
                if (!PermissionsChecker.checkAccessLocationPermission(me, getApplicationContext(), LOCATION_PERMISSIONS)) {
                    db.setLocationToggle(false);
                    mLocationToggle.setChecked(false);
                } 
            }
        });


        mDrivingDetectionToggle = (Switch)findViewById(R.id.driving_detection_toggle);

        mDrivingDetectionToggle.setChecked(db.getDrivingDetectionToggle());

        if(db.getParentalControlsToggle()) {
            mDrivingDetectionToggle.setEnabled(false);
        }
        else {
            mDrivingDetectionToggle.setEnabled(true);
        }

        mDrivingDetectionToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                DBInstance db = DBProvider.getInstance(false, context);

                if (mDrivingDetectionToggle.isChecked()) {
                    if (!DrivingDetectionService.isRunning(context) && PermissionsChecker.checkAccessLocationPermission(me, context, DRIVING_DETECTION)) {
                        db.setDrivingDetectionToggle(true);
                        context.startService(new Intent(context, DrivingDetectionService.class));
                    }
                } else {
                    //make sure that parental controls is not enabled
                    if (!db.getParentalControlsToggle()) {
                        //disable service
                        if (DrivingDetectionService.isRunning(context)) {
                            db.setDrivingDetectionToggle(false);
                            context.stopService(new Intent(context, DrivingDetectionService.class));
                        }
                    } else {
                        mDrivingDetectionToggle.setChecked(true);
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        DBInstance db =  DBProvider.getInstance(false, getApplicationContext());

        // handle if access is granted
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case LOCATION_PERMISSIONS:
                    if (db != null) db.setLocationToggle(true);
                    break;
                case ACTIVITY_PERMISSIONS:
                    if (db != null) db.setActivityToggle(true);
                    break;
                case RECEIVE_SMS_PERMISSIONS:
                    break;
                case SEND_SMS_PERMISSIONS:
                    break;
                case READ_SMS_PERMISSIONS:
                    break;
                case DRIVING_DETECTION:
                    // if permissions are granted
                    if (db != null) db.setDrivingDetectionToggle(true);
                    getApplicationContext().startService(new Intent(getApplicationContext(), DrivingDetectionService.class));
                    // if permissions denied
                    break;
                default:
                    throw new InvalidParameterException("Unknown request code: " + requestCode);
            }
        }
        // handle if access is not granted
        else {
            switch (requestCode) {
                case LOCATION_PERMISSIONS:
                    if (db != null) db.setLocationToggle(false);
                    mLocationToggle.setChecked(false);
                    break;
                case ACTIVITY_PERMISSIONS:
                    if (db != null) db.setActivityToggle(false);
                    mCalenderToggle.setChecked(false);
                    break;
                case RECEIVE_SMS_PERMISSIONS:
                    break;
                case SEND_SMS_PERMISSIONS:
                    break;
                case READ_SMS_PERMISSIONS:
                    break;
                case DRIVING_DETECTION:
                    if (db != null) db.setDrivingDetectionToggle(false);
                    mDrivingDetectionToggle.setChecked(false);
                    break;
                default:
                    throw new InvalidParameterException("Unknown request code: " + requestCode);
            }
        }
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

    public void goToTutorial(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, Tutorial.class);
        startActivity(intent);
    }
}
