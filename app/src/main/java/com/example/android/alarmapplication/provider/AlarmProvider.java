package com.example.android.alarmapplication.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.example.android.alarmapplication.data.AlarmContract;
import com.example.android.alarmapplication.data.AlarmDbHelper;

/**
 * Created by lg on 2017-01-28.
 */

public class AlarmProvider extends ContentProvider {

    private SQLiteDatabase mDb;

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        AlarmDbHelper dbHelper = new AlarmDbHelper(getContext());
        mDb = dbHelper.getWritableDatabase();

        return mDb.query(AlarmContract.AlarmEntity.TABLE_NAME,
                null, null, null, null, null, null);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
