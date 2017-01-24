package com.example.android.alarmapplication;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.android.alarmapplication.data.AlarmContract;
import com.example.android.alarmapplication.data.AlarmDbHelper;

public class MainActivity extends AppCompatActivity {

    private AlarmListAdapter mAdapter;
    private static SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView alarmRecyclerView;

        alarmRecyclerView = (RecyclerView) findViewById(R.id.rv_alarm_list);
        alarmRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        AlarmDbHelper dbHelper = new AlarmDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        Cursor cursor = getAllAlarms();
        mAdapter = new AlarmListAdapter(this, cursor);
        alarmRecyclerView.setAdapter(mAdapter);

        // add Alarm
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues cv = new ContentValues();
                cv.put(AlarmContract.AlarmEntity.COLUMN_SELECTED_HOUR, 8);
                cv.put(AlarmContract.AlarmEntity.COLUMN_SELECTED_MINUTE, 27);
                cv.put(AlarmContract.AlarmEntity.COLUMN_REPEAT_YN, "Y");
                cv.put(AlarmContract.AlarmEntity.COLUMN_SELECTED_DAY_OF_WEEK, "1,3,6");
                cv.put(AlarmContract.AlarmEntity.COLUMN_SELECTED_DATE, "");
                cv.put(AlarmContract.AlarmEntity.COLUMN_MEMO, "모닝콜!! 일어낫");
                cv.put(AlarmContract.AlarmEntity.COLUMN_SOUND_YN, "Y");
                cv.put(AlarmContract.AlarmEntity.COLUMN_VIBRATE_YN, "N");
                cv.put(AlarmContract.AlarmEntity.COLUMN_SOUND_VOLUME, 50);
                ContentValues cv2 = new ContentValues();
                cv2.put(AlarmContract.AlarmEntity.COLUMN_SELECTED_HOUR, 22);
                cv2.put(AlarmContract.AlarmEntity.COLUMN_SELECTED_MINUTE, 13);
                cv2.put(AlarmContract.AlarmEntity.COLUMN_REPEAT_YN, "N");
                cv2.put(AlarmContract.AlarmEntity.COLUMN_SELECTED_DAY_OF_WEEK, "");
                cv2.put(AlarmContract.AlarmEntity.COLUMN_SELECTED_DATE, "20170125");
                cv2.put(AlarmContract.AlarmEntity.COLUMN_MEMO, "치과를 가야함");
                cv2.put(AlarmContract.AlarmEntity.COLUMN_SOUND_YN, "Y");
                cv2.put(AlarmContract.AlarmEntity.COLUMN_VIBRATE_YN, "Y");
                cv2.put(AlarmContract.AlarmEntity.COLUMN_SOUND_VOLUME, 44);
                cv.put(AlarmContract.AlarmEntity.COLUMN_ENABLE_YN, "N");
                mDb.insert(AlarmContract.AlarmEntity.TABLE_NAME, null, cv);
                mDb.insert(AlarmContract.AlarmEntity.TABLE_NAME, null, cv2);

                mAdapter.swapCursor(getAllAlarms());
            }
        });

    }

    private Cursor getAllAlarms() {
        return mDb.query(
                AlarmContract.AlarmEntity.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);
    }

}
