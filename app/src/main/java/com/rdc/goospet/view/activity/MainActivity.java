package com.rdc.goospet.view.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.rdc.goospet.R;
import com.rdc.goospet.adapter.RVMainAdapter;
import com.rdc.goospet.base.BaseActivity;
import com.rdc.goospet.listener.HidingScrollListener;
import com.rdc.goospet.presenter.MainPresenter;
import com.rdc.goospet.receiver.ComeWxMessage;
import com.rdc.goospet.receiver.MyMessage;
import com.rdc.goospet.service.FloatingPetService;
import com.rdc.goospet.utils.DimenUtils;
import com.rdc.goospet.view.vinterface.MainVInterface;

/**
 * 主界面
 */
public class MainActivity extends BaseActivity<MainVInterface, MainPresenter> implements MainVInterface, View.OnClickListener, MyMessage{

    private ComeWxMessage comeWxMessage;
    private MyMessage myMessage;
    private RecyclerView mRvMain;
    private FloatingActionButton mFABSetting;

    boolean isFirstIn = false;


    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter(this);
    }

    @Override
    protected int setContentViewById() {
        return R.layout.activity_main;
    }

    @Override
    protected void initAttributes() {

    }

    @Override
    protected void initView() {
        findAllViewById();
        initRv();
        mFABSetting.setOnClickListener(this);
        myMessage=new MainActivity();
        comeWxMessage=new ComeWxMessage(myMessage,this);
        comeWxMessage.toggleNotificationListenerService();
        comeWxMessage.openSetting();

//        toggleNotificationListenerService();
//        openSetting();
//        SharedPreferences preferences = getSharedPreferences("first_pref",MODE_PRIVATE);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putBoolean("isFirstIn",false);
//        editor.commit();
    }

    @Override
    public void comePhone() {
        Log.e("AAA","====回调中，收到来电===");
        //这里写调用让宠物换图标的方法
    }

    @Override
    public void comeShortMessage() {
        Log.e("AAA","====回调中，收到短信消息===");
        //这里写调用让宠物换图标的方法
    }

    @Override
    public void comeWxMessage() {
        Log.e("AAA","====回调中，收到微信消息===");
        //这里写调用让宠物换图标的方法

    }

    @Override
    public void comeQQmessage() {
        Log.e("AAA","====回调中，收到QQ消息===");
        //这里写调用让宠物换图标的方法

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        comeWxMessage.unRegistBroadcast();
    }

    public void openSetting(){
        if (!isEnabled()) {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        } else {
            Toast.makeText(this, "已开启服务权限", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(this.getContentResolver(),
                "enabled_notification_listeners");
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void toggleNotificationListenerService() {
        PackageManager pm =  getPackageManager();
        pm.setComponentEnabledSetting(
                new ComponentName(this,com.rdc.goospet.receiver.NotifyService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
/*
        pm.setComponentEnabledSetting(
                new ComponentName(this,com.rdc.goospet.receiver.NotifyService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);*/
    }

    /**
     * 加载R
     */
    private void initRv() {
        RVMainAdapter rvAdapter = mPresenter.getRVAdapter(this);
        mRvMain.setLayoutManager(new LinearLayoutManager(this));
        mRvMain.setAdapter(rvAdapter);
        mRvMain.addOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                hideFAB();
            }

            @Override
            public void onShow() {
                showFAB();
            }
        });
        ItemTouchHelper itemHelper = mPresenter.getItemTouchHelper(rvAdapter);
        itemHelper.attachToRecyclerView(mRvMain);
    }


    /**
     * 显示悬浮按钮
     */
    private void showFAB() {
        mFABSetting.animate().translationY(0).setInterpolator(new DecelerateInterpolator(1)).start();
    }

    /**
     * 隐藏悬浮按钮
     */
    private void hideFAB() {
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) mFABSetting.getLayoutParams();
        int fabBottomMargin = lp.bottomMargin;
        mFABSetting.animate().translationY(mFABSetting.getHeight() + fabBottomMargin + DimenUtils.getNavBarHeight(this) + DimenUtils.getStatusBarHeight(this)).
                setInterpolator(new AccelerateInterpolator(2)).start();
    }


    @Override
    protected void findAllViewById() {
        mRvMain = $(R.id.rv_main);
        mFABSetting = $(R.id.fab_setting);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rv_main:
                break;
            case R.id.fab_setting:
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivityWithAnim(intent);
                break;
//            case R.id.btn_show:
//                //启动悬浮pet
//                Intent intent = new Intent(MainActivity.this, FloatingPetService.class);
//                startService(intent);
//                Intent home = new Intent(Intent.ACTION_MAIN);
//                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                home.addCategory(Intent.CATEGORY_HOME);
//                startActivity(home);
//                break;
        }
    }

    @Override
    public void launchDesktopPet() {
        //启动悬浮pet
        Intent intent = new Intent(MainActivity.this, FloatingPetService.class);
        startService(intent);
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }
}
