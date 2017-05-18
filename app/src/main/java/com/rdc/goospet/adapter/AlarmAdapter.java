package com.rdc.goospet.adapter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.rdc.goospet.R;
import com.rdc.goospet.entity.Alarm;
import com.rdc.goospet.receiver.AlarmReceiver;
import com.rdc.goospet.view.activity.AlarmActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by SC on 2017/5/14.
 */

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {

    private List<Alarm> alarmList;
    private AlarmActivity alarmActivity;
    private RecyclerView recyclerView;

    static class ViewHolder extends RecyclerView.ViewHolder {
        public View alarmView;
        public TextView textView;
        public Button editButton;
        public Button deleteButton;

        public ViewHolder(View view) {
            super(view);
            alarmView = view;
            textView = (TextView) view.findViewById(R.id.tv_alarm);
            editButton = (Button) view.findViewById(R.id.alarm_edit_button);
            deleteButton = (Button) view.findViewById(R.id.alarm_delete_button);
        }
    }

    public AlarmAdapter(AlarmActivity alarmActivity) {
        this.alarmActivity = alarmActivity;

        alarmList = new ArrayList<>();
        loadAlarmList();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmActivity.editAlarm(holder.getAdapterPosition(), alarmList.get(holder.getAdapterPosition()));
            }
        });
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAlarm(holder.getAdapterPosition());
            }
        });
        return  holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Alarm alarm = alarmList.get(position);
        holder.textView.setText(alarm.getTimeText());

        // 过期的单次闹钟显示为红色
        if (alarm.getMode() == Alarm.MODE_ONCE && System.currentTimeMillis() > alarm.getAlarmTime())
            holder.textView.setTextColor(Color.RED);
        else
            holder.textView.setTextColor(Color.BLACK);
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    /**
     * 注册闹钟到系统，由系统定时触发
     * @param alarm 注册的闹钟
     */
    private void registerAlarm(Alarm alarm) {
        AlarmManager alarmManager = (AlarmManager) alarmActivity.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(alarmActivity, AlarmReceiver.class);
        intent.putExtra("message", alarm.getMessage());
        switch (alarm.getMode()) {
            case Alarm.MODE_ONCE:
                alarmManager.set(AlarmManager.RTC_WAKEUP, alarm.getAlarmTime(),
                        PendingIntent.getBroadcast(alarmActivity, alarm.getRequestCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT));
                break;
            case Alarm.MODE_EveryDay:
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarm.getAlarmTime(), AlarmManager.INTERVAL_DAY,
                        PendingIntent.getBroadcast(alarmActivity, alarm.getRequestCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT));
                break;
        }
        Log.e("Register", new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date(alarm.getAlarmTime())) + " requrestCode=" + alarm.getRequestCode());
    }

    /**
     * 取消闹钟注册到系统的事件
     * @param alarm 取消的闹钟
     */
    private void cancelAlarm(Alarm alarm) {
        AlarmManager alarmManager = (AlarmManager) alarmActivity.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(PendingIntent.getBroadcast(alarmActivity, alarm.getRequestCode(), new Intent(alarmActivity, AlarmReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT));
    }

    /**
     * 新增一个闹钟
     * @param alarm 新的闹钟
     */
    public void addAlarm(Alarm alarm) {
        alarmList.add(alarm);
        notifyItemInserted(alarmList.size() - 1);
        recyclerView.scrollToPosition(alarmList.size() - 1);

        registerAlarm(alarm);
        saveAlarmList();
    }

    /**
     * 改变一个闹钟
     * @param position 被改变的闹钟的位置
     * @param alarm 改变后的闹钟
     */
    public void changeAlarm(int position, Alarm alarm) {
        Alarm previousAlarm = alarmList.set(position, alarm);
        notifyItemChanged(position);

        cancelAlarm(previousAlarm);
        registerAlarm(alarm);
        saveAlarmList();
    }

    /**
     * 移除一个闹钟，与其重复的所有闹钟也会被删除
     * @param position 闹钟的位置
     */
    private void removeAlarm(int position) {
        Alarm removedAlarm = alarmList.remove(position);
        notifyItemRemoved(position);

        cancelAlarm(removedAlarm);
        saveAlarmList();

        // 删除重复闹钟（因为这些闹钟已经不会响了）
        for (int i = 0; i < alarmList.size(); i++) {
            if (removedAlarm.getRequestCode() == alarmList.get(i).getRequestCode()) {
                removeAlarm(i);
                break;
            }
        }
    }

    /**
     * 保存闹钟列表，在闹钟列表有任何变化时调用
     */
    private void saveAlarmList() {
        SharedPreferences.Editor editor = alarmActivity.getSharedPreferences(AlarmActivity.class.getName(), Context.MODE_PRIVATE).edit();

        String data = null;
        if (alarmList.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Alarm alarm : alarmList) {
                stringBuilder.append(alarm.getAlarmTime()).append(Character.MAX_VALUE);
                stringBuilder.append(alarm.getMode()).append(Character.MAX_VALUE);
                stringBuilder.append(alarm.getMessage()).append(Character.MAX_VALUE);
            }
            data = stringBuilder.toString();
        }

        editor.putString("alarm", data);
        editor.apply();
    }

    /**
     * 载入闹钟列表，在本适配器实例化时（闹钟活动创建时）调用
     */
    private void loadAlarmList() {
        SharedPreferences sharedPreferences = alarmActivity.getSharedPreferences(AlarmActivity.class.getName(), Context.MODE_PRIVATE);
        String data = sharedPreferences.getString("alarm", null);
        if (data != null) {
            String[] alarmStrings = data.split(String.valueOf(Character.MAX_VALUE));
            for (int i = 0; i < alarmStrings.length; i += 3) {
                alarmList.add(new Alarm(Long.parseLong(alarmStrings[i]), Integer.parseInt(alarmStrings[i+1]), alarmStrings[i+2]));
            }
        }
    }

}
