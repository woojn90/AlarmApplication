package com.example.android.alarmapplication.Util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.os.Build;

import java.sql.Date;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by wjn on 2017-01-27.
 */

public class AlarmUtility {

   public static String getTimeFromHourMinute(int hour, int minute) {
       String meridian = hour >= 12 ? "PM" : "AM";
       if (hour >= 13) hour -= 12;

       return meridian + " " + String.format("%02d", hour)
               + ":" + String.format("%02d",minute);
   }

    // date를 입력하면 요일을 반환해주는 method
    // '2017/1/28' 을 input 으로 넣으면 ' (토)' 를 return
    public static String getWeekDayFromDate(String date) {
        Calendar c = Calendar.getInstance();
        c.setTime(Date.valueOf(date.replace("/", "-")));

        return " (" + c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()) + ")";
    }

    // year, month, day를 int값으로 넣으면 String date를 반환
    public static String getDateFromYearMonthDay(int year, int month, int day) {
        return year + "/" + String.format("%02d",month) + "/" + String.format("%02d",day);
    }

    // SDK version에 따라 다른 알람 세팅 적용
    public static void setAlarmBySdkVersion(AlarmManager manager, long time, PendingIntent pendingIntent) {

        // SDK 23 이상 (doze 방지)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        }
        // SDK 19 이상
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            manager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        }
        else {
            manager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        }
    }

}
