package com.babykangaroo.android.freedomclock;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;

import br.com.goncalves.pugnotification.interfaces.ImageLoader;
import br.com.goncalves.pugnotification.interfaces.OnImageLoadingCompleted;
import br.com.goncalves.pugnotification.notification.PugNotification;

/**
 * Created by Gene Denney on 7/23/2017.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class EtsNotifications extends com.firebase.jobdispatcher.JobService {

    @Override
    public boolean onStartJob(com.firebase.jobdispatcher.JobParameters job) {

        long etsDate = PreferenceManager.getDefaultSharedPreferences(this).getLong(getString(R.string.ets_date), 0);
        long now = System.currentTimeMillis();
        long timeTillSep = etsDate - now;
        long daysLeft = timeTillSep/(1000*60*60*24);
        int daysLeftInt= ((int) daysLeft) +1;

        ImageLoader imageLoader = new ImageLoader() {
            @Override
            public void load(String uri, OnImageLoadingCompleted onCompleted) {

            }

            @Override
            public void load(int imageResId, OnImageLoadingCompleted onCompleted) {
                Bitmap map = BitmapFactory.decodeResource(getResources(), R.drawable.u_s__department_of_the_army_da_seal1_5);
                onCompleted.imageLoadingCompleted(map);
            }
        };

        PugNotification.with(getApplicationContext())
                .load()
                .title(getString(R.string.app_name))
                .message(String.valueOf(daysLeftInt) + " days left till FREEDOM!!!")
                .smallIcon(R.drawable.pugnotification_ic_launcher)
                .largeIcon(R.drawable.pugnotification_ic_launcher)
                .flags(Notification.DEFAULT_ALL)
                .click(MainActivity.class)
                .autoCancel(true)
                .custom()
                .background(R.drawable.u_s__department_of_the_army_da_seal1_5)
                .setImageLoader(imageLoader)
                .build();
        return false;
    }

    @Override
    public boolean onStopJob(com.firebase.jobdispatcher.JobParameters job) {
        return false;
    }
}
