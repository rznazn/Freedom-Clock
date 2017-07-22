package com.babykangaroo.android.freedomclock;

import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    public Context mainContext;
    public static SharedPreferences mainSharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainContext = this;
        mainSharedPreferences = getPreferences(MODE_PRIVATE);
        fragmentManager = getFragmentManager();

        EtsFragment etsFragment = new EtsFragment();
        DeadlineFragment deadlineFragment = new DeadlineFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.fl_fragment_container1, etsFragment, "etsFragment")
                .replace(R.id.fl_fragment_container2, deadlineFragment, "deadlineFragment")
                .commit();

    }
}
