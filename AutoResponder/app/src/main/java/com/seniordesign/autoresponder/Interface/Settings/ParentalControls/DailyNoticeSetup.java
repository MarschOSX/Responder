package com.seniordesign.autoresponder.Interface.Settings.ParentalControls;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;
import com.seniordesign.autoresponder.R;

import java.sql.Time;
import java.util.MissingResourceException;

public class DailyNoticeSetup extends AppCompatActivity {
    private TimePicker timePicker;
    private Button button;
    private DBInstance db;
    private TimePickerDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_notice_setup);

        this.timePicker = (TimePicker) findViewById(R.id.timePicker_dailyNotice);
        this.button = (Button) findViewById(R.id.button_setDailyNotice);

        this.db = DBProvider.getInstance(false, getApplicationContext());

        if(db != null){
            timePicker.setCurrentHour(db.getDailyNoticeTime_hour());
            timePicker.setCurrentMinute(db.getDailyNoticeTime_minute());

            this.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.setDailyNoticeTime(timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                    Toast.makeText(getApplicationContext(), "Daily Notice Time has been updated", Toast.LENGTH_SHORT).show();

                    finish();
                }
            });
        }
        else{
            throw new MissingResourceException("data base could not be accessed", "DBinstance", "database");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_daily_notice_setup, menu);
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
}
