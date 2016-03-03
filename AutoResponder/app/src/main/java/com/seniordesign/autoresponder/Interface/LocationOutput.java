package com.seniordesign.autoresponder.Interface;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.seniordesign.autoresponder.R;

public class LocationOutput extends Activity {
    private TextView latitudeTextView;
    private TextView longitudeTextView;
    private TextView speedTextView;
    private Switch serviceSwitch;

    private float latitude;
    private float longitude;
    private float speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_output);

        this.latitudeTextView = (TextView)findViewById(R.id.latitude_text);
        this.longitudeTextView = (TextView)findViewById(R.id.longitude_text);
        this.speedTextView = (TextView)findViewById(R.id.speed_text);
        this.serviceSwitch = (Switch)findViewById(R.id.service_switch);

        this.latitudeTextView.setText("0");
        this.longitudeTextView.setText("0");
        this.speedTextView.setText("0");

        this.serviceSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (serviceSwitch.isChecked()){
                    latitudeTextView.setText("1");
                    longitudeTextView.setText("1");
                    speedTextView.setText("1");
                }
                else{
                    latitudeTextView.setText("0");
                    longitudeTextView.setText("0");
                    speedTextView.setText("0");
                }
            }
        });
    }
}
