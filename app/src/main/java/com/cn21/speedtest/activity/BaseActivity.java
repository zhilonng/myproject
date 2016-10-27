package com.cn21.speedtest.activity;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;


/**
 * 基类activity，展示用户界面
 * <p/>
 * Copyright (c) 2016. shenpeng (sx_shenp@corp.21cn.com)
 *
 * @version 1.0
 *          创建时间：2016/8/1.
 *          创建人：申鹏
 */
public abstract class BaseActivity extends Activity {

    protected Context mContext = null;

    //    private Handler myHandler=new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            int msgId=msg.what;
//            switch (msgId){
//                case 1:
//
//            }
//        }
//    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        initView();
//        loadData();
        initData();
        initEvent();

//        initData();

//        Thread thread=new Thread(new Runnable() {
//            @Override
//            public void run() {
//                initData();
//                Message message=new Message();
//                message.what=1;
//                myHandler.sendMessage(message);
//            }
//        });
//        thread.start();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    /**
     * activity销毁时，要进行资源回收
     */
    protected void onDestroy() {
        super.onDestroy();
    }

    ;

    /**
     * 初始化布局，子类必须实现
     */
    protected abstract void initView();

    /**
     * 初始化事件
     */
    protected abstract void initEvent();

    /**
     * 初始化数据，子类可以不实现
     */


    protected void initData() {
    }

    /**
     * 创建上下文对象
     */
    public Context getContext() {
        return mContext;
    }

}
