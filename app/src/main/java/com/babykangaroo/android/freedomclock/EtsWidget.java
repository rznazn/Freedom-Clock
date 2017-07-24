package com.babykangaroo.android.freedomclock;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.widget.RemoteViews;

/**
 * Created by Gene Denney on 7/23/2017.
 */

public class EtsWidget extends AppWidgetProvider{

    public static Context mContext;
    public static AppWidgetManager mAppWidgetManager;
    public static int mAppWidgetId;


    private static RemoteViews mViews;
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        mContext = context;
        mAppWidgetManager = appWidgetManager;
        mAppWidgetId = appWidgetIds[0];
        for (int appWidgetId : appWidgetIds) {
            updateEtsWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private static void updateEtsWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        long etsDate = sharedPreferences.getLong(context.getString(R.string.ets_date), 0);
        long daysLeft = ((etsDate-System.currentTimeMillis())/ DateUtils.DAY_IN_MILLIS) +1;

        mViews = new RemoteViews(context.getPackageName(), R.layout.ets_widget);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        mViews.setOnClickPendingIntent(R.id.fl_ets_widget, pendingIntent);
        mViews.setTextViewText(R.id.tv_time_until, String.valueOf(daysLeft));

        appWidgetManager.updateAppWidget(appWidgetId, mViews);
    }

}