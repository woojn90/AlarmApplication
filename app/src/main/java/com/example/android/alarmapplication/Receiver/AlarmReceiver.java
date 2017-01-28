package com.example.android.alarmapplication.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.android.alarmapplication.AlarmActivity;
import com.example.android.alarmapplication.data.AlarmContract;

import java.util.Calendar;

import static com.example.android.alarmapplication.util.AlarmUtility.setAlarmBySdkVersion;

/**
 * Created by wjn on 2017-01-28.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        long id = intent.getLongExtra("id", 0);
        ContentValues cv = (ContentValues) intent.getExtras().get("ContentValue");

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int)id, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // 반복일 경우
        if (cv != null &&
                "Y".equals(cv.getAsString(AlarmContract.AlarmEntity.COLUMN_REPEAT_YN))) {

            String allDayOfWeek = cv.getAsString(AlarmContract.AlarmEntity.COLUMN_SELECTED_DAY_OF_WEEK);
            String todaysDayOfWeek = String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));

            Log.d("Debug", "AlarmReceiver : allDayOfWeek = " + allDayOfWeek);
            Log.d("Debug", "AlarmReceiver : todaysDayOfWeek = " + todaysDayOfWeek);

            // 24시간 후로 동일 알람 생성
            setAlarmBySdkVersion(manager, System.currentTimeMillis() + 24 * 60 * 60 * 1000, pendingIntent);

            // 선택한 요일일 경우에만 알람이 울림
            if (allDayOfWeek.contains(todaysDayOfWeek)) {
                Log.d("Debug", "반복 alarm alarm!!");
                startAlarmActivity(context, cv);
            }
        }
        // 1회일 경우
        else {
            Log.d("Debug", "1회 alarm alarm!!");
            startAlarmActivity(context, cv);
        }
    }

    private void startAlarmActivity(Context context, ContentValues cv) {
        Intent alarmIntent = new Intent(context, AlarmActivity.class);
        alarmIntent.putExtra("ContentValue", cv);
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(alarmIntent);
    }
}
