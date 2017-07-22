package com.babykangaroo.android.freedomclock;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.babykangaroo.android.mydatabaselibrary.ListContract;

import java.text.SimpleDateFormat;

public class DeadlineFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int LOADER_REQUEST= 9999;
    private FloatingActionButton fabAddEvent;
    private Context parentContext;
    private MyCursorAdapter myCursorAdapter;
    private RecyclerView recyclerView;
    private SharedPreferences sharedPreferences;
    private TextView emptyView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        parentContext = getActivity();
        sharedPreferences = MainActivity.mainSharedPreferences;
        myCursorAdapter = new MyCursorAdapter(parentContext);

        View rootView = inflater.inflate(R.layout.deadline_fragment, container, false);

        emptyView = (TextView) rootView.findViewById(R.id.tv_rv_empty_view);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_upcoming);
        recyclerView.setAdapter(myCursorAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(parentContext);
        recyclerView.setLayoutManager(layoutManager);
        getLoaderManager().initLoader(LOADER_REQUEST,null,this);

        fabAddEvent = (FloatingActionButton) rootView.findViewById(R.id.fab_add_deadline);
        fabAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View adView = LayoutInflater.from(parentContext).inflate(R.layout.deadline_setter, null);
                final EditText etTask = (EditText) adView.findViewById(R.id.et_deadline_name);
                final EditText etDaysPrior = (EditText) adView.findViewById(R.id.et_deadline_days_prior);

                AlertDialog.Builder builder = new AlertDialog.Builder(parentContext);
                builder.setView(adView);
                builder.setNegativeButton(getString(R.string.cancel), null);
                builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String task = etTask.getText().toString();
                        int daysPrior = Integer.valueOf(etDaysPrior.getText().toString());
                        long etsDate = sharedPreferences.getLong(getString(R.string.ets_date), System.currentTimeMillis());
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(ListContract.ListContractEntry.COLUMN_ITEM_NAME, task);
                        contentValues.put(ListContract.ListContractEntry.COLUMN_ITEM_DATE, daysPrior);
                        Uri uri = getActivity().getContentResolver().insert(ListContract.ListContractEntry.ITEMS_CONTENT_URI, contentValues);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MMM-dd");
                        String dateOfEvent = simpleDateFormat.format(etsDate - ((1000*60*60*24)*daysPrior));
                        Toast.makeText(parentContext, dateOfEvent, Toast.LENGTH_LONG).show();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(parentContext, ListContract.ListContractEntry.ITEMS_CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.getCount() < 1) {
            emptyView.setVisibility(View.VISIBLE);
        }else{
            emptyView.setVisibility(View.INVISIBLE);
        }
        myCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        myCursorAdapter.swapCursor(null);
    }
}
