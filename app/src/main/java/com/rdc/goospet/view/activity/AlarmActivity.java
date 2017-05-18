package com.rdc.goospet.view.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.rdc.goospet.R;
import com.rdc.goospet.adapter.AlarmAdapter;
import com.rdc.goospet.entity.Alarm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SC on 2017/5/14.
 */

public class AlarmActivity extends AppCompatActivity {

    private AlarmAdapter alarmAdapter;

    private static final int REQUEST_CREATE = 0x0000ffff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_alarm);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        alarmAdapter = new AlarmAdapter(this);
        recyclerView.setAdapter(alarmAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.alarm_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.alarm_create_item: // 新建闹钟选项
                createAlarm();
                break;
        }
        return  true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Alarm alarm = (Alarm) data.getSerializableExtra("alarm");
            if (requestCode == REQUEST_CREATE) {
                alarmAdapter.addAlarm(alarm);
            } else {
                alarmAdapter.changeAlarm(requestCode, alarm);
            }
        }
    }

    private void createAlarm() {
        Alarm newAlarm = new Alarm(System.currentTimeMillis(), Alarm.MODE_ONCE, "");
        Intent intent = new Intent(AlarmActivity.this, AlarmEditActivity.class);
        intent.putExtra("alarm", newAlarm);
        startActivityForResult(intent, REQUEST_CREATE);
    }

    public void editAlarm(int position, Alarm alarm) {
        Intent intent = new Intent(AlarmActivity.this, AlarmEditActivity.class);
        intent.putExtra("alarm", alarm);
        startActivityForResult(intent, position);
    }
}
