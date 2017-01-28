package com.example.android.alarmapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;

import com.example.android.alarmapplication.Util.AlarmUtility;

/**
 * Created by wjn on 2017-01-28.
 */

public class PopupActivity extends Activity {

    public static final int ACTIVITY_FINISH_RESULT_CODE = 1;

    private DatePicker datePicker;
    private Button cancelButton;
    private Button finishButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);

        datePicker = (DatePicker) findViewById(R.id.dp_date);
        cancelButton = (Button) findViewById(R.id.btn_cancel);
        finishButton = (Button) findViewById(R.id.btn_finish);

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = datePicker.getYear();
                int month = datePicker.getMonth() + 1;
                int day = datePicker.getDayOfMonth();

                String date = AlarmUtility.getDateFromYearMonthDay(year, month, day);

                Intent result = new Intent();
                result.putExtra("date", date);
                setResult(ACTIVITY_FINISH_RESULT_CODE, result);

                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
