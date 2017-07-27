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
import android.util.Log;

import br.com.goncalves.pugnotification.interfaces.ImageLoader;
import br.com.goncalves.pugnotification.interfaces.OnImageLoadingCompleted;
import br.com.goncalves.pugnotification.notification.PugNotification;

/**
 * Created by Gene Denney on 7/23/2017.
 */

/**
 * class handles notifications
 * tagged for min android version
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class EtsNotifications extends com.firebase.jobdispatcher.JobService {

    private int inginiaId;
    private Context context;

    @Override
    public boolean onStartJob(com.firebase.jobdispatcher.JobParameters job) {
        context = getApplicationContext();

        /**
         * load preferences and parse
         */
        long etsDate = PreferenceManager.getDefaultSharedPreferences(this).getLong(getString(R.string.ets_date), 0);
        long now = System.currentTimeMillis();
        long timeTillSep = etsDate - now;
        long daysLeft = timeTillSep/(1000*60*60*24);
        int daysLeftInt= ((int) daysLeft) +1;

        String branch = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.branch_pref), getString(R.string.Army));
        switch (branch){
            case "Army":
                inginiaId = context.getResources().getIdentifier("u_s__department_of_the_army_da_seal1_5", "drawable", context.getPackageName());
                break;
            case "Marines":
                inginiaId = context.getResources().getIdentifier("emblem_marines", "drawable", context.getPackageName());
                break;
            case "Navy":
                inginiaId = context.getResources().getIdentifier("navyemblem", "drawable", context.getPackageName());
                break;
            case "Air Force":
                inginiaId = context.getResources().getIdentifier("department_of_the_air_force_57f5d_250x250", "drawable", context.getPackageName());
                break;
            default:
                inginiaId = context.getResources().getIdentifier("u_s__department_of_the_army_da_seal1_5", "drawable", context.getPackageName());
                break;
        }

        ImageLoader imageLoader = new ImageLoader() {
            @Override
            public void load(String uri, OnImageLoadingCompleted onCompleted) {

            }

            @Override
            public void load(int imageResId, OnImageLoadingCompleted onCompleted) {
                Bitmap map = BitmapFactory.decodeResource(getResources(), imageResId);
                onCompleted.imageLoadingCompleted(map);
            }
        };

        PugNotification.with(getApplicationContext())
                .load()
                .title(getString(R.string.app_name))
                .message(String.valueOf(daysLeftInt) + " days left till FREEDOM!!!")
                .smallIcon(R.drawable.small_icon)
                .largeIcon(R.drawable.small_icon)
                .flags(Notification.DEFAULT_ALL)
                .click(MainActivity.class)
                .autoCancel(true)
                .custom()
                .background(inginiaId)
                .setImageLoader(imageLoader)
                .build();
        return false;
    }

    @Override
    public boolean onStopJob(com.firebase.jobdispatcher.JobParameters job) {
        return false;
    }
}
