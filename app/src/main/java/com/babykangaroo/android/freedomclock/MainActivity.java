package com.babykangaroo.android.freedomclock;

import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getFragmentManager();

        EtsFragment etsFragment = new EtsFragment();
        DeadlineFragment deadlineFragment = new DeadlineFragment();
        fragmentManager.beginTransaction()
                .add(R.id.fl_fragment_container1, etsFragment, "etsFragment")
                .add(R.id.fl_fragment_container2, deadlineFragment, "deadlineFragment")
                .commit();

    }
}
