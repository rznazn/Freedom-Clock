package com.babykangaroo.android.freedomclock;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Trigger;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    public Context mainContext;
    public static SharedPreferences mainSharedPreferences;
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 9998;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainContext = this;
        mainSharedPreferences = getPreferences(MODE_PRIVATE);
        fragmentManager = getFragmentManager();

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        EtsFragment etsFragment = new EtsFragment();
        DeadlineFragment deadlineFragment = new DeadlineFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.fl_fragment_container1, etsFragment, "etsFragment")
                .replace(R.id.fl_fragment_container2, deadlineFragment, "deadlineFragment")
                .commit();


        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));

        Job notificationJob = dispatcher.newJobBuilder()
                .setService(EtsNotifications.class)
                .setTag("notification")
                .setTrigger(Trigger.executionWindow(20, 30))
                .setReplaceCurrent(true)
                .build();
        dispatcher.mustSchedule(notificationJob);
    }
}
