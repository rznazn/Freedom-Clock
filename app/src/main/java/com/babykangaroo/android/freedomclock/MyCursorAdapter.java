package com.babykangaroo.android.freedomclock;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.babykangaroo.android.mydatabaselibrary.ListContract;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static java.text.DateFormat.getDateInstance;

/**
 * Created by Gene Denney on 7/22/2017.
 */

/**
 * cursor adapter for deadline fragment
 */
public class MyCursorAdapter extends RecyclerView.Adapter<MyCursorAdapter.DeadlineViewHolder>
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Cursor mCursor;
    private Context mContext;
    private SharedPreferences sharedPreferences;
    private ItemClickListener clickListener;

    public MyCursorAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        clickListener = listener;
        sharedPreferences = MainActivity.mainSharedPreferences;
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }


    public interface ItemClickListener {
        void onClick(long clickedItemId);
    }

    @Override
    public DeadlineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.deadline_list_item, parent, false);
        return new DeadlineViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(DeadlineViewHolder holder, int position){
            long etsDate = sharedPreferences.getLong(mContext.getString(R.string.ets_date), 0);
            mCursor.moveToPosition(position);
            String event = mCursor.getString(
                    mCursor.getColumnIndex(ListContract.ListContractEntry.COLUMN_ITEM_NAME));
            holder.nameView.setText(event);
            long daysPrior = mCursor.getLong(mCursor.getColumnIndex(ListContract.ListContractEntry.COLUMN_ITEM_DATE));
            long dateOfevent = etsDate - ((1000 * 60 * 60 * 24) * daysPrior);
            DateFormat dateFormat = getDateInstance();
            String date = dateFormat.format(dateOfevent);
            holder.dateView.setText(date);
    }

    void swapCursor(Cursor cursor) {
        if (cursor != null) {
            mCursor = cursor;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        if (mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(mContext.getString(R.string.ets_date))){
            notifyDataSetChanged();
        }
    }

    class DeadlineViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView nameView;
        TextView dateView;

        DeadlineViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            nameView = (TextView) itemView.findViewById(R.id.tv_list_item_name);
            dateView = (TextView) itemView.findViewById(R.id.tv_list_item_date);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mCursor.moveToPosition(position);
            long id = mCursor.getLong(mCursor.getColumnIndex(ListContract.ListContractEntry._ID));
            clickListener.onClick(id);
        }
    }
}
