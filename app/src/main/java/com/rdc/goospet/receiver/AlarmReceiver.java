package com.rdc.goospet.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by SC on 2017/5/15.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("message");
        Log.e("AlarmReceiver", "闹钟：" + message);
        Toast.makeText(context, "闹钟：" + message, Toast.LENGTH_LONG).show();
    }
}
