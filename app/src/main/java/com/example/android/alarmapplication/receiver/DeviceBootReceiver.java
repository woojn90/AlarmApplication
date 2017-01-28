package com.example.android.alarmapplication.receiver;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.example.android.alarmapplication.data.AlarmContract;
import com.example.android.alarmapplication.data.AlarmDbHelper;

import static com.example.android.alarmapplication.util.AlarmUtility.setAlarmService;

/**
 * Created by wjn on 2017-01-28.
 */

public class DeviceBootReceiver extends BroadcastReceiver {

    private SQLiteDatabase mDb;
    private AlarmManager manager;

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            // DB 설정 (조회에 필요)
            AlarmDbHelper dbHelper = new AlarmDbHelper(context);
            mDb = dbHelper.getWritableDatabase();

            // AlarmManager 설정
            manager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);

            Cursor cursor = mDb.query(
                    AlarmContract.AlarmEntity.TABLE_NAME, null,
                    AlarmContract.AlarmEntity.COLUMN_ENABLE_YN + "='Y'", null, null, null, null);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(AlarmContract.AlarmEntity._ID));
                ContentValues cv = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(cursor, cv);

                setAlarmService(context, manager, id, cv);
            }
        }
    }

}
