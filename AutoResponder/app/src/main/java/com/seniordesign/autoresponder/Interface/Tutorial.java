package com.seniordesign.autoresponder.Interface;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.seniordesign.autoresponder.Interface.Settings.MyFragmentCreator;
import com.seniordesign.autoresponder.Interface.Settings.PageAdapter;
import com.seniordesign.autoresponder.R;

import java.util.ArrayList;
import java.util.List;

public class Tutorial extends FragmentActivity {

    PageAdapter pageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        List<Fragment> fragments = getFragments();
        pageAdapter = new PageAdapter(getSupportFragmentManager(), fragments);
        ViewPager pager =
                (ViewPager)findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tutorial, menu);
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

    private List<Fragment> getFragments() {
        List<Fragment> fList = new ArrayList<>();
        fList.add(MyFragmentCreator.newInstance("AutoResponder: What is it?", this.getString(R.string.what_is_it), this.getString(R.string.start_direction)));
        fList.add(MyFragmentCreator.newInstance("Adding Contacts", this.getString(R.string.adding_contacts), this.getString(R.string.middle_direction)));
        fList.add(MyFragmentCreator.newInstance("Creating Groups", this.getString(R.string.creating_groups),this.getString(R.string.middle_direction)));
        fList.add(MyFragmentCreator.newInstance("Location and Activity Requests", this.getString(R.string.location_and_activity),this.getString(R.string.middle_direction)));
        fList.add(MyFragmentCreator.newInstance("Universal Reply", this.getString(R.string.universal_reply),this.getString(R.string.middle_direction)));
        fList.add(MyFragmentCreator.newInstance("World Reply", this.getString(R.string.world_reply),this.getString(R.string.middle_direction)));
        fList.add(MyFragmentCreator.newInstance("Time Limit VS Time Delay", this.getString(R.string.time_limit_and_delay),this.getString(R.string.middle_direction)));
        fList.add(MyFragmentCreator.newInstance("Driving Detection", this.getString(R.string.driving_detection),this.getString(R.string.middle_direction)));
        fList.add(MyFragmentCreator.newInstance("Parental Controls", this.getString(R.string.parental_controls),this.getString(R.string.middle_direction)));
        fList.add(MyFragmentCreator.newInstance("Response Log", this.getString(R.string.response_log),this.getString(R.string.middle_direction)));

        return fList;
    }
}
