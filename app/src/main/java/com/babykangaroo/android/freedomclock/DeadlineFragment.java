package com.babykangaroo.android.freedomclock;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.babykangaroo.android.mydatabaselibrary.ListContract;
import com.google.firebase.analytics.FirebaseAnalytics;


/**
 * This fragment manages the lists of upcoming deadlines
 */
public class DeadlineFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, MyCursorAdapter.ItemClickListener {
    /**
     * Context variables
     */
    private Context parentContext;
    private SharedPreferences sharedPreferences;
    private static final int LOADER_REQUEST = 9999;
    /**
     * view variables
     */
    private FloatingActionButton fabAddEvent;
    private MyCursorAdapter myCursorAdapter;
    private RecyclerView recyclerView;
    private TextView emptyView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //initialize parent context and associated preferences
        parentContext = getActivity();
        sharedPreferences = MainActivity.mainSharedPreferences;

        View rootView = inflater.inflate(R.layout.deadline_fragment, container, false);
        //set the values for the views
        emptyView = (TextView) rootView.findViewById(R.id.tv_rv_empty_view);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_upcoming);
        myCursorAdapter = new MyCursorAdapter(parentContext, this);
        recyclerView.setAdapter(myCursorAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(parentContext);
        recyclerView.setLayoutManager(layoutManager);
        getLoaderManager().initLoader(LOADER_REQUEST, null, this);

        // set up fab to add events
        fabAddEvent = (FloatingActionButton) rootView.findViewById(R.id.fab_add_deadline);
        fabAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeadlineDialog(null, null, null);
            }
        });
        return rootView;
    }


    /**
     * create a loader to query events table and show the most upcoming first
     *
     * @param i
     * @param bundle
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(parentContext, ListContract.ListContractEntry.ITEMS_CONTENT_URI,
                null,
                null,
                null,
                ListContract.ListContractEntry.COLUMN_ITEM_DATE + " DESC");
    }

    // load cursor to adapter and hide empty view
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.getCount() < 1) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.INVISIBLE);
        }
        myCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        myCursorAdapter.swapCursor(null);
    }

    /**
     * override cursor adapter interface, open alert dialog to edit, schedule or delete.
     *
     * @param clickedItemId cursor id of the selected item
     */
    @Override
    public void onClick(long clickedItemId) {
        String[] args = new String[1];
        args[0] = String.valueOf(clickedItemId);
        Cursor cursor = parentContext.getContentResolver().query(
                ListContract.ListContractEntry.ITEMS_CONTENT_URI,
                null,
                ListContract.ListContractEntry._ID + " = ? ",
                args,
                null);
        cursor.moveToFirst();
        showDeadlineDialog(cursor.getString(cursor.getColumnIndex(ListContract.ListContractEntry.COLUMN_ITEM_NAME)),
                cursor.getInt(cursor.getColumnIndex(ListContract.ListContractEntry.COLUMN_ITEM_DATE)), clickedItemId);
        cursor.close();
    }

    /**
     * shows alert dialog to enter edit schedule or delete  an item
     *
     * @param name
     * @param daysPrior
     * @param itemId
     */
    private void showDeadlineDialog(@Nullable final String name, @Nullable final Integer daysPrior, @Nullable final Long itemId) {
        final View adView = LayoutInflater.from(parentContext).inflate(R.layout.deadline_setter, null);
        final EditText etTask = (EditText) adView.findViewById(R.id.et_deadline_name);
        if (name != null) {
            etTask.setText(name);
        }
        final EditText etDaysPrior = (EditText) adView.findViewById(R.id.et_deadline_days_prior);
        if (daysPrior != null) {
            etDaysPrior.setText(String.valueOf(daysPrior));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(parentContext);
        builder.setView(adView);
        builder.setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                parentContext.getContentResolver().delete(ListContract.ListContractEntry.ITEMS_CONTENT_URI,
                        ListContract.ListContractEntry._ID + " = ? ",
                        new String[]{String.valueOf(itemId)});
            }
        });
        builder.setNegativeButton(R.string.schedule, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, getString(R.string.app_name));
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "calendar");
                MainActivity.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


                String event = etTask.getText().toString();
                String daysBefore = etDaysPrior.getText().toString();
                if (event.isEmpty() || daysBefore.isEmpty()) {
                    Toast.makeText(parentContext, getString(R.string.bad_entry), Toast.LENGTH_LONG).show();
                    return;
                }
                int daysBeforeInt = Integer.valueOf(daysBefore);

                boolean update = (name != null);
                ContentValues contentValues = new ContentValues();
                contentValues.put(ListContract.ListContractEntry.COLUMN_ITEM_NAME, event);
                contentValues.put(ListContract.ListContractEntry.COLUMN_ITEM_DATE, daysBefore);
                if (update) {
                    String[] args = new String[1];
                    args[0] = String.valueOf(itemId);
                    getActivity().getContentResolver().update(ListContract.ListContractEntry.ITEMS_CONTENT_URI,
                            contentValues,
                            ListContract.ListContractEntry._ID + " = ? ",
                            args);
                } else {
                    Uri uri = getActivity().getContentResolver().insert(ListContract.ListContractEntry.ITEMS_CONTENT_URI, contentValues);
                }

                long etsDate = sharedPreferences.getLong(getString(R.string.ets_date), System.currentTimeMillis());
                long scheduleDate = etsDate - (daysBeforeInt * DateUtils.DAY_IN_MILLIS);
                Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
                builder.appendPath("time");
                ContentUris.appendId(builder, scheduleDate);
                Intent intent = new Intent(Intent.ACTION_VIEW)
                        .setData(builder.build());
                startActivity(intent);
            }
        });
        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String event = etTask.getText().toString();
                String daysBefore = etDaysPrior.getText().toString();
                if (event.isEmpty() || daysBefore.isEmpty()) {
                    Toast.makeText(parentContext, getString(R.string.bad_entry), Toast.LENGTH_LONG).show();
                    return;
                }
                int daysBeforeInt = Integer.valueOf(daysBefore);

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, getString(R.string.app_name));
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "setDate");
                MainActivity.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                boolean update = (name != null);
                ContentValues contentValues = new ContentValues();
                contentValues.put(ListContract.ListContractEntry.COLUMN_ITEM_NAME, event);
                contentValues.put(ListContract.ListContractEntry.COLUMN_ITEM_DATE, daysBeforeInt);
                if (update) {
                    String[] args = new String[1];
                    args[0] = String.valueOf(itemId);
                    getActivity().getContentResolver().update(ListContract.ListContractEntry.ITEMS_CONTENT_URI,
                            contentValues,
                            ListContract.ListContractEntry._ID + " = ? ",
                            args);
                } else {
                    Uri uri = getActivity().getContentResolver().insert(ListContract.ListContractEntry.ITEMS_CONTENT_URI, contentValues);
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}

