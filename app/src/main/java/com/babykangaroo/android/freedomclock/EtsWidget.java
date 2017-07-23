package com.babykangaroo.android.freedomclock;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.v7.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.TextView;

import java.text.SimpleDateFormat;

/**
 * Created by Gene Denney on 7/23/2017.
 */

public class EtsWidget extends AppWidgetProvider {



    private static RemoteViews mViews;
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        long etsDate = sharedPreferences.getLong(context.getString(R.string.ets_date), 0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd");
        String dateToDisplay = dateFormat.format(etsDate);
        mViews = new RemoteViews(context.getPackageName(), R.layout.ets_widget);

        mViews.setTextViewText(R.id.tv_date_of_separation, dateToDisplay);
        CountDown countDown = new CountDown((etsDate + (1000*60*60*24)) - System.currentTimeMillis(),1000, appWidgetManager,appWidgetId,mViews);
        countDown.start();

        appWidgetManager.updateAppWidget(appWidgetId, mViews);
    }
}

 class CountDown extends CountDownTimer {
    private RemoteViews mViews;
    private AppWidgetManager mAppWidgetManager;
    private int mAppWidgetId;

    public CountDown(long millisInFuture, int countDownInterval,
                     AppWidgetManager appWidgetManager, int appWidgetId, RemoteViews views) {
        super(millisInFuture, countDownInterval);
        mAppWidgetManager = appWidgetManager;
        mAppWidgetId = appWidgetId;
        mViews = views;
    }

    @Override
    public void onFinish() {
    }

    @Override
    public void onTick(long millisUntilFinished) {
        long seconds = millisUntilFinished / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        String time = days + ":" + hours % 24 + ":" + minutes % 60 + ":" + seconds % 60;
        mViews.setTextViewText(R.id.tv_time_until, time);
        mAppWidgetManager.updateAppWidget(mAppWidgetId, mViews);
    }
}