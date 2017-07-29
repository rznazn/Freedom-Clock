package com.babykangaroo.android.freedomclock;

import android.app.AlertDialog;
import android.app.Fragment;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * this class manages the ets fragment that shows ets date and remaining time
 */
public class EtsFragment extends Fragment {
    /**
     * context variables
     */
    private Context parentContext;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor spEditor;
    private CountDownTimer countDownTimer;
    private long etsDate;
    /**
     * view variables
     */
    private TextView tvEtsDateView;
    private TextView tvDaysTillSeperation;
    private FrameLayout toDraw;
    private ImageView setETSdate;
    private ImageView shareEts;
    private ImageView takeSelfie;
    private ImageView helpDialog;


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.ets_fragment, container, false);
        parentContext = getActivity();
        sharedPreferences = MainActivity.mainSharedPreferences;



        toDraw = (FrameLayout) rootView.findViewById(R.id.fl_to_draw_actual);
        tvEtsDateView = (TextView) rootView.findViewById(R.id.tv_date_of_separation);
        tvDaysTillSeperation = (TextView) rootView.findViewById(R.id.tv_time_until);
        setETSdate = (ImageView) rootView.findViewById(R.id.iv_set_date);
        setETSdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerAd();

            }
        });

        shareEts = (ImageView) rootView.findViewById(R.id.iv_share);
        shareEts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, getString(R.string.app_name));
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "share");
                MainActivity.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                Bitmap bitmap = convertToBitmap(toDraw);String pathofBmp =
                        MediaStore.Images.Media.insertImage(parentContext.getContentResolver(), bitmap,"title", null);
                Uri bmpUri = Uri.parse(pathofBmp);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                intent.setType("image/png");
                startActivity(intent);
            }
        });

        takeSelfie = (ImageView) rootView.findViewById(R.id.iv_photo);
        takeSelfie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(parentContext, SelfieActivity.class));
            }
        });

        helpDialog = (ImageView) rootView.findViewById(R.id.iv_help);
        helpDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View adView = getActivity().getLayoutInflater().inflate(R.layout.help_dialog, null);
                ScrollView innerView = (ScrollView) adView.findViewById(R.id.ll_help_container);
                    innerView.getLayoutParams().height = MainActivity.dpHeight/2;

                final TextView messageTv =(TextView) adView.findViewById(R.id.tv_help_message);
                messageTv.setText(R.string.help_message);
                AlertDialog.Builder builder = new AlertDialog.Builder(parentContext);
                builder.setView(adView);
                builder.setMessage(R.string.from_the_developer);
                builder.setPositiveButton(getString(R.string.dismiss), null);

                AlertDialog ad = builder.create();
                ad.show();
            }
        });
        /**
         * if ets date pref is null (fresh install) show date picker
         */
        if (sharedPreferences.contains(getString(R.string.ets_date))){
            setViews();

        } else {
            showDatePickerAd();
        }
        return rootView;
    }

    /**
     * shows date picker to select ets date and branch
     */
    private void showDatePickerAd() {
        final View adView = getActivity().getLayoutInflater().inflate(R.layout.date_picker, null);
        ScrollView innerView = (ScrollView) adView.findViewById(R.id.ll_date_picker);
        final DatePicker datePicker = (DatePicker) adView.findViewById(R.id.dp_datePicker);
            innerView.getLayoutParams().height = MainActivity.dpHeight/2;

        final Spinner branchPicker = (Spinner) adView.findViewById(R.id.sp_branch_selector);
        if (sharedPreferences.contains(getString(R.string.ets_date))){
            long etsDate = sharedPreferences.getLong(getString(R.string.ets_date), 0);
            String branch = sharedPreferences.getString(getString(R.string.branch_pref), getString(R.string.Army));
            int branchId = 0;
            switch (branch){
                case "Army":
                    branchId=0;
                    break;
                case "Marines":
                    branchId = 1;
                    break;
                case "Navy":
                    branchId = 2;
                    break;
                case "Air Force":
                    branchId = 3;
                    break;
            }
            branchPicker.setSelection(branchId);
            Date date = new Date(etsDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            datePicker.updateDate(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(parentContext);
        builder.setView(adView);
        builder.setMessage(R.string.set_ets_date);
        builder.setNegativeButton(getString(R.string.cancel),null);
        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int day = datePicker.getDayOfMonth();
                String branch = String.valueOf(branchPicker.getSelectedItem());
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_MONTH, day);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.HOUR_OF_DAY,0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND,0);
                cal.set(Calendar.MILLISECOND,0);
                etsDate = cal.getTimeInMillis();
                spEditor = sharedPreferences.edit();
                spEditor.putLong(getString(R.string.ets_date), etsDate);
                spEditor.putString(getString(R.string.branch_pref), branch);

                spEditor.apply();
                setViews();
            }
        });

        AlertDialog ad = builder.create();
        ad.show();

    }

    /**
     * set views to display information based on the preferences
     */
    private void setViews(){
        if (countDownTimer != null){
            countDownTimer.cancel();
            countDownTimer = null;
        }
        etsDate = sharedPreferences.getLong(getString(R.string.ets_date), 0);
        DateFormat dateFormat = DateFormat.getDateInstance();
        String dateToDisplay = dateFormat.format(etsDate);
        tvEtsDateView.setText(dateToDisplay);
        tvEtsDateView.setTextSize(40);
        countDownTimer = new CountDownTimer((etsDate + (1000*60*60*24)) - System.currentTimeMillis(), 1000) {
            @Override
            public void onTick(long l) {
                long seconds = l / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                long days = hours / 24;
                String time = days + ":" + hours % 24 + ":" + minutes % 60 + ":" + seconds % 60;
                tvDaysTillSeperation.setText(String.valueOf(time));
            }

            @Override
            public void onFinish() {

            }
        };
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(parentContext);
        ComponentName widgetComponent = new ComponentName(parentContext, EtsWidget.class);
        int[] widgetIds = widgetManager.getAppWidgetIds(widgetComponent);
        Intent intent = new Intent();
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        parentContext.sendBroadcast(intent);
        countDownTimer.start();
    }

    /**
     * @param layout is converted to bitmap for share intent
     * @return
     */
    protected Bitmap convertToBitmap(View layout) {
        Bitmap map = Bitmap.createBitmap(layout.getMeasuredWidth(),
                layout.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        layout.setDrawingCacheEnabled(true);
        layout.buildDrawingCache();
        Canvas canvas = new Canvas(map);
        layout.draw(canvas);
        return map;
    }
}
