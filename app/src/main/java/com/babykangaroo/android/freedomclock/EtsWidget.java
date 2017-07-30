package com.babykangaroo.android.freedomclock;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.transition.Visibility;
import android.support.v7.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.RemoteViews;

/**
 * Created by Gene Denney on 7/23/2017.
 */

public class EtsWidget extends AppWidgetProvider{

    /**
     * context variables
     */
    public static AppWidgetManager mAppWidgetManager;
    public static int mAppWidgetId;

    private static RemoteViews mViews;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        mAppWidgetManager = appWidgetManager;
        mAppWidgetId = appWidgetIds[0];
        for (int appWidgetId : appWidgetIds) {
            updateEtsWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private static void updateEtsWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        long etsDate = sharedPreferences.getLong(context.getString(R.string.ets_date), 0);
        String branch = sharedPreferences.getString(context.getString(R.string.branch_pref), context.getString(R.string.Army));
        long daysLeft = ((etsDate-System.currentTimeMillis())/ DateUtils.DAY_IN_MILLIS) +1;

        mViews = new RemoteViews(context.getPackageName(), R.layout.ets_widget);

        if (daysLeft == 1){
            mViews.setTextViewText(R.id.tv_days_until, context.getString(R.string.day_until_freedom_widget));
        }else if (daysLeft <= 0){
            mViews.setTextViewText(R.id.tv_days_until, context.getString(R.string.done_son));
            mViews.setViewVisibility(R.id.tv_time_until, View.GONE);
        }

        switch (branch){
            case "Army":
                mViews.setImageViewResource(R.id.iv_branch_insignia, R.drawable.insignia_pending);
                break;
            case "Marines":
                mViews.setImageViewResource(R.id.iv_branch_insignia, R.drawable.insignia_pending);
                break;
            case "Navy":
                mViews.setImageViewResource(R.id.iv_branch_insignia, R.drawable.insignia_pending);
                break;
            case "Air Force":
                mViews.setImageViewResource(R.id.iv_branch_insignia, R.drawable.insignia_pending);
                break;
            default:
                mViews.setImageViewResource(R.id.iv_branch_insignia, R.drawable.insignia_pending);
                break;
        }

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        mViews.setOnClickPendingIntent(R.id.fl_ets_widget, pendingIntent);
        mViews.setTextViewText(R.id.tv_time_until, String.valueOf(daysLeft));

        appWidgetManager.updateAppWidget(appWidgetId, mViews);
    }

}