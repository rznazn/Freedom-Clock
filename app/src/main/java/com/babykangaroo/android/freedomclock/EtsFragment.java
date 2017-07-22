package com.babykangaroo.android.freedomclock;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EtsFragment extends Fragment {

    private TextView tvEtsDateView;
    private TextView tvDaysTillSeperation;
    private long etsDate;
    private ImageView setETSdate;
    private ImageView shareEts;
    private Context parentContext;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor spEditor;
    private CountDownTimer countDownTimer;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.ets_fragment, container, false);
        parentContext = getActivity();
        sharedPreferences = MainActivity.mainSharedPreferences;

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

                Bitmap bitmap = ConvertToBitmap(rootView);String pathofBmp =
                        MediaStore.Images.Media.insertImage(parentContext.getContentResolver(), bitmap,"title", null);
                Uri bmpUri = Uri.parse(pathofBmp);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                intent.setType("image/png");
                startActivity(intent);
            }
        });
        if (sharedPreferences.contains(getString(R.string.ets_date))){
            setViews();

        } else {
            showDatePickerAd();
        }
        return rootView;
    }

    private void showDatePickerAd() {
        final View adView = getActivity().getLayoutInflater().inflate(R.layout.date_picker, null);
        final DatePicker datePicker = (DatePicker) adView.findViewById(R.id.dp_datePicker);
        if (sharedPreferences.contains(getString(R.string.ets_date))){
            long etsDate = sharedPreferences.getLong(getString(R.string.ets_date), 0);
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

                spEditor.apply();
                setViews();
            }
        });

        AlertDialog ad = builder.create();
        ad.show();

    }

    private void setViews(){
        if (countDownTimer != null){
            countDownTimer.cancel();
            countDownTimer = null;
        }
        etsDate = sharedPreferences.getLong(getString(R.string.ets_date), 0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd");
        String dateToDisplay = dateFormat.format(etsDate);
        tvEtsDateView.setText(dateToDisplay);
        tvEtsDateView.setTextSize(40);
        countDownTimer = new CountDownTimer((etsDate + (1000*60*60*24)) - System.currentTimeMillis(), 1000) {
            @Override
            public void onTick(long l) {
//                long daysUntil = timeUntil/(1000*60*60*24);
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
        countDownTimer.start();
    }
    protected Bitmap ConvertToBitmap(View layout) {
        Bitmap map;
        layout.setDrawingCacheEnabled(true);
        layout.buildDrawingCache();
        return map=layout.getDrawingCache();
    }
}
