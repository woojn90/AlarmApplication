package com.example.android.alarmapplication;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.alarmapplication.Receiver.AlarmReceiver;
import com.example.android.alarmapplication.data.AlarmContract;
import com.example.android.alarmapplication.data.AlarmDbHelper;

import java.util.Calendar;

import static com.example.android.alarmapplication.Util.AlarmUtility.setAlarmBySdkVersion;

public class MainActivity extends AppCompatActivity
        implements AlarmListAdapter.ItemClickListener {

    private static final int ACTIVITY_INSERT_REQUEST_CODE = 1;
    private static final int ACTIVITY_UPDATE_REQUEST_CODE = 2;

    private AlarmListAdapter mAdapter;
    private SQLiteDatabase mDb;

    //TODO ButterKnife Library 적용
    private TextView emptyTextView;
    private RecyclerView alarmRecyclerView;

    private Toast mToast;

    private AlarmManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // View 설정
        emptyTextView = (TextView) findViewById(R.id.tv_alarm_empty);
        alarmRecyclerView = (RecyclerView) findViewById(R.id.rv_alarm_list);
        alarmRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // DB 설정
        AlarmDbHelper dbHelper = new AlarmDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        Cursor cursor = getAllAlarms();
        mAdapter = new AlarmListAdapter(this, cursor, this);
        alarmRecyclerView.setAdapter(mAdapter);

        // AlarmManager 설정
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // insert Alarm
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_insert);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent insertIntent = new Intent(MainActivity.this, DetailActivity.class);
                startActivityForResult(insertIntent, ACTIVITY_INSERT_REQUEST_CODE);
            }
        });
    }

    private Cursor getAllAlarms() {
        Cursor cursor = mDb.query(
                AlarmContract.AlarmEntity.TABLE_NAME, null, null, null, null, null, null);

        // 알람이 없을 경우, 추가 권유 메세지가 나옴
        if (cursor != null && cursor.getCount() > 0) {
            emptyTextView.setVisibility(View.GONE);
        } else {
            emptyTextView.setVisibility(View.VISIBLE);
        }
        return cursor;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_setting) {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(final long id, int viewId) {
        ContentValues cv;

        Log.d("Debug", "MainActivity : id : " + id + ", viewId : " + viewId);

        //TODO DB 처리결과 확인 ( if (~~ > 0))
        switch (viewId) {
            // 그냥 item을 클릭했을 경우
            case -1:
                Intent updateIntent = new Intent(MainActivity.this, DetailActivity.class);
                updateIntent.putExtra("id", id);
                updateIntent.putExtra("ContentValue", getContentValuesById(id));
                startActivityForResult(updateIntent, ACTIVITY_UPDATE_REQUEST_CODE);
                break;

            case R.id.ib_delete:
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.string_info)
                        .setMessage(getString(R.string.msg_info_delete))
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDb.delete(AlarmContract.AlarmEntity.TABLE_NAME, AlarmContract.AlarmEntity._ID + "=" + id, null);
                                makeToastMsg(getString(R.string.msg_alarm_delete));
                                deleteAlarm(id);

                                // 변경 내용 UI 적용
                                mAdapter.swapCursor(getAllAlarms());
                            }
                        }).show();
                break;

            case R.id.iv_alarm_on:
                cv = getContentValuesById(id);
                cv.put(AlarmContract.AlarmEntity.COLUMN_ENABLE_YN, "N");
                mDb.update(
                        AlarmContract.AlarmEntity.TABLE_NAME, cv, AlarmContract.AlarmEntity._ID + "=" + id, null);
                insertOrUpdateAlarm(id, cv);
                makeToastMsg(getString(R.string.msg_alarm_off));
                break;

            case R.id.iv_alarm_off:
                cv = getContentValuesById(id);
                cv.put(AlarmContract.AlarmEntity.COLUMN_ENABLE_YN, "Y");
                mDb.update(
                        AlarmContract.AlarmEntity.TABLE_NAME, cv, AlarmContract.AlarmEntity._ID + "=" + id, null);
                insertOrUpdateAlarm(id, cv);
                makeToastMsg(getString(R.string.msg_alarm_on));
                break;
        }
        // 변경 내용 UI 적용
        mAdapter.swapCursor(getAllAlarms());
    }

    // 상세 화면에서 변경된 내용 DB 저장
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // save 일 경우만 적용
        if (resultCode == DetailActivity.ACTIVITY_SAVE_RESULT_CODE) {
            long id = data.getLongExtra("id", 0);
            ContentValues cv = (ContentValues) data.getExtras().get("ContentValue");
            Log.d("Debug", "MainActivity : id = " + id);
            Log.d("Debug", "MainActivity : requestCode = " + requestCode);

            switch (requestCode) {
                case ACTIVITY_INSERT_REQUEST_CODE:
                    mDb.insert(AlarmContract.AlarmEntity.TABLE_NAME, null, cv);
                    insertOrUpdateAlarm(id, cv);
                    makeToastMsg(getString(R.string.msg_alarm_insert));
                    break;
                case ACTIVITY_UPDATE_REQUEST_CODE:
                    mDb.update(
                            AlarmContract.AlarmEntity.TABLE_NAME, cv, AlarmContract.AlarmEntity._ID + "=" + id, null);
                    insertOrUpdateAlarm(id, cv);
                    makeToastMsg(getString(R.string.msg_alarm_update));
                    break;
            }
            // 변경 내용 UI 적용
            mAdapter.swapCursor(getAllAlarms());
        }
    }

    // Toast 메세지 출력 (mToast 이용 null 체크)
    private void makeToastMsg(String msg) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        mToast.show();
    }

    // id로 1개 row(data) 선택 후 ContentValues 로 return
    private ContentValues getContentValuesById(long id) {
        Cursor cursorRow = mDb.query(
                AlarmContract.AlarmEntity.TABLE_NAME, null,
                AlarmContract.AlarmEntity._ID + "=" + id, null, null, null, null);

        ContentValues cv = null;
        if (cursorRow.moveToFirst()) {
            cv = new ContentValues();
            DatabaseUtils.cursorRowToContentValues(cursorRow, cv);
        }
        return cv;
    }

    private long getNewRowId() {
        final String MAX_ROW_ALIAS = "NEW_ID";

        String[] maxCol = {"MAX(" + AlarmContract.AlarmEntity._ID + ") AS " + MAX_ROW_ALIAS};

        Cursor cursorNewRowId = mDb.query(
                AlarmContract.AlarmEntity.TABLE_NAME,
                maxCol,
                null,
                null,
                null,
                null,
                null);

        long newRowId = 0;

        if (cursorNewRowId != null && cursorNewRowId.getCount() != 0) {
            if (cursorNewRowId.moveToFirst()) {
                newRowId = cursorNewRowId.getLong(cursorNewRowId.getColumnIndex(MAX_ROW_ALIAS));
            }
        }
        Log.d("Debug", "MainActivity : newRowId = " + newRowId);
        return newRowId;
    }

    private void insertOrUpdateAlarm(long id, ContentValues cv) {
        // insert 일 경우
        if (id == 0) {
            id = getNewRowId();
        }
        Intent intent = new Intent(this, AlarmReceiver.class);
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
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int)id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        setAlarmBySdkVersion(manager, calendar.getTimeInMillis(), pendingIntent);
    }

    private void deleteAlarm(long id) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int)id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        manager.cancel(pendingIntent);
        pendingIntent.cancel();
    }
}
