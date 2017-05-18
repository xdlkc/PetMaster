package com.rdc.goospet.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.rdc.goospet.R;

public class ControlActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGHT = 300;
    boolean isFirstIn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences("first_pref",MODE_PRIVATE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (isFirstIn){
                    intent = new Intent(ControlActivity.this,MainActivity.class);
                }else {
                    intent = new Intent(ControlActivity.this, SettingActivity.class);
                }
                ControlActivity.this.startActivity(intent);
                ControlActivity.this.finish();
            }
        },  SPLASH_DISPLAY_LENGHT);
    }
}
