package com.example.android.alarmapplication;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.android.alarmapplication.data.AlarmContract;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.android.alarmapplication.util.AlarmUtility.getDateFromYearMonthDay;
import static com.example.android.alarmapplication.util.AlarmUtility.getWeekDayFromDate;

/**
 * Created by wjn on 2017-01-27.
 */

public class DetailActivity extends AppCompatActivity {

    private static final int ACTIVITY_POPUP_REQUEST_CODE = 1;

    public static final int ACTIVITY_SAVE_RESULT_CODE = 1;

    private final static int TAB_REPEAT_Y_INDEX = 0;
    private final static int TAB_REPEAT_N_INDEX = 1;

    private long id;

    private ContentValues mContentValues;


    @BindView(android.R.id.tabhost)
    TabHost tabHost;
    @BindView(R.id.tp_detail_time)
    TimePicker timePicker;
    @BindView(R.id.et_detail_memo)
    TextView memoEditText;
    @BindView(R.id.sb_volume)
    SeekBar volumeSeekBar;
    @BindView(R.id.tb_sound)
    ToggleButton soundToggleButton;
    @BindView(R.id.tb_vibrate)
    ToggleButton vibrateToggleButton;
    @BindView(R.id.tb_day_sun)
    ToggleButton sunToggleButton;
    @BindView(R.id.tb_day_mon)
    ToggleButton monToggleButton;
    @BindView(R.id.tb_day_tue)
    ToggleButton tueToggleButton;
    @BindView(R.id.tb_day_wed)
    ToggleButton wedToggleButton;
    @BindView(R.id.tb_day_thd)
    ToggleButton thdToggleButton;
    @BindView(R.id.tb_day_fri)
    ToggleButton friToggleButton;
    @BindView(R.id.tb_day_sat)
    ToggleButton satToggleButton;
    @BindView(R.id.tv_date)
    TextView dateTextView;

    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        // ActionBar 설정
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Tab 설정
        tabHost.setup();

        TabHost.TabSpec spec1 = tabHost.newTabSpec("Tab1").setContent(R.id.tab_repeat)
                .setIndicator(getString(R.string.string_repeat_Y));
        tabHost.addTab(spec1);
        TabHost.TabSpec spec2 = tabHost.newTabSpec("Tab2").setContent(R.id.tab_once)
                .setIndicator(getString(R.string.string_repeat_N));
        tabHost.addTab(spec2);


        id = getIntent().getLongExtra("id", 0);
        Log.d("Debug", "DetailActivity : id = " + id);

