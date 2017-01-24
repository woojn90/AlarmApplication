package com.example.android.alarmapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.alarmapplication.data.AlarmContract;
import com.example.android.alarmapplication.data.AlarmDbHelper;

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
    public void onBindViewHolder(final AlarmViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) return;

        int hour = mCursor.getInt(mCursor.getColumnIndex(AlarmContract.AlarmEntity.COLUMN_SELECTED_HOUR));
        int minute = mCursor.getInt(mCursor.getColumnIndex(AlarmContract.AlarmEntity.COLUMN_SELECTED_MINUTE));
        String repeatYn = mCursor.getString(mCursor.getColumnIndex(AlarmContract.AlarmEntity.COLUMN_REPEAT_YN));
        String dayOfWeek = mCursor.getString(mCursor.getColumnIndex(AlarmContract.AlarmEntity.COLUMN_SELECTED_DAY_OF_WEEK));
        String date = mCursor.getString(mCursor.getColumnIndex(AlarmContract.AlarmEntity.COLUMN_SELECTED_DATE));
        String memo = mCursor.getString(mCursor.getColumnIndex(AlarmContract.AlarmEntity.COLUMN_MEMO));
        String soundYn = mCursor.getString(mCursor.getColumnIndex(AlarmContract.AlarmEntity.COLUMN_SOUND_YN));
        String vibrateYn = mCursor.getString(mCursor.getColumnIndex(AlarmContract.AlarmEntity.COLUMN_VIBRATE_YN));
        int soundVolume = mCursor.getInt(mCursor.getColumnIndex(AlarmContract.AlarmEntity.COLUMN_SOUND_VOLUME));
        String enableYn = mCursor.getString(mCursor.getColumnIndex(AlarmContract.AlarmEntity.COLUMN_ENABLE_YN));

        long id = mCursor.getLong(mCursor.getColumnIndex(AlarmContract.AlarmEntity._ID));
        holder.deleteImageButton.setTag(id);

        // 활성화 여부
        if ("Y".equals(enableYn)) {
            holder.alarmOffImageView.setVisibility(View.GONE);
            holder.alarmOnImageView.setVisibility(View.VISIBLE);
        } else {
            holder.alarmOnImageView.setVisibility(View.GONE);
            holder.alarmOffImageView.setVisibility(View.VISIBLE);
        }

        // 반복 여부
        if ("Y".equals(repeatYn)) {
            holder.repeatTextView.setText(mContext.getString(R.string.repeat_Y));
            holder.repeatDetailTextView.setText(dayOfWeek
                    .replace("1","일")
                    .replace("2","월")
                    .replace("3","화")
                    .replace("4","수")
                    .replace("5","목")
                    .replace("6","금")
                    .replace("7","토"));

        } else {
            holder.repeatTextView.setText(mContext.getString(R.string.repeat_Y));
            holder.repeatDetailTextView.setText(date);
        }

        String meridian = hour >= 12 ? "AM" : "PM";
        holder.timeTextView.setText(meridian + " " + hour + ":" + minute);
        holder.memoTextView.setText(memo);


        // Delete Alarm
        holder.deleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long id = (long) v.getTag();
                SQLiteDatabase db = new AlarmDbHelper(mContext).getWritableDatabase();
                // alert 추가할 것
                db.delete(AlarmContract.AlarmEntity.TABLE_NAME, AlarmContract.AlarmEntity._ID + "=" + id, null);
            }
        });

        // Enable Alarm
        holder.alarmOnImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                holder.alarmOffImageView.setVisibility(View.VISIBLE);
                // db 처리 필요
            }
        });
        holder.alarmOffImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                holder.alarmOnImageView.setVisibility(View.VISIBLE);
                // db 처리 필요
            }
        });

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

        private final ImageView alarmOnImageView;
        private final ImageView alarmOffImageView;
        private final TextView repeatTextView;
        private final TextView repeatDetailTextView;
        private final TextView timeTextView;
        private final TextView memoTextView;
        private final ImageButton deleteImageButton;

        public AlarmViewHolder(final View itemView) {
            super(itemView);
            repeatTextView = (TextView) itemView.findViewById(R.id.tv_repeat);
            repeatDetailTextView = (TextView) itemView.findViewById(R.id.tv_repeat_detail);
            timeTextView = (TextView) itemView.findViewById(R.id.tv_time);
            memoTextView = (TextView) itemView.findViewById(R.id.tv_memo);
            deleteImageButton = (ImageButton) itemView.findViewById(R.id.ib_delete);
            alarmOnImageView = (ImageView) itemView.findViewById(R.id.iv_alarm_on);
            alarmOffImageView = (ImageView) itemView.findViewById(R.id.iv_alarm_off);
        }

    }

}
