package com.babykangaroo.android.freedomclock;

import android.app.FragmentManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    public Context mainContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainContext = this;

        fragmentManager = getFragmentManager();

        EtsFragment etsFragment = new EtsFragment();
        DeadlineFragment deadlineFragment = new DeadlineFragment();
        fragmentManager.beginTransaction()
                .add(R.id.fl_fragment_container1, etsFragment, "etsFragment")
                .add(R.id.fl_fragment_container2, deadlineFragment, "deadlineFragment")
                .commit();

    }
}