        if (id == 0) {
            Log.d("Debug", "DetailActivity : 신규");
            setTitle(R.string.action_detail_insert);

            mContentValues = new ContentValues();
            initializeInsertAlarm();
        }
        else {
            Log.d("Debug", "DetailActivity : 수정 (id = " + id + ")");
            setTitle(R.string.action_detail_update);

            mContentValues = (ContentValues) getIntent().getExtras().get("ContentValue");
            initializeUpdateAlarm();
        }
    }

    @OnClick({R.id.ib_calendar, R.id.btn_save, R.id.btn_cancel})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_calendar:
                Intent popupIntent = new Intent(DetailActivity.this, PopupActivity.class);
                startActivityForResult(popupIntent, ACTIVITY_POPUP_REQUEST_CODE);
                break;
            case R.id.btn_save:
                if (validateData()) {
                    new AlertDialog.Builder(DetailActivity.this)
                            .setTitle(R.string.string_info)
                            .setMessage(R.string.msg_info_save)
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    save();

                                    Intent result = new Intent();
                                    result.putExtra("id", id); // 0이면 insert
                                    result.putExtra("ContentValue", mContentValues);
                                    setResult(ACTIVITY_SAVE_RESULT_CODE, result);

                                    finish();
                                }
                            }).show();
                }
            break;
            case R.id.btn_cancel:
                finish();
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // finish 일 경우만 적용
        if (resultCode == PopupActivity.ACTIVITY_FINISH_RESULT_CODE) {
            String date = data.getStringExtra("date");

            Log.d("Debug", "DetailActivity : date = " + date);

            switch (requestCode) {
                case ACTIVITY_POPUP_REQUEST_CODE:
                    dateTextView.setText(date + getWeekDayFromDate(date));
                    dateTextView.setTag(date);
                    break;
            }
        }
    }

    // Insert 일 경우 현재 시간, 날짜로 적용
    private void initializeInsertAlarm() {
        Calendar calendar = Calendar.getInstance();

        // setHour, setMinute은 API 23 Level 이상부터 가능
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
            timePicker.setMinute(calendar.get(Calendar.MINUTE));
        } else {
            timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
            timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
        }
        initializeCurrentDate();
    }

    // Update 일 경우 기존 설정값을 UI에 적용
    private void initializeUpdateAlarm() {

        // setHour, setMinute은 API 23 Level 이상부터 가능
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(mContentValues.getAsInteger(AlarmContract.AlarmEntity.COLUMN_SELECTED_HOUR));
            timePicker.setMinute(mContentValues.getAsInteger(AlarmContract.AlarmEntity.COLUMN_SELECTED_MINUTE));
        } else {
            timePicker.setCurrentHour(mContentValues.getAsInteger(AlarmContract.AlarmEntity.COLUMN_SELECTED_HOUR));
            timePicker.setCurrentMinute(mContentValues.getAsInteger(AlarmContract.AlarmEntity.COLUMN_SELECTED_MINUTE));
        }

        memoEditText.setText(mContentValues.getAsString(AlarmContract.AlarmEntity.COLUMN_MEMO));
        volumeSeekBar.setProgress(mContentValues.getAsInteger(AlarmContract.AlarmEntity.COLUMN_SOUND_VOLUME));

        soundToggleButton.setChecked(
                "Y".equals(mContentValues.getAsString(AlarmContract.AlarmEntity.COLUMN_SOUND_YN)));
        vibrateToggleButton.setChecked(
                "Y".equals(mContentValues.getAsString(AlarmContract.AlarmEntity.COLUMN_VIBRATE_YN)));

        // 반복일 경우
        if ("Y".equals(mContentValues.getAsString(AlarmContract.AlarmEntity.COLUMN_REPEAT_YN))) {
            tabHost.setCurrentTab(TAB_REPEAT_Y_INDEX);
            String sequenceNumOfDayOfWeek = mContentValues.getAsString(AlarmContract.AlarmEntity.COLUMN_SELECTED_DAY_OF_WEEK);

            if (sequenceNumOfDayOfWeek.contains("1")) sunToggleButton.setChecked(true);
            if (sequenceNumOfDayOfWeek.contains("2")) monToggleButton.setChecked(true);
            if (sequenceNumOfDayOfWeek.contains("3")) tueToggleButton.setChecked(true);
            if (sequenceNumOfDayOfWeek.contains("4")) wedToggleButton.setChecked(true);
            if (sequenceNumOfDayOfWeek.contains("5")) thdToggleButton.setChecked(true);
            if (sequenceNumOfDayOfWeek.contains("6")) friToggleButton.setChecked(true);
            if (sequenceNumOfDayOfWeek.contains("7")) satToggleButton.setChecked(true);

            initializeCurrentDate();
        }
        // 1회일 경우
        else {
            tabHost.setCurrentTab(TAB_REPEAT_N_INDEX);
            String date = mContentValues.getAsString(AlarmContract.AlarmEntity.COLUMN_SELECTED_DATE);
            dateTextView.setText(date + getWeekDayFromDate(date));
            dateTextView.setTag(date);
        }
    }

    private void initializeCurrentDate() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        String date = getDateFromYearMonthDay(year, month, day);

        dateTextView.setText(date + getWeekDayFromDate(date));
        dateTextView.setTag(date);
    }

    // 가능한 data 인지 확인
    private boolean validateData() {
        if (!soundToggleButton.isChecked() && !vibrateToggleButton.isChecked()) {
            makeToastMsg(getString(R.string.msg_valid_way));
            return false;
        }

        // 반복일 경우
        if (tabHost.getCurrentTab() == TAB_REPEAT_Y_INDEX) {
            if (!sunToggleButton.isChecked() && !monToggleButton.isChecked()
                    && !tueToggleButton.isChecked() && !wedToggleButton.isChecked()
                    && !thdToggleButton.isChecked() && !thdToggleButton.isChecked()
                    && !satToggleButton.isChecked()) {
                makeToastMsg(getString(R.string.msg_valid_week_day));
                return false;
            }
        }
        // 1회일 경우
        else {
            Calendar selectedCalendar = Calendar.getInstance();

            int hour;
            int minute;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                hour = timePicker.getHour();
                minute = timePicker.getMinute();
            } else {
                hour = timePicker.getCurrentHour();
                minute = timePicker.getCurrentMinute();
            }

            String[] dates = dateTextView.getTag().toString().split("/");
            int year = Integer.parseInt(dates[0]);
            int month = Integer.parseInt(dates[1]) - 1;
            int day = Integer.parseInt(dates[2]);

            selectedCalendar.set(year, month, day, hour, minute, 0);

            // 현재보다 과거인지 확인 (time, date 전부 고려)
            if (selectedCalendar.getTimeInMillis() < System.currentTimeMillis()) {
                makeToastMsg(getString(R.string.msg_valid_past));
                return false;
            }
        }
        return true;
    }

    private void save() {
        // getHour, getMinute은 API 23 Level 이상부터 가능
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mContentValues.put(AlarmContract.AlarmEntity.COLUMN_SELECTED_HOUR, timePicker.getHour());
            mContentValues.put(AlarmContract.AlarmEntity.COLUMN_SELECTED_MINUTE, timePicker.getMinute());
        } else {
            mContentValues.put(AlarmContract.AlarmEntity.COLUMN_SELECTED_HOUR, timePicker.getCurrentHour());
            mContentValues.put(AlarmContract.AlarmEntity.COLUMN_SELECTED_MINUTE, timePicker.getCurrentMinute());
        }

        mContentValues.put(AlarmContract.AlarmEntity.COLUMN_MEMO, memoEditText.getText().toString().trim());
        mContentValues.put(AlarmContract.AlarmEntity.COLUMN_SOUND_VOLUME, volumeSeekBar.getProgress());
        mContentValues.put(AlarmContract.AlarmEntity.COLUMN_SOUND_YN, soundToggleButton.isChecked() ? "Y" : "N");
        mContentValues.put(AlarmContract.AlarmEntity.COLUMN_VIBRATE_YN, vibrateToggleButton.isChecked() ? "Y" : "N");

        mContentValues.put(AlarmContract.AlarmEntity.COLUMN_ENABLE_YN, "Y");

        // 반복일 경우
        if (tabHost.getCurrentTab() == TAB_REPEAT_Y_INDEX) {
            mContentValues.put(AlarmContract.AlarmEntity.COLUMN_REPEAT_YN, "Y");
            mContentValues.put(AlarmContract.AlarmEntity.COLUMN_SELECTED_DATE, "");

            String weekDay = "";
            if (sunToggleButton.isChecked()) weekDay += "1";
            if (monToggleButton.isChecked()) weekDay += "2";
            if (tueToggleButton.isChecked()) weekDay += "3";
            if (wedToggleButton.isChecked()) weekDay += "4";
            if (thdToggleButton.isChecked()) weekDay += "5";
            if (friToggleButton.isChecked()) weekDay += "6";
            if (satToggleButton.isChecked()) weekDay += "7";

            mContentValues.put(AlarmContract.AlarmEntity.COLUMN_SELECTED_DAY_OF_WEEK, weekDay);
        }
        // 1회일 경우
        else {
            mContentValues.put(AlarmContract.AlarmEntity.COLUMN_REPEAT_YN, "N");
            mContentValues.put(AlarmContract.AlarmEntity.COLUMN_SELECTED_DAY_OF_WEEK, "");

            mContentValues.put(AlarmContract.AlarmEntity.COLUMN_SELECTED_DATE, dateTextView.getTag().toString());
        }
    }

    // Toast 메세지 출력 (mToast 이용 null 체크)
    public void makeToastMsg(String msg) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        mToast.show();
    }


}
