package com.example.android.alarmapplication;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.alarmapplication.data.AlarmContract;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.android.alarmapplication.util.AlarmUtility.*;

/**
 * Created by wjn on 2017-01-24.
 */

public class AlarmListAdapter extends RecyclerView.Adapter<AlarmListAdapter.AlarmViewHolder> {

    private Cursor mCursor;
    private Context mContext;

    private final ItemClickListener mOnClickListener;

    public AlarmListAdapter(Context context, Cursor cursor, ItemClickListener listener) {
        this.mContext = context;
        this.mCursor = cursor;
        this.mOnClickListener = listener;
    }

    public interface ItemClickListener {
        void onItemClick(long id, int viewId);
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
        String enableYn = mCursor.getString(mCursor.getColumnIndex(AlarmContract.AlarmEntity.COLUMN_ENABLE_YN));

        long id = mCursor.getLong(mCursor.getColumnIndex(AlarmContract.AlarmEntity._ID));
        holder.itemView.setTag(id);

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
            holder.repeatTextView.setText(mContext.getString(R.string.string_repeat_Y));

            holder.repeatDateTextView.setVisibility(View.GONE);
            holder.repeatWeekDayLayout.setVisibility(View.VISIBLE);

            // 요일 UI
            // Context.getColor()는 API 23 Level 이상부터 가능
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (dayOfWeek.contains("1"))
                    holder.repeatSunTextView.setTextColor(mContext.getColor(R.color.colorEnable));
                if (dayOfWeek.contains("2"))
                    holder.repeatMonTextView.setTextColor(mContext.getColor(R.color.colorEnable));
                if (dayOfWeek.contains("3"))
                    holder.repeatTueTextView.setTextColor(mContext.getColor(R.color.colorEnable));
                if (dayOfWeek.contains("4"))
                    holder.repeatWedTextView.setTextColor(mContext.getColor(R.color.colorEnable));
                if (dayOfWeek.contains("5"))
                    holder.repeatThdTextView.setTextColor(mContext.getColor(R.color.colorEnable));
                if (dayOfWeek.contains("6"))
                    holder.repeatFriTextView.setTextColor(mContext.getColor(R.color.colorEnable));
                if (dayOfWeek.contains("7"))
                    holder.repeatSatTextView.setTextColor(mContext.getColor(R.color.colorEnable));
            } else {
                if (dayOfWeek.contains("1"))
                    holder.repeatSunTextView.setTextColor(mContext.getResources().getColor(R.color.colorEnable));
                if (dayOfWeek.contains("2"))
                    holder.repeatMonTextView.setTextColor(mContext.getResources().getColor(R.color.colorEnable));
                if (dayOfWeek.contains("3"))
                    holder.repeatTueTextView.setTextColor(mContext.getResources().getColor(R.color.colorEnable));
                if (dayOfWeek.contains("4"))
                    holder.repeatWedTextView.setTextColor(mContext.getResources().getColor(R.color.colorEnable));
                if (dayOfWeek.contains("5"))
                    holder.repeatThdTextView.setTextColor(mContext.getResources().getColor(R.color.colorEnable));
                if (dayOfWeek.contains("6"))
                    holder.repeatFriTextView.setTextColor(mContext.getResources().getColor(R.color.colorEnable));
                if (dayOfWeek.contains("7"))
                    holder.repeatSatTextView.setTextColor(mContext.getResources().getColor(R.color.colorEnable));
            }

        } else {
            holder.repeatTextView.setText(mContext.getString(R.string.string_repeat_N));

            holder.repeatWeekDayLayout.setVisibility(View.GONE);
            holder.repeatDateTextView.setVisibility(View.VISIBLE);

            holder.repeatDateTextView.setText(date + getWeekDayFromDate(date));
        }

        holder.timeTextView.setText(getTimeFromHourMinute(hour, minute));
        holder.memoTextView.setText(memo);
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

    class AlarmViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        @BindView(R.id.iv_alarm_on)
        ImageView alarmOnImageView;
        @BindView(R.id.iv_alarm_off)
        ImageView alarmOffImageView;
        @BindView(R.id.tv_repeat)
        TextView repeatTextView;
        @BindView(R.id.tv_repeat_sun)
        TextView repeatSunTextView;
        @BindView(R.id.tv_repeat_mon)
        TextView repeatMonTextView;
        @BindView(R.id.tv_repeat_tue)
        TextView repeatTueTextView;
        @BindView(R.id.tv_repeat_wed)
        TextView repeatWedTextView;
        @BindView(R.id.tv_repeat_thd)
        TextView repeatThdTextView;
        @BindView(R.id.tv_repeat_fri)
        TextView repeatFriTextView;
        @BindView(R.id.tv_repeat_sat)
        TextView repeatSatTextView;
        @BindView(R.id.tv_repeat_date)
        TextView repeatDateTextView;
        @BindView(R.id.layout_repeat_week_day)
        LinearLayout repeatWeekDayLayout;
        @BindView(R.id.tv_time)
        TextView timeTextView;
        @BindView(R.id.tv_memo)
        TextView memoTextView;
        @BindView(R.id.ib_delete)
        ImageButton deleteImageButton;

        public AlarmViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @OnClick({R.id.ib_delete, R.id.iv_alarm_on, R.id.iv_alarm_off})
        public void onClick(View v) {
            long id = (long) itemView.getTag();
            int viewId = v.getId();

            // Main에서 db처리를 위해 id(DB)와 view ID 전달
            mOnClickListener.onItemClick(id, viewId);

            // 알람 활성화 버튼을 누를 경우 On,Off 이미지를 교체
            switch (v.getId()) {
                case R.id.iv_alarm_on:
                    alarmOnImageView.setVisibility(View.GONE);
                    alarmOffImageView.setVisibility(View.VISIBLE);
                    break;

                case R.id.iv_alarm_off:
                    alarmOffImageView.setVisibility(View.GONE);
                    alarmOnImageView.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

}
