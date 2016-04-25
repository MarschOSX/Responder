package com.seniordesign.autoresponder.Interface.Settings;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.seniordesign.autoresponder.R;

public class MyFragmentCreator extends Fragment {

    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    public static final String EXTRA_DIRECTION = "EXTRA_DIRECTION";
    public static final MyFragmentCreator newInstance(String title, String message, String direction)
    {
        MyFragmentCreator f = new MyFragmentCreator();
        Bundle bdl = new Bundle();
        bdl.putString(EXTRA_TITLE, title);
        bdl.putString(EXTRA_MESSAGE, message);
        bdl.putString(EXTRA_DIRECTION, direction);
        f.setArguments(bdl);
        return f;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String title = getArguments().getString(EXTRA_TITLE);
        String message = getArguments().getString(EXTRA_MESSAGE);
        String direction = getArguments().getString(EXTRA_DIRECTION);
        View v = inflater.inflate(R.layout.activity_my_fragment_creator, container, false);
        TextView titleTextView = (TextView)v.findViewById(R.id.textView_Title);
        titleTextView.setText(title);
        TextView messageTextView = (TextView)v.findViewById(R.id.textView_Info);
        messageTextView.setText(message);
        TextView directionsTextView = (TextView)v.findViewById(R.id.textView_swipeInfo);
        directionsTextView.setText(direction);
        return v;
    }
}
