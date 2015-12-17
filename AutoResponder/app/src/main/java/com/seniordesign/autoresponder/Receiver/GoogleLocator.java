package com.seniordesign.autoresponder.Receiver;

//code based off of code provided by Ravi Tamada at http://www.androidhive.info/2015/02/android-location-api-using-google-play-services/

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by gabowser on 12/7/15.
 */
public class GoogleLocator implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    public static final String TAG = "GoogleLocator";
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    //private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private Location mCurrentLocation;
    private Address mCurrentAddress;
    private EventHandler mHandler;

    public GoogleLocator(Context context){
        this.mContext = context;

        if (checkPlayServices()){
            buildGoogleApiClient();
        }
        else{
            Log.e(TAG, "could not find Google Play Services");
        }
    }

    public GoogleLocator(Context context, EventHandler e){
        this(context);
        this.mHandler = e;
    }

    public Location getCurrentLocation(){
        if (mGoogleApiClient.isConnected())
            return this.mCurrentLocation;
        else{
            Log.e(TAG, "could not connect to Google Play Services ");
            return null;
        }
    }
    public Address getCurrentAddress(){
        if (mGoogleApiClient.isConnected())
            return this.mCurrentAddress;
        else{
            Log.e(TAG, "could not connect to Google Play Services ");
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
        Geocoder geocoder = new Geocoder(this.mContext, Locale.ENGLISH);
        List<Address> addressList;


        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mCurrentLocation != null){
            try{
                addressList = geocoder.getFromLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), 1);
                if (addressList != null) mCurrentAddress = addressList.get(0);
                Log.e(TAG, mCurrentLocation.getLatitude() + " " + mCurrentLocation.getLongitude() + " " + mCurrentAddress.toString());
            }
            catch (IOException e){
                Log.e(TAG, "error retrieving address for " + mCurrentLocation.getLatitude() + " " + mCurrentLocation.getLongitude());
                e.printStackTrace();
            }

        }
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
        if(mHandler != null){
            Log.d(TAG, "calling sendLocationInfo()");
            mHandler.sendLocationInfo();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

}
