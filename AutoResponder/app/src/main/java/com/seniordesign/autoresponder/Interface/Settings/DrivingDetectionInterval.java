package com.seniordesign.autoresponder.Interface.Settings;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;
import com.seniordesign.autoresponder.R;

public class DrivingDetectionInterval extends AppCompatActivity {

    private static final String TAG = "DrivingDetectionInt";
    private DBInstance db;

    private RadioButton quarterMinRB;
    private RadioButton halfMinRB;
    private RadioButton oneMinRB;
    private RadioButton twoMinRB;
    private RadioButton threeMinRB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving_detection_interval);

        this.db = DBProvider.getInstance(false, getApplicationContext());

        this.quarterMinRB = (RadioButton) findViewById(R.id.radioButton_1);
        this.halfMinRB = (RadioButton) findViewById(R.id.radioButton_2);
        this.oneMinRB = (RadioButton) findViewById(R.id.radioButton_3);
        this.twoMinRB = (RadioButton) findViewById(R.id.radioButton_4);
        this.threeMinRB = (RadioButton) findViewById(R.id.radioButton_5);

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
        Float interval = db.getDrivingDetectionInterval();

        if(interval == 0.25f){
            quarterMinRB.setChecked(true);
        }else if(interval == 0.5f){
            halfMinRB.setChecked(true);
        }else if(interval == 1.0f){
            oneMinRB.setChecked(true);
        }else if(interval == 2.0f){
            twoMinRB.setChecked(true);
        }else if(interval == 3.0f){
            threeMinRB.setChecked(true);
        }
        else if(interval < 0){
            db.setDrivingDetectionInterval(1.0f);
            oneMinRB.setChecked(true);
        }
        else{
            db.setDrivingDetectionInterval(5);
            threeMinRB.setChecked(true);
        }

    }

    public void radioButtonTimeLimitSet(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        float interval = 1.0f;
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButton_1:
                if (checked)
                    interval = 0.25f;
                break;
            case R.id.radioButton_2:
                if (checked)
                    interval = 0.5f;
                break;
            case R.id.radioButton_3:
                if (checked)
                    interval = 1.0f;
                break;
            case R.id.radioButton_4:
                if (checked)
                    interval = 2.0f;
                break;
            case R.id.radioButton_5:
                if (checked)
                    interval = 3.0f;
                break;
        }

        db.setDrivingDetectionInterval(interval);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(SettingListAdapter.ACTION_UPDATE_INTERVAL));
    }
}
