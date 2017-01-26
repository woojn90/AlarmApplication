package com.example.android.alarmapplication;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by wjn on 2017-01-27.
 */

public class DetailActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        long id = getIntent().getLongExtra("id", 0);
        Log.d("Debug", "DetailActivity : id = " + id);

        //TODO add
        if (id == 0) {
            setTitle(R.string.action_detail_add);


            Toast.makeText(getApplicationContext(), "신규", Toast.LENGTH_LONG).show();
        }
        //TODO update
        else {
            setTitle(R.string.action_detail_update);


            Toast.makeText(getApplicationContext(), "수정 (id = " + id + ")", Toast.LENGTH_LONG).show();
        }


    }
}
