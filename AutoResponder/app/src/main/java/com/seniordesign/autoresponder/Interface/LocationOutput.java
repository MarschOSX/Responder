package com.seniordesign.autoresponder.Interface;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.seniordesign.autoresponder.DataStructures.Contact;
import com.seniordesign.autoresponder.R;
import com.seniordesign.autoresponder.Services.DrivingDetection;

public class LocationOutput extends Activity {
    private String TAG = "LocationOutput";
    private TextView latitudeTextView;
    private TextView longitudeTextView;
    private TextView speedTextView;
    private Switch serviceSwitch;
    private Switch serviceBindSwitch;
    private Button serviceStatusButton;
    private Context context;
    private boolean isBound = false;
    private DrivingDetection mService;

    private float latitude;
    private float longitude;
    private float speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_output);

        this.context = getApplicationContext();
        this.latitudeTextView = (TextView)findViewById(R.id.latitude_text);
        this.longitudeTextView = (TextView)findViewById(R.id.longitude_text);
        this.speedTextView = (TextView)findViewById(R.id.speed_text);
        this.serviceSwitch = (Switch)findViewById(R.id.service_switch);
        this.serviceBindSwitch = (Switch)findViewById(R.id.bind_switch);
        this.serviceStatusButton = (Button)findViewById(R.id.service_status_button);

        this.latitudeTextView.setText("0");
        this.longitudeTextView.setText("0");
        this.speedTextView.setText("0");

        if (isMyServiceRunning(DrivingDetection.class)) serviceSwitch.setChecked(true);

        this.serviceSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (serviceSwitch.isChecked()) {
                    //context.startService(new Intent(context, DrivingDetection.class));

                    if (!isMyServiceRunning(DrivingDetection.class)) {
                        Thread serviceThread = new Thread() {
                            public void run() {
                                context.startService(new Intent(context, DrivingDetection.class));
                            }
                        };

                        serviceThread.start();
                    }

                } else {
                    if (isMyServiceRunning(DrivingDetection.class)) {
                        stopService(new Intent(context, DrivingDetection.class));
                    }
                }
            }
        });

        this.serviceBindSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, serviceBindSwitch.isChecked() + " + " + isBound);
                if (serviceBindSwitch.isChecked()) {
                    //context.startService(new Intent(context, DrivingDetection.class));

                    if (!isBound && isMyServiceRunning(DrivingDetection.class)) {
                        bindService(new Intent(context, DrivingDetection.class), mServiceConnection, Context.BIND_AUTO_CREATE);
                        Log.d(TAG, "service is bound");
                    } else {
                        serviceBindSwitch.setChecked(false);
                    }

                } else {

                    if (isBound && isMyServiceRunning(DrivingDetection.class)) {
                        unbindService(mServiceConnection);
                        isBound = false;
                        latitudeTextView.setText("0");
                        longitudeTextView.setText("0");
                        speedTextView.setText("0");
                        Log.d(TAG, "service is no longer bound");
                    }
                }
            }
        });

        serviceStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Driving Detection is running = " + isMyServiceRunning(DrivingDetection.class), Toast.LENGTH_SHORT).show();

                if (mService != null) {
                    Integer[] array = mService.testing();

                    latitudeTextView.setText(array[0].toString());
                    longitudeTextView.setText(array[1].toString());
                    speedTextView.setText(array[2].toString());
                } else {
                    Log.e(TAG, "serivce is null");

                }
            }
        });
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DrivingDetection.LocalBinder myBinder = (DrivingDetection.LocalBinder) service;
            mService = myBinder.getService();
            isBound = true;
        }
    };

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
