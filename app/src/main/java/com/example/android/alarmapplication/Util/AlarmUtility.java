package com.example.android.alarmapplication.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.android.alarmapplication.receiver.AlarmReceiver;
import com.example.android.alarmapplication.data.AlarmContract;

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

    public static void setAlarmService(Context context, AlarmManager manager, long id, ContentValues cv) {

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("id", id);
        intent.putExtra("ContentValue", cv);

        Calendar calendar = Calendar.getInstance();

        int hour = cv.getAsInteger(AlarmContract.AlarmEntity.COLUMN_SELECTED_HOUR);
        int minute = cv.getAsInteger(AlarmContract.AlarmEntity.COLUMN_SELECTED_MINUTE);
        int year;
        int month;
        int day;

        // 반복일 경우
        if ("Y".equals(cv.getAsString(AlarmContract.AlarmEntity.COLUMN_REPEAT_YN))) {
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);

            calendar.set(year, month, day, hour, minute, 0);

            // 현재보다 과거일 경우 다음날로 세팅
            if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
        }
        // 1회일 경우
        else {
            String[] dates = cv.getAsString(AlarmContract.AlarmEntity.COLUMN_SELECTED_DATE).split("/");
            year = Integer.parseInt(dates[0]);
            month = Integer.parseInt(dates[1]) - 1;
            day = Integer.parseInt(dates[2]);

            calendar.set(year, month, day, hour, minute, 0);

            // 현재보다 과거일 경우 세팅하지 않음 (BOOT_COMPLETED 용)
            // 일반적인 추가, 수정의 경우 UI에서 Validation check를 하기 때문에 영향이 없는 부분임
            if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                return;
            }
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int)id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        setAlarmBySdkVersion(manager, calendar.getTimeInMillis(), pendingIntent);
    }

}
