package com.example.android.alarmapplication;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by wjn on 2017-01-24.
 */

public class AlarmListAdapter extends RecyclerView.Adapter<AlarmListAdapter.AlarmViewHolder> {

    private Cursor mCursor;
    private Context mContext;

    public AlarmListAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;
    }

    @Override
    public AlarmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.alarm_list_item, parent, false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlarmViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) return;

        // Entity 객체 작성 후


    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) mCursor.close();
        mCursor = newCursor;
        if (newCursor != null) {
            this.notifyDataSetChanged();
        }
    }

    class AlarmViewHolder extends RecyclerView.ViewHolder {

        private final ImageView alarmImageView;
        private final ImageView deleteImageView;
        private final TextView repeatTextView;
        private final TextView repeatDetailTextView;
        private final TextView timeTextView;
        private final TextView memoTextView;

        public AlarmViewHolder(View itemView) {
            super(itemView);
            alarmImageView = (ImageView) itemView.findViewById(R.id.iv_alarm);
            deleteImageView = (ImageView) itemView.findViewById(R.id.iv_delete);
            repeatTextView = (TextView) itemView.findViewById(R.id.tv_repeat);
            repeatDetailTextView = (TextView) itemView.findViewById(R.id.tv_repeat_detail);
            timeTextView = (TextView) itemView.findViewById(R.id.tv_time);
            memoTextView = (TextView) itemView.findViewById(R.id.tv_memo);
        }
    }

}
