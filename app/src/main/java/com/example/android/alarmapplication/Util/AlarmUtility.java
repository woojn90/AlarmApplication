package com.example.android.alarmapplication.Util;

import java.sql.Date;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by wjn on 2017-01-27.
 */

public class AlarmUtility {

    // date를 입력하면 요일을 반환해주는 method
    // '2017/1/28' 을 input 으로 넣으면 ' (토)' 를 return
    public static String getWeekDayFromDate(String date) {
        Calendar c = Calendar.getInstance();
        c.setTime(Date.valueOf(date.replace("/", "-")));

        return " (" + c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()) + ")";
    }

}
