package com.babykangaroo.android.freedomclock;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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
                        etsDate = new LocalDate(year,month,day);
                        tvEtsDateView.setText(etsDate.toString());
                    }
                });

                AlertDialog ad = builder.create();
                ad.show();
            }
        });
        return rootView;
    }
}
