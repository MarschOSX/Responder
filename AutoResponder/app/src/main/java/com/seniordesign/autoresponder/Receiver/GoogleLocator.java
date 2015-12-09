package com.seniordesign.autoresponder.Receiver;

//code based off of code provided by Ravi Tamada at http://www.androidhive.info/2015/02/android-location-api-using-google-play-services/

import android.content.Context;
import android.location.GpsStatus;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.concurrent.TimeUnit;

/**
 * Created by gabowser on 12/7/15.
 */
public class GoogleLocator implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    public static final String TAG = "GoogleLocator";
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    //private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private Location mCurrentLocation;
    private EventListener mListener;

    public GoogleLocator(Context context){
        this.mContext = context;

        if (checkPlayServices()){
            buildGoogleApiClient();
        }
        else{
            Log.e(TAG, "could not find Google Play Services");
        }
    }

    public GoogleLocator(Context context, EventListener e){
        this(context);
        this.mListener = e;
    }

    public Location getCurrentLocation(){
        if (mGoogleApiClient.isConnected())
            return this.mCurrentLocation;
        else{
            Log.e(TAG, "could not connect to Google Play Services " + mGoogleApiClient.isConnecting());
            return null;
        }
    }

    public void close(){
        mGoogleApiClient.disconnect();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
        if (resultCode != ConnectionResult.SUCCESS) {
            return false;
        }
        else return true;
    }

    private void getLocation() {
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.e(TAG, mCurrentLocation.getLatitude() + " " + mCurrentLocation.getLongitude());
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {
        // Once connected with google api, get the location
        getLocation();
        if(mListener != null){
            Log.d(TAG, "calling cont");
            mListener.onReceive_cont();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

}
