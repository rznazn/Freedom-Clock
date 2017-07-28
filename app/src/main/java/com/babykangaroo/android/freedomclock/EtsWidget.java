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

        switch (branch){
            case "Army":
                mViews.setImageViewResource(R.id.iv_branch_insignia, R.drawable.u_s__department_of_the_army_da_seal1_5);
                break;
            case "Marines":
                mViews.setImageViewResource(R.id.iv_branch_insignia, R.drawable.emblem_marines);
                break;
            case "Navy":
                mViews.setImageViewResource(R.id.iv_branch_insignia, R.drawable.navyemblem);
                break;
            case "Air Force":
                mViews.setImageViewResource(R.id.iv_branch_insignia, R.drawable.department_of_the_air_force_57f5d_250x250);
                break;
            default:
                mViews.setImageViewResource(R.id.iv_branch_insignia, R.drawable.u_s__department_of_the_army_da_seal1_5);
                break;
        }

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        mViews.setOnClickPendingIntent(R.id.fl_ets_widget, pendingIntent);
        mViews.setTextViewText(R.id.tv_time_until, String.valueOf(daysLeft));

        appWidgetManager.updateAppWidget(appWidgetId, mViews);
    }

}