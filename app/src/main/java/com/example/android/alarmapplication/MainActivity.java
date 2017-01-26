package com.example.android.alarmapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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

    private AlarmListAdapter mAdapter;
    private static SQLiteDatabase mDb;

    //TODO ButterKnife Library 적용
    private TextView emptyTextView;
    private RecyclerView alarmRecyclerView;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emptyTextView = (TextView) findViewById(R.id.tv_alarm_empty);
        alarmRecyclerView = (RecyclerView) findViewById(R.id.rv_alarm_list);
        alarmRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        AlarmDbHelper dbHelper = new AlarmDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        Cursor cursor = getAllAlarms();
        mAdapter = new AlarmListAdapter(this, cursor, this);
        alarmRecyclerView.setAdapter(mAdapter);

        // add Alarm
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO fake data 삭제
//                ContentValues cv = new ContentValues();
//                cv.put(AlarmContract.AlarmEntity.COLUMN_SELECTED_HOUR, 8);
//                cv.put(AlarmContract.AlarmEntity.COLUMN_SELECTED_MINUTE, 27);
//                cv.put(AlarmContract.AlarmEntity.COLUMN_REPEAT_YN, "Y");
//                cv.put(AlarmContract.AlarmEntity.COLUMN_SELECTED_DAY_OF_WEEK, "1,3,6");
//                cv.put(AlarmContract.AlarmEntity.COLUMN_SELECTED_DATE, "");
//                cv.put(AlarmContract.AlarmEntity.COLUMN_MEMO, "모닝콜!! 일어낫");
//                cv.put(AlarmContract.AlarmEntity.COLUMN_SOUND_YN, "Y");
//                cv.put(AlarmContract.AlarmEntity.COLUMN_VIBRATE_YN, "N");
//                cv.put(AlarmContract.AlarmEntity.COLUMN_SOUND_VOLUME, 50);
//                ContentValues cv2 = new ContentValues();
//                cv2.put(AlarmContract.AlarmEntity.COLUMN_SELECTED_HOUR, 22);
//                cv2.put(AlarmContract.AlarmEntity.COLUMN_SELECTED_MINUTE, 13);
//                cv2.put(AlarmContract.AlarmEntity.COLUMN_REPEAT_YN, "N");
//                cv2.put(AlarmContract.AlarmEntity.COLUMN_SELECTED_DAY_OF_WEEK, "");
//                cv2.put(AlarmContract.AlarmEntity.COLUMN_SELECTED_DATE, "20170125");
//                cv2.put(AlarmContract.AlarmEntity.COLUMN_MEMO, "치과를 가야함");
//                cv2.put(AlarmContract.AlarmEntity.COLUMN_SOUND_YN, "Y");
//                cv2.put(AlarmContract.AlarmEntity.COLUMN_VIBRATE_YN, "Y");
//                cv2.put(AlarmContract.AlarmEntity.COLUMN_SOUND_VOLUME, 44);
//                cv.put(AlarmContract.AlarmEntity.COLUMN_ENABLE_YN, "N");
//                mDb.insert(AlarmContract.AlarmEntity.TABLE_NAME, null, cv);
//                mDb.insert(AlarmContract.AlarmEntity.TABLE_NAME, null, cv2);
//
//                mAdapter.swapCursor(getAllAlarms());

                Intent addIntent = new Intent(MainActivity.this, DetailActivity.class);
                startActivity(addIntent);
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
                startActivity(updateIntent);
                break;

            case R.id.ib_delete:
                //TODO 삭제 alert 추가
                mDb.delete(AlarmContract.AlarmEntity.TABLE_NAME, AlarmContract.AlarmEntity._ID + "=" + id, null);
                makeToastMsg(getString(R.string.msg_alarm_deleted));
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

    // Toast 메세지 출력 (mToast 이용 null 체크)
    private void makeToastMsg(String msg) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        mToast.show();
    }
}
