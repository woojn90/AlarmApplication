package com.example.android.alarmapplication.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wjn on 2017-01-24.
 */

public class AlarmDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "alarmApp.db";
    private static final int DATABASE_VERSION = 1;

    public AlarmDbHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_ALARM_TABLE =
                "CREATE TABLE " + AlarmContract.AlarmEntity.TABLE_NAME + " ("
                        + AlarmContract.AlarmEntity._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + AlarmContract.AlarmEntity.COLUMN_SELECTED_HOUR + " INTEGER NOT NULL, "
                        + AlarmContract.AlarmEntity.COLUMN_SELECTED_MINUTE + " INTEGER NOT NULL, "
                        + AlarmContract.AlarmEntity.COLUMN_REPEAT_YN + " TEXT NOT NULL, "
                        + AlarmContract.AlarmEntity.COLUMN_SELECTED_DAY_OF_WEEK + " TEXT, "
                        + AlarmContract.AlarmEntity.COLUMN_SELECTED_DATE + " TIMESTAMP, "
                        + AlarmContract.AlarmEntity.COLUMN_MEMO + " TEXT, "
                        + AlarmContract.AlarmEntity.COLUMN_SOUND_YN + " TEXT NOT NULL, "
                        + AlarmContract.AlarmEntity.COLUMN_VIBRATE_YN + " TEXT NOT NULL, "
                        + AlarmContract.AlarmEntity.COLUMN_SOUND_VOLUME + " INTEGER NOT NULL, "
                        + AlarmContract.AlarmEntity.COLUMN_ENABLE_YN + " TEXT DEFAULT 'Y');";
        db.execSQL(SQL_CREATE_ALARM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + AlarmContract.AlarmEntity.TABLE_NAME);
        onCreate(db);
    }
}
