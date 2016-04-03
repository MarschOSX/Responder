package com.seniordesign.autoresponder.Services;



import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;

import com.seniordesign.autoresponder.DataStructures.ResponseLog;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Receiver.SMSSender;

/**
 * Created by MarschOSX on 3/30/2016.
 *http://stackoverflow.com/questions/9520277/practical-way-to-find-out-if-sms-has-been-sent
 *http://stackoverflow.com/questions/7204035/how-to-access-sms-and-contacts-data
 *http://stackoverflow.com/questions/9118496/sms-contentobserver-onchange-fires-multiple-times
 *
 */
public class ParentalControlsWatcher extends Service {
    public static final String TAG = "ParentalControlsWatcher";
    private DBInstance db;
    public SMSObserver observer;


    @Override
    public int onStartCommand(Intent intent, int flags, int startID){
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        // REGISTER ContentObserver
        Log.e(TAG, "Creating SMSObserver!");
        observer = new SMSObserver(new Handler());
        this.getContentResolver().
                registerContentObserver(Uri.parse("content://sms/"), true, observer);
    }

    @Override
    public void onDestroy(){
        this.getContentResolver().unregisterContentObserver(observer);
        Log.d(TAG, "is dead");
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


    public class SMSObserver extends ContentObserver {
        private Handler m_handler;
        private Context mContext;
        Long last_id = 0l;


        public SMSObserver(Handler handler) {
            super(handler);
            m_handler = handler;
            mContext = getApplicationContext();
            Log.e(TAG, "SMSObserver Created");
        }



        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            Log.d(TAG, "onChange detected!");
            super.onChange(selfChange);
            Uri uriSMSURI = Uri.parse("content://sms");

            Cursor cursor = mContext.getContentResolver().query(uriSMSURI, null, null,
                    null, null);
            cursor.moveToNext();

            //preventing multiple runs
            if(last_id == cursor.getLong(cursor.getColumnIndex("_id"))){
                return;//cancel since its the same
            }
            last_id = cursor.getLong(cursor.getColumnIndex("_id"));
            String protocol = cursor.getString(cursor.getColumnIndex("protocol"));

            if (protocol == null) {
                //the message is sent out just now
                Log.e(TAG, "SMS Sent Detected!!");
                try{
                    cursor.moveToFirst();
                    String[] columns = new String[] { "address", "date", "body"};
                        String address = cursor.getString(cursor.getColumnIndex(columns[0]));
                        String date = cursor.getString(cursor.getColumnIndex(columns[1]));
                        String msg = cursor.getString(cursor.getColumnIndex(columns[2]));
                        Log.d(TAG, "Address: "+address);
                        Log.d(TAG, "Date: "+date);
                        Log.d(TAG, "Message: "+msg);

                    //Todo resume here with notes on GitHub Issue #79
                    /*ResponseLog lastLog = db.getLastResponseByNum(db.getParentalControlsNumber());
                    android.util.Log.v(TAG, "last response log retrieved");
                    if(!lastLog.getMessageSent().matches(db.getParentalControlsNumber())){
                        android.util.Log.v(TAG, "Message WAS NOT SENT from AutoResponder!");
                        /*boolean isDriving = true;
                        //TODO get data from DB
                        if(isDriving){
                            android.util.Log.v(TAG, "A text was sent WHILE DRIVING! BAD DOGGY, alert parent");
                            SMSSender sender = new SMSSender(db);
                            sender.sendSMS("ALERT this user sent a text while driving", "",
                                    db.getParentalControlsNumber(), 0L, false, false);

                        }

                    }else{
                        android.util.Log.v(TAG, "Message WAS SENT from AutoResponder! No worries!");
                    }*/
                }
                catch (Exception e){
                    Log.e(TAG, "Exception trying to read outgoing sms");
                }
                finally{
                    cursor.close();
                }


            } else {
                //the message is received just now
                Log.e(TAG, "Message is recieved just now!");
            }
        }
    }

    public static boolean isRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (ParentalControlsWatcher.class.getName().equals(service.service.getClassName())) {
                Log.d(TAG, "ParentalControls service is already running!");
                return true;
            }
        }
        Log.d(TAG, "ParentalControls service not running (yet)!");
        return false;
    }
}





