package com.seniordesign.autoresponder.Interface.Settings;

/**
 * Created by Garlan on 11/15/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.seniordesign.autoresponder.DataStructures.Group;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;
import com.seniordesign.autoresponder.Persistance.PermDBInstance;
import com.seniordesign.autoresponder.R;


public class SettingListAdapter extends ArrayAdapter<String> {
    private final String TAG = "SettingListAdapter";
    private final Context context;
    private final String[] settingList;
    private final DBInstance db;

    public SettingListAdapter(Context context, String[] settingList) {
        super(context, R.layout.setting_row, settingList);

        this.context = context;
        this.settingList = settingList;
        this.db = DBProvider.getInstance(false, context);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.setting_row, null, true);

        TextView title = (TextView) rowView.findViewById(R.id.title);
        TextView description = (TextView) rowView.findViewById(R.id.description);
        Switch toggle = (Switch) rowView.findViewById(R.id.toggle);

        switch (settingList[position]){
            case "Default Contact Location Setting": //default group location toggle
                title.setText(R.string.defaultGroup_location_toggle);

                description.setText(R.string.defaultGroup_location_toggle_descr);

                toggle.setChecked(db.getGroupInfo(Group.DEFAULT_GROUP).isLocationPermission());
                toggle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        db.setGroupLocationPermission(Group.DEFAULT_GROUP, !db.getGroupInfo(Group.DEFAULT_GROUP).isLocationPermission());
                    }
                });
                break;
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
                break;
            case "Time Delay": //default group time delay
                toggle.setVisibility(View.GONE);
                title.setText(R.string.setting_timeDelay);
                description.setVisibility(View.GONE);
                break;
            case "Default Contact Response":
                toggle.setVisibility(View.GONE);
                title.setText(R.string.defaultGroup_Response);
                description.setVisibility(View.GONE);
                break;
            default:
                Log.e(TAG, "this setting has not been configured!!!!");
        }

        return rowView;

    }
}