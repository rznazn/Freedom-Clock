package com.babykangaroo.android.freedomclock;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.babykangaroo.android.mydatabaselibrary.ListContract;

import java.text.SimpleDateFormat;

/**
 * Created by Gene Denney on 7/22/2017.
 */

public class MyCursorAdapter extends RecyclerView.Adapter<MyCursorAdapter.DeadlineViewHolder> {

    private Cursor mCursor;
    private Context mContext;

    public MyCursorAdapter(Context context){
        mContext = context;
    }
    @Override
    public DeadlineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.deadline_list_item,parent, false);
        return new DeadlineViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(DeadlineViewHolder holder, int position) {
            mCursor.moveToPosition(position);
            String event = mCursor.getString(
                    mCursor.getColumnIndex(ListContract.ListContractEntry.COLUMN_ITEM_NAME));
            holder.nameView.setText(event);
            long dateLong = mCursor.getLong(mCursor.getColumnIndex(ListContract.ListContractEntry.COLUMN_ITEM_DATE));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy-MMM-dd");
            String date = simpleDateFormat.format(dateLong);
            holder.dateView.setText(date);

    }

    void swapCursor(Cursor cursor){
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

    class DeadlineViewHolder extends RecyclerView.ViewHolder {
        TextView nameView;
        TextView dateView;
         DeadlineViewHolder(View itemView) {
            super(itemView);
            nameView = (TextView) itemView.findViewById(R.id.tv_list_item_name);
            dateView = (TextView) itemView.findViewById(R.id.tv_list_item_date);
        }
    }
}
