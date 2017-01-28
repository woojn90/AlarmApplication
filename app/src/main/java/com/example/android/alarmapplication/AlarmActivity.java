package com.example.android.alarmapplication;

import android.content.ContentValues;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.android.alarmapplication.data.AlarmContract;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.android.alarmapplication.util.AlarmUtility.getTimeFromHourMinute;

/**
 * Created by wjn on 2017-01-28.
 */

public class AlarmActivity extends AppCompatActivity {

    @BindView(R.id.tv_memo_alarm)
    TextView memoTextView;
    @BindView(R.id.tv_time_alarm)
    TextView timeTextView;

    private Runnable mRunnable;
    private Handler mHandler;

    private MediaPlayer music;
    private Vibrator vibe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        ButterKnife.bind(this);

        ContentValues cv = (ContentValues) getIntent().getExtras().get("ContentValue");

        String memo = cv.getAsString(AlarmContract.AlarmEntity.COLUMN_MEMO);
        int hour = cv.getAsInteger(AlarmContract.AlarmEntity.COLUMN_SELECTED_HOUR);
        int minute = cv.getAsInteger(AlarmContract.AlarmEntity.COLUMN_SELECTED_MINUTE);

        memoTextView.setText(memo);
        timeTextView.setText(getTimeFromHourMinute(hour, minute));

        int soundVolume = cv.getAsInteger(AlarmContract.AlarmEntity.COLUMN_SOUND_VOLUME);
        String soundYn = cv.getAsString(AlarmContract.AlarmEntity.COLUMN_SOUND_YN);
        String vibrateYn = cv.getAsString(AlarmContract.AlarmEntity.COLUMN_VIBRATE_YN);

        startAlarm(soundVolume, soundYn, vibrateYn);

        // 1분 뒤 자동 종료
        mRunnable = new Runnable() {
            @Override
            public void run() {
                finishAlarm();
            }
        };
        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, 60 * 1000);
    }

    @OnClick(R.id.btn_cancel_alarm)
    public void onClick(View v) {
        finishAlarm();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnable);
    }

    private void startAlarm(int soundVolume, String soundYn, String vibrateYn) {
        // 소리
        if ("Y".equals(soundYn)) {
            float soundVolumeFloat = soundVolume / 100f;

            music = MediaPlayer.create(this, R.raw.konan);
            music.setLooping(true); // 무한 반복
            music.setVolume(soundVolumeFloat, soundVolumeFloat);

            music.start();
        }
        // 진동
        if ("Y".equals(vibrateYn)) {
            vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = {0, 3000, 500};
            vibe.vibrate(pattern, 1); // 진동 2000, 쉼 500 무한 반복
        }
    }

    private void finishAlarm() {
        if (music != null && music.isPlaying()) {
            music.stop();
        }
        if (vibe != null) {
            vibe.cancel();
        }
        finish();
    }

}
