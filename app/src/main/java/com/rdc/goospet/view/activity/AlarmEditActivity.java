package com.rdc.goospet.view.activity;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.rdc.goospet.R;
import com.rdc.goospet.entity.Alarm;

import java.util.Calendar;

public class AlarmEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_edit);

        Intent intent = getIntent();
        final Alarm alarm = (Alarm) intent.getSerializableExtra("alarm");

        // 设置时间
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(alarm.getAlarmTime());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        final TimePicker timePicker = (TimePicker) findViewById(R.id.tp_alarm);
        timePicker.setIs24HourView(true);
//        timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
//        timePicker.setMinute(calendar.get(Calendar.MINUTE));
        // 模式选择
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Alarm.modes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner spinner = (Spinner) findViewById(R.id.s_alarm);
        spinner.setAdapter(adapter);
        spinner.setSelection(alarm.getMode());

        // 提醒消息
        final EditText editText = (EditText) findViewById(R.id.et_alarm);
        editText.setText(alarm.getMessage());

        // 完成按钮
        Button button = (Button) findViewById(R.id.alarm_finish_button);
        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                calendar.set(Calendar.MINUTE, timePicker.getMinute());
                long alarmTime = calendar.getTimeInMillis();
                if (System.currentTimeMillis() >= alarmTime) alarmTime += 24*60*60*1000;
                else if (System.currentTimeMillis() + 24*60*60*1000 < alarmTime) alarmTime -= 24*60*60*1000;
                alarm.setAlarmTime(alarmTime);

                alarm.setMode(spinner.getSelectedItemPosition());

                String message = editText.getText().toString();
                if (message == null || message.length() == 0) message = " "; // 防止由于使用split()方法读保存的闹钟造成的越界
                alarm.setMessage(message);

                Intent intent = new Intent();
                intent.putExtra("alarm", alarm);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
