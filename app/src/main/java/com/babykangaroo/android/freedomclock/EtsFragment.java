package com.babykangaroo.android.freedomclock;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EtsFragment extends Fragment {

    private TextView tvEtsDateView;
    private TextView tvDaysTillSeperation;
    private long etsDate;
    private ImageView setETSdate;
    private Context parentContext;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor spEditor;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.ets_fragment, container, false);
        parentContext = getActivity();

        tvEtsDateView = (TextView) rootView.findViewById(R.id.tv_date_of_separation);
        tvDaysTillSeperation = (TextView) rootView.findViewById(R.id.tv_time_until);
        setETSdate = (ImageView) rootView.findViewById(R.id.iv_set_date);
        setETSdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View adView = getActivity().getLayoutInflater().inflate(R.layout.date_picker, null);
                final DatePicker datePicker = (DatePicker) adView.findViewById(R.id.dp_datePicker);
                AlertDialog.Builder builder = new AlertDialog.Builder(parentContext);
                builder.setView(adView);
                builder.setMessage(R.string.set_ets_date);
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
        });sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        if (sharedPreferences.contains(getString(R.string.ets_date))){
            setViews();

        }
        return rootView;
    }

    private void setViews(){
        etsDate = sharedPreferences.getLong(getString(R.string.ets_date), 0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd");
        String dateToDisplay = dateFormat.format(etsDate);
        tvEtsDateView.setText(dateToDisplay);
        tvEtsDateView.setTextSize(40);
        long timeUntil = (etsDate + (1000*60*60*24)) - System.currentTimeMillis();
        long daysUntil = timeUntil/(1000*60*60*24);
        tvDaysTillSeperation.setText(String.valueOf(daysUntil));
    }
}
