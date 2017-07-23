package com.babykangaroo.android.freedomclock;

import android.app.Notification;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import br.com.goncalves.pugnotification.notification.PugNotification;

/**
 * Created by Gene Denney on 7/23/2017.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class EtsNotifications extends com.firebase.jobdispatcher.JobService {

    @Override
    public boolean onStartJob(com.firebase.jobdispatcher.JobParameters job) {
        long etsDate = MainActivity.mainSharedPreferences.getLong(getString(R.string.ets_date), 0);
        long now = System.currentTimeMillis();
        long timeTillSep = etsDate - now;
        long daysLeft = timeTillSep/(1000*60*60*24);
        int daysLeftInt= (int) daysLeft;
        PugNotification.with(getApplicationContext())
                .load()
                .title(getString(R.string.app_name))
                .message(String.valueOf(daysLeftInt) + " days left till FREEDOM!!!")
                .smallIcon(R.drawable.pugnotification_ic_launcher)
                .largeIcon(R.drawable.pugnotification_ic_launcher)
                .flags(Notification.DEFAULT_ALL)
                .simple()
                .build();
        return false;
    }

    @Override
    public boolean onStopJob(com.firebase.jobdispatcher.JobParameters job) {
        return false;
    }
}
