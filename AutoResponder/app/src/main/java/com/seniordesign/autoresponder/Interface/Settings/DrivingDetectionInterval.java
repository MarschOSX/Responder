package com.seniordesign.autoresponder.Interface.Settings;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
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

public class DrivingDetectionInterval extends AppCompatActivity {

    private static final String TAG = "DrivingDetectionInt";
    private DBInstance db;

    private RadioButton oneMinRB;
    private RadioButton twoMinRB;
    private RadioButton threeMinRB;
    private RadioButton fourMinRB;
    private RadioButton fiveMinRB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving_detection_interval);

        this.db = DBProvider.getInstance(false, getApplicationContext());

        this.oneMinRB = (RadioButton) findViewById(R.id.radioButton_1);
        this.twoMinRB = (RadioButton) findViewById(R.id.radioButton_2);
        this.threeMinRB = (RadioButton) findViewById(R.id.radioButton_3);
        this.fourMinRB = (RadioButton) findViewById(R.id.radioButton_4);
        this.fiveMinRB = (RadioButton) findViewById(R.id.radioButton_5);

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
        Integer interval = db.getDrivingDetectionInterval();

        if(interval == 1){
            oneMinRB.setChecked(true);
        }else if(interval == 2){
            twoMinRB.setChecked(true);
        }else if(interval == 3){
            threeMinRB.setChecked(true);
        }else if(interval == 4){
            fourMinRB.setChecked(true);
        }else if(interval == 5){
            fiveMinRB.setChecked(true);
        }
        else if(interval < 0){
            db.setDrivingDetectionInterval(1);
            oneMinRB.setChecked(true);
        }
        else{
            db.setDrivingDetectionInterval(5);
            fiveMinRB.setChecked(true);
        }

    }

    public void radioButtonTimeLimitSet(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        int interval = 1;
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButton_1:
                if (checked)
                    interval = 1;
                break;
            case R.id.radioButton_2:
                if (checked)
                    interval = 2;
                break;
            case R.id.radioButton_3:
                if (checked)
                    interval = 3;
                break;
            case R.id.radioButton_4:
                if (checked)
                    interval = 4;
                break;
            case R.id.radioButton_5:
                if (checked)
                    if (checked)
                        interval = 5;
                break;
        }

        db.setDrivingDetectionInterval(interval);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(SettingListAdapter.ACTION_UPDATE_INTERVAL));
    }
}
