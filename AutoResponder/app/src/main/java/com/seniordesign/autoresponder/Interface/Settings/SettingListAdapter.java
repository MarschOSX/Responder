package com.seniordesign.autoresponder.Interface.Settings;

/**
 * Created by Garlan on 11/15/2015.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.seniordesign.autoresponder.Interface.LocationOutput;
import com.seniordesign.autoresponder.Interface.Settings.ParentalControls.ParentalControlsPassword;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;
import com.seniordesign.autoresponder.R;


public class SettingListAdapter extends ArrayAdapter<String> {
    private final String TAG = "SettingListAdapter";
    private final Context context;
    private final String[] settingList;
    private final DBInstance db;
    private AppCompatActivity parentApp;
    public static final String ACTION_UPDATE_INTERVAL = "ACTION_UPDATE_INTERVAL";

    public SettingListAdapter(Context context, String[] settingList, AppCompatActivity parentApp) {
        super(context, R.layout.setting_row, settingList);

        this.context = context;
        this.settingList = settingList;
        this.db = DBProvider.getInstance(false, context);
        this.parentApp = parentApp;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.setting_row, null, true);

        TextView title = (TextView) rowView.findViewById(R.id.title);
        TextView description = (TextView) rowView.findViewById(R.id.description);
        final Switch toggle = (Switch) rowView.findViewById(R.id.toggle);

        switch (settingList[position]){
            case "Time Limit": //default group time limit
                toggle.setVisibility(View.GONE);
                title.setText(R.string.setting_timeLimit);
                description.setText(R.string.setting_timeLimit_descr);

                rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(parentApp, TimeLimit.class);
                        parentApp.startActivity(intent);
                    }
                });
                break;

            case "Default Contact Location Setting": //default group location toggle
                title.setText(R.string.defaultGroup_location_toggle);
                title.setText(R.string.end_direction);

                description.setText(R.string.defaultGroup_location_toggle_descr);
                break;
            case "World Toggle": //default group location toggle
                title.setText("World Reply");
                description.setText("Responds to any number, not just contacts");

                toggle.setChecked(db.getWorldToggle());
                toggle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        db.setWorldToggle(toggle.isChecked());
                    }
                });
                break;
            /*
            case "Default Contact Activity Setting": //default group activity toggle
                title.setText(R.string.defaultGroup_activity_toggle);

                description.setText(R.string.defaultGroup_activity_toggle_descr);

                toggle.setChecked(db.getGroupInfo(Group.DEFAULT_GROUP).isActivityPermission());
                toggle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        db.setGroupActivityPermission(Group.DEFAULT_GROUP, !db.getGroupInfo(Group.DEFAULT_GROUP).isActivityPermission());
                    }
                });
                break;*/
            case "Time Delay": //default group time delay
                toggle.setVisibility(View.GONE);
                title.setText(R.string.setting_timeDelay);
                description.setText(R.string.setting_timeDelay_descr);

                rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(parentApp, TimeDelay.class);
                        parentApp.startActivity(intent);
                    }
                });
                break;
            case "Response Log": //default group time delay
                toggle.setVisibility(View.GONE);
                title.setText("Response Log");
                description.setText("History of what has been sent");


                rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(parentApp, ResponseLogList.class);
                        parentApp.startActivity(intent);
                    }
                });
                break;
            case "Default Contacts":
                toggle.setVisibility(View.GONE);
                title.setText("Default Contacts");
                description.setText("Contacts with no custom settings will use the Default");

                rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(parentApp, DefaultContactResponse.class);
                        parentApp.startActivity(intent);
                    }
                });
                break;
            case "Parental Controls":
                toggle.setVisibility(View.GONE);
                title.setText("Parental Controls");
                description.setText("SMS alerts to parents if user is texting while driving");

                rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(parentApp, ParentalControlsPassword.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("CHANGING_PASSWORD","false");
                        parentApp.startActivity(intent);
                    }
                });
                break;


            case "Developer Settings":
                toggle.setVisibility(View.GONE);
                title.setText("Developer Settings");
                description.setText("Advanced tools for development");

                rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(parentApp, LocationOutput.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        parentApp.startActivity(intent);
                    }
                });
                break;
            case "Driving Detection Interval":
                title.setText(R.string.drivingDetection_interval);
                description.setText(R.string.drivingDetection_interval_descr_short);
                toggle.setVisibility(View.GONE);

                rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(parentApp, DrivingDetectionInterval.class);
                        parentApp.startActivity(intent);
                    }
                });
                break;
                    default:
                            Log.e(TAG, "this setting (" + settingList[position] + ") has not been configured!!!!");
        }

        return rowView;

    }
}