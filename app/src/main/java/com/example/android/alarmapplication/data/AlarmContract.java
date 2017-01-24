package com.example.android.alarmapplication.data;

import android.provider.BaseColumns;

/**
 * Created by wjn on 2017-01-24.
 */

public class AlarmContract {

    private AlarmContract() {};

    public static final class AlarmEntity implements BaseColumns {
        public static final String TABLE_NAME = "alarm";
        public static final String COLUMN_SELECTED_HOUR = "selectedHour";
        public static final String COLUMN_SELECTED_MINUTE = "selectedMinute";
        public static final String COLUMN_REPEAT_YN = "repeatYn";
        public static final String COLUMN_SELECTED_DAY_OF_WEEK = "selectedDayOfWeek";
        public static final String COLUMN_SELECTED_DATE = "selectedDate";
        public static final String COLUMN_MEMO = "memo";
        public static final String COLUMN_SOUND_YN = "soundYn";
        public static final String COLUMN_VIBRATE_YN = "vibrateYn";
        public static final String COLUMN_SOUND_VOLUME = "soundVolume";
        public static final String COLUMN_ENABLE_YN = "enableYn";
    }

}
