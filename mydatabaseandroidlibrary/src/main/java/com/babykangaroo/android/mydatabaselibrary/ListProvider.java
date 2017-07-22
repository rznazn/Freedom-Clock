package com.babykangaroo.android.mydatabaselibrary;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;


/**
 * Created by sport on 5/31/2017.
 */

public class ListProvider extends ContentProvider {

    /**
     * DataBase Helper object for Content Provider class
     */
    private ListDBHelper mDBHelper;

    /**
     * UriMatcher to match to directory list, items list, table, or item
     * @return
     */
    public static final int ITEMS = 200;
    public static final int ITEMS_WITH_ID = 201;

    /** a custom URIMatcher to ensure proper labeling and handleing of provider requests */
    private static final UriMatcher mUriMatcher = buildUriMatcher();
    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


        uriMatcher.addURI(ListContract.CONTENT_AUTHORITY, ListContract.ITEMS_PATH_NAME, ITEMS);
        uriMatcher.addURI(ListContract.CONTENT_AUTHORITY, ListContract.ITEMS_PATH_NAME + "#", ITEMS_WITH_ID);

        return uriMatcher;
    }

    /** create a database helper to handle database interaction */
    @Override
    public boolean onCreate() {
        mDBHelper = new ListDBHelper(getContext());
        return true;
    }

    /**
     * Database CRUD methods. All use switches to direct to the correct table
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int match = mUriMatcher.match(uri);
        Cursor cursor;
        switch (match) {
            case ITEMS:
                cursor = db.query(ListContract.ListContractEntry.ITEMS_TABLE_NAME,
                        null,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(),
                        ListContract.ListContractEntry.ITEMS_CONTENT_URI);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return cursor;


    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();

        int match = mUriMatcher.match(uri);
        Uri returnUri = null;
        long id;
        switch (match) {
            case ITEMS:
                id = db.insert(ListContract.ListContractEntry.ITEMS_TABLE_NAME,null, values);
                if (id > 0){
                    returnUri = ContentUris.withAppendedId(ListContract.ListContractEntry.ITEMS_CONTENT_URI, id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int match = mUriMatcher.match(uri);
        int rowsDeleted = -1;
        switch (match) {
            case ITEMS:
                rowsDeleted = db.delete(ListContract.ListContractEntry.ITEMS_TABLE_NAME,
                        selection,
                        selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                break;
        }
        return rowsDeleted;
    }

    /** update does not use a switch because it is only implemented for the items table*/
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int rowsUpdated = db.update(ListContract.ListContractEntry.ITEMS_TABLE_NAME,
                values,
                selection,
                selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
