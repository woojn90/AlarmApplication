package com.example.android.alarmapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.android.alarmapplication.Util.AlarmUtility;
import com.example.android.alarmapplication.data.AlarmContract;

/**
 * Created by wjn on 2017-01-27.
 */

public class DetailActivity extends AppCompatActivity {

    private final static int TAB_REPEAT_Y_INDEX = 0;
    private final static int TAB_REPEAT_N_INDEX = 1;

    private long id;

    private ContentValues mContentValues;
    private TabHost tabHost;

    private TimePicker timePicker;
    private TextView memoEditText;
    private SeekBar volumeSeekBar;
    private ToggleButton soundToggleButton;
    private ToggleButton vibrateToggleButton;
    private ToggleButton sunToggleButton;
    private ToggleButton monToggleButton;
    private ToggleButton tueToggleButton;
    private ToggleButton wedToggleButton;
    private ToggleButton thdToggleButton;
    private ToggleButton friToggleButton;
    private ToggleButton satToggleButton;
    private ImageButton calendarImageButton;
    private TextView dateTextView;
    private Button saveButton;
    private Button cancelButton;

    private Toast mToast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // ActionBar 설정
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Tab 설정
        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();

        TabHost.TabSpec spec1 = tabHost.newTabSpec("Tab1").setContent(R.id.tab_repeat)
                .setIndicator(getString(R.string.string_repeat_Y));
        tabHost.addTab(spec1);

        TabHost.TabSpec spec2 = tabHost.newTabSpec("Tab2").setContent(R.id.tab_once)
                .setIndicator(getString(R.string.string_repeat_N));
        tabHost.addTab(spec2);

        // View 설정
        timePicker =  (TimePicker) findViewById(R.id.tp_detail_time);
        memoEditText = (EditText) findViewById(R.id.et_detail_memo);
        volumeSeekBar = (SeekBar) findViewById(R.id.sb_volume);
        soundToggleButton = (ToggleButton) findViewById(R.id.tb_sound);
        vibrateToggleButton = (ToggleButton) findViewById(R.id.tb_vibrate);
        sunToggleButton = (ToggleButton) findViewById(R.id.tb_day_sun);
        monToggleButton = (ToggleButton) findViewById(R.id.tb_day_mon);
        tueToggleButton = (ToggleButton) findViewById(R.id.tb_day_tue);
        wedToggleButton = (ToggleButton) findViewById(R.id.tb_day_wed);
        thdToggleButton = (ToggleButton) findViewById(R.id.tb_day_thd);
        friToggleButton = (ToggleButton) findViewById(R.id.tb_day_fri);
        satToggleButton = (ToggleButton) findViewById(R.id.tb_day_sat);
        calendarImageButton = (ImageButton) findViewById(R.id.ib_calendar);
        dateTextView = (TextView) findViewById(R.id.tv_date);
        saveButton = (Button) findViewById(R.id.btn_save);
        cancelButton = (Button) findViewById(R.id.btn_cancel);


        id = getIntent().getLongExtra("id", 0);
        Log.d("Debug", "DetailActivity : id = " + id);

        if (id == 0) {
            Log.d("Debug", "DetailActivity : 신규");
            setTitle(R.string.action_detail_insert);

            mContentValues = new ContentValues();
        }
        else {
            Log.d("Debug", "DetailActivity : 수정 (id = " + id + ")");
            setTitle(R.string.action_detail_update);

            mContentValues = (ContentValues) getIntent().getExtras().get("ContentValue");

            for (String key : mContentValues.keySet()) {
                Log.d("Debug", "DetailActivity : key = " + key +
                        ", value = " + mContentValues.getAsString(key));
            }
            initializeUpdateAlarm();
        }

        calendarImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // TODO tag에 String date 로 저장해야함
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateData()) {
                    // TODO alert 추가 (확인창)
                    save();

                    Intent result = new Intent();
                    result.putExtra("id", id); // 0이면 insert
                    result.putExtra("ContentValue", mContentValues);
                    setResult(0, result);

                    finish();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO alert 추가
                finish();
            }
        });
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
        }
        // 1회일 경우
        else {
            tabHost.setCurrentTab(TAB_REPEAT_N_INDEX);
            String date = mContentValues.getAsString(AlarmContract.AlarmEntity.COLUMN_SELECTED_DATE);
            dateTextView.setText(date + AlarmUtility.getWeekDayFromDate(date));
            dateTextView.setTag(date);
        }
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
            // TODO 현재보다 이후 인지 확인 (time, date 전부 고려)

        }

        return true;
    }

    private void save() {
        // setHour, setMinute은 API 23 Level 이상부터 가능
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
