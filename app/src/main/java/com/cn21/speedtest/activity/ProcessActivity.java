package com.cn21.speedtest.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.cn21.speedtest.R;
import com.cn21.speedtest.model.DefaultApplication;

/**
 * 应用、进程管理
 * Created by 梁照江 on 2016/8/9.
 */
public class ProcessActivity extends BaseActivity{
    private FragmentManager fragmentManager;
    private Fragment[] fragments;
    private FragmentTransaction fragmentTransaction;
    private RadioGroup radioGroup;
    private RadioButton rbRunning, rbSys,rbUser;
    private ProgressDialog progressDialog;
    private MyReceiver myReceiver;
    @Override
    protected void initData() {
        super.initData();
        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.cn21.loadfinish");
        registerReceiver(myReceiver,filter);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.process_activity);
        if (DefaultApplication.loadFinish ==false)
            showIndeterminate();
        fragments = new Fragment[3];
        fragmentManager = getFragmentManager();
        fragments[0] = fragmentManager.findFragmentById(R.id.fragment_user);
        fragments[1] = fragmentManager.findFragmentById(R.id.fragment_sys);
        fragments[2] = fragmentManager.findFragmentById(R.id.fragment_running);
        fragmentTransaction = fragmentManager.beginTransaction().hide(fragments[0])
                .hide(fragments[1]).hide(fragments[2]);
        fragmentTransaction.show(fragments[2]).commit();
    }



    @Override
    protected void initEvent() {
        radioGroup = (RadioGroup) findViewById(R.id.tab_menu);
        rbRunning = (RadioButton) findViewById(R.id.chose_running_app);
        rbSys = (RadioButton) findViewById(R.id.chose_sys_app);
        rbUser = (RadioButton) findViewById(R.id.chose_user_app);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                fragmentTransaction = fragmentManager.beginTransaction().hide(fragments[0])
                        .hide(fragments[1]).hide(fragments[2]);
                switch (i){
                    case R.id.chose_user_app:
                        fragmentTransaction.show(fragments[0]).commit();break;
                    case R.id.chose_sys_app:
                        fragmentTransaction.show(fragments[1]).commit();break;
                    case R.id.chose_running_app:
                        fragmentTransaction.show(fragments[2]).commit();break;
                    default:
                        break;
                }
            }
        });
    }

    public void showIndeterminate(){
        progressDialog = new ProgressDialog(ProcessActivity.this);
    //    progressDialog.setTitle("加载中");
        progressDialog.setMessage("正在获取当前手机应用，请稍候");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    public class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("com.cn21.loadfinish")){
                Log.d("接收广播","done");
                fragmentTransaction = fragmentManager.beginTransaction().hide(fragments[0])
                        .hide(fragments[1]).hide(fragments[2]);
                fragmentTransaction.show(fragments[2]).commit();
                progressDialog.dismiss();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }
}
