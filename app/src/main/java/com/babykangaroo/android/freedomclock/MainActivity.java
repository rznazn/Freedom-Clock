package com.babykangaroo.android.freedomclock;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.text.SimpleDateFormat;

/**
 * activity that houses and shows the ets and deadline fragments
 */
public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    /**
     * context variables
     */
    private FragmentManager fragmentManager;
    public Context mainContext;
    public static SharedPreferences mainSharedPreferences;
    public static FirebaseAnalytics mFirebaseAnalytics;

    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mainContext = this;
        mainSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mainSharedPreferences.registerOnSharedPreferenceChangeListener(this);

        setBranchTheme();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        mAdView = (AdView) findViewById(R.id.av_ads);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        fragmentManager = getFragmentManager();

        //set fragments
        EtsFragment etsFragment = new EtsFragment();
        DeadlineFragment deadlineFragment = new DeadlineFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.fl_fragment_container1, etsFragment, "etsFragment")
                .replace(R.id.fl_fragment_container2, deadlineFragment, "deadlineFragment")
                .commit();

        /**
         * check android version for compatability for notifications
         */
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
            scheduleNotifications();
        }
    }

    private void scheduleNotifications(){

        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job notification = dispatcher.newJobBuilder()
                .setService(EtsNotifications.class)
                .setTag("Notification")
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow((60*60*36), (60*60*36)+120))
                .setReplaceCurrent(true)
                .build();
        dispatcher.mustSchedule(notification);

    }

    void setBranchTheme(){
        String branch = mainSharedPreferences.getString(getString(R.string.branch_pref), getString(R.string.Army));
        switch (branch){
            case "Army":
                setTheme(R.style.ArmyTheme);
                break;
            case "Marines":
                setTheme(R.style.MarineTheme);
                break;
            case "Navy":
                setTheme(R.style.NavyTheme);
                break;
            case "Air Force":
                setTheme(R.style.AirForceTheme);
                break;
            default:
                setTheme(R.style.ArmyTheme);
                break;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s == getString(R.string.branch_pref)) {
            this.recreate();
        }
    }
}
