package com.example.android.alarmapplication;

import android.content.ContentValues;
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

import com.example.android.alarmapplication.data.AlarmContract;
import com.example.android.alarmapplication.data.AlarmDbHelper;

public class MainActivity extends AppCompatActivity
        implements AlarmListAdapter.ItemClickListener {

    private static final int ACTIVITY_INSERT_REQUEST_CODE = 0;
    private static final int ACTIVITY_UPDATE_REQUEST_CODE = 1;

    private AlarmListAdapter mAdapter;
    private SQLiteDatabase mDb;

    //TODO ButterKnife Library 적용
    private TextView emptyTextView;
    private RecyclerView alarmRecyclerView;

    private Toast mToast;

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
    public void onItemClick(long id, int viewId) {
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
                //TODO 삭제 alert 추가
                mDb.delete(AlarmContract.AlarmEntity.TABLE_NAME, AlarmContract.AlarmEntity._ID + "=" + id, null);
                makeToastMsg(getString(R.string.msg_alarm_delete));
                break;

            case R.id.iv_alarm_on:
                cv = new ContentValues();
                cv.put(AlarmContract.AlarmEntity.COLUMN_ENABLE_YN, "N");
                mDb.update(
                        AlarmContract.AlarmEntity.TABLE_NAME, cv, AlarmContract.AlarmEntity._ID + "=" + id, null);
                makeToastMsg(getString(R.string.msg_alarm_off));
                break;

            case R.id.iv_alarm_off:
                cv = new ContentValues();
                cv.put(AlarmContract.AlarmEntity.COLUMN_ENABLE_YN, "Y");
                mDb.update(
                        AlarmContract.AlarmEntity.TABLE_NAME, cv, AlarmContract.AlarmEntity._ID + "=" + id, null);
                makeToastMsg(getString(R.string.msg_alarm_on));
                break;
        }
        // 변경 내용 UI 적용
        mAdapter.swapCursor(getAllAlarms());
    }

    // 상세 화면에서 변경된 내용 DB 저장
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // cancel 일 경우 적용 안되게
        if (data != null) {
            long id = data.getLongExtra("id", 0);
            ContentValues cv = (ContentValues) data.getExtras().get("ContentValue");
            Log.d("Debug", "MainActivity : id = " + id);
            Log.d("Debug", "MainActivity : requestCode = " + requestCode);

            switch (requestCode) {
                case ACTIVITY_INSERT_REQUEST_CODE:
                    mDb.insert(AlarmContract.AlarmEntity.TABLE_NAME, null, cv);
                    makeToastMsg(getString(R.string.msg_alarm_insert));
                    break;
                case ACTIVITY_UPDATE_REQUEST_CODE:
                    mDb.update(
                            AlarmContract.AlarmEntity.TABLE_NAME, cv, AlarmContract.AlarmEntity._ID + "=" + id, null);
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


}
