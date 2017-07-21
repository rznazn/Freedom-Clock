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
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.LocalDate;

public class EtsFragment extends Fragment {

    private TextView tvEtsDateView;
    private TextView tvDaysTillSeperation;
    private LocalDate etsDate;
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
                        spEditor = sharedPreferences.edit();
                        spEditor.putInt(getString(R.string.year_pref), year);
                        spEditor.putInt(getString(R.string.month_pref), month);
                        spEditor.putInt(getString(R.string.day_pref), day);
                        etsDate = new LocalDate(year,month,day);
                        spEditor.apply();
                        setViews();
                    }
                });

                AlertDialog ad = builder.create();
                ad.show();
            }
        });sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        if (sharedPreferences.contains(getString(R.string.year_pref))
                && sharedPreferences.contains(getString(R.string.month_pref))
                && sharedPreferences.contains(getString(R.string.day_pref))){
            setViews();

        }
        return rootView;
    }

    private void setViews(){
        etsDate = new LocalDate(sharedPreferences.getInt(getString(R.string.year_pref), 1970),
            sharedPreferences.getInt(getString(R.string.month_pref), 1),
            sharedPreferences.getInt(getString(R.string.day_pref), 1));
        tvEtsDateView.setText(etsDate.toString());


    }
}
