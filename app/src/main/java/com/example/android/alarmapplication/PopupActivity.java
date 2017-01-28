package com.example.android.alarmapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;

import com.example.android.alarmapplication.util.AlarmUtility;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wjn on 2017-01-28.
 */

public class PopupActivity extends Activity {

    public static final int ACTIVITY_FINISH_RESULT_CODE = 1;

    @BindView(R.id.dp_date)
    DatePicker datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_finish, R.id.btn_cancel})
    public void onClick(View v) {
        if (v.getId() == R.id.btn_finish) {
            int year = datePicker.getYear();
            int month = datePicker.getMonth() + 1;
            int day = datePicker.getDayOfMonth();

            String date = AlarmUtility.getDateFromYearMonthDay(year, month, day);

            Intent result = new Intent();
            result.putExtra("date", date);
            setResult(ACTIVITY_FINISH_RESULT_CODE, result);
        }
        finish();
    }
}
