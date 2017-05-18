package com.rdc.goospet.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by SC on 2017/5/14.
 */

public class Alarm implements Serializable {

    private long alarmTime;
    private int mode;
    private String message;

    /**
     * 新建一个闹钟对象，不注册系统事件
     * @param alarmTime 闹钟时间
     * @param mode 闹钟模式
     * @param message 闹钟提醒消息
     */
    public Alarm(long alarmTime, int mode, String message) {
        this.alarmTime = alarmTime;
        this.mode = mode;
        this.message = message;
    }

    /**
     * 得到闹钟文本
     * @return 显示此闹钟时间的文本
     */
    public String getTimeText() {
        // 将毫秒时间转化成文本
        //Calendar calendar = Calendar.getInstance();
        //calendar.setTimeInMillis(alarmTime);
        //String timeText = String.format("%d月%d日 %d:%d", calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

        SimpleDateFormat dateFormat;
        switch (mode) {
            case MODE_ONCE:
                dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
                break;
            case MODE_EveryDay:
                dateFormat = new SimpleDateFormat("HH:mm");
                break;
            default:
                throw new IllegalStateException("Illegal mode value: " + mode);
        }

        return dateFormat.format(new Date(alarmTime));
    }

    /**
     * 得到此闹钟的请求码
     * @return 此闹钟的请求码
     */
    public int getRequestCode() {
        return (int)(alarmTime/1000/60) + mode + message.hashCode();
    }

    public long getAlarmTime() { return alarmTime; }
    public void setAlarmTime(long alarmTime) { this.alarmTime = alarmTime; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public int getMode() { return mode; }
    public void setMode(int mode) { this.mode = mode; }

    // 闹钟模式
    public static final int MODE_ONCE = 0;
    public static final int MODE_EveryDay = 1;

    /**
     * 所有模式中文名字字符串数组
     */
    public static final String[] modes = { "单次", "每天" };
}
