package com.babykangaroo.android.freedomclock;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.babykangaroo.android.mydatabaselibrary.ListContract;

import java.text.SimpleDateFormat;

public class DeadlineFragment extends Fragment {

    private FloatingActionButton fabAddEvent;
    private Context parentContext;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        parentContext = getActivity();
        View rootView = inflater.inflate(R.layout.deadline_fragment, container, false);
        fabAddEvent = (FloatingActionButton) rootView.findViewById(R.id.fab_add_deadline);
        fabAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long daysPrior = 7;
                SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                long etsDate = sharedPreferences.getLong(getString(R.string.ets_date), System.currentTimeMillis());
                ContentValues contentValues = new ContentValues();
                contentValues.put(ListContract.ListContractEntry.COLUMN_ITEM_NAME, "deadline");
                contentValues.put(ListContract.ListContractEntry.COLUMN_ITEM_DATE, daysPrior);
                Uri uri = getActivity().getContentResolver().insert(ListContract.ListContractEntry.ITEMS_CONTENT_URI, contentValues);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MMM-dd");
                String dateOfEvent = simpleDateFormat.format(etsDate - ((1000*60*60*24)*daysPrior));
                Toast.makeText(parentContext, uri.toString(), Toast.LENGTH_LONG).show();
            }
        });
        return rootView;
    }
}
