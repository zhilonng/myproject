package com.cn21.speedtest.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cn21.speedtest.R;
import com.cn21.speedtest.utils.User;

/**
 * Created by huangzhilong on 16/8/25.
 */
public class FloatingWindows extends LinearLayout {
    private TextView tv_pakage;
    private TextView tv_newfps;
    private TextView tv_frames;
    private TextView tv_jank;
    private TextView tv_avg;
    public static float amount =0;
    public FloatingWindows(Context context) {
        super(context);
        setOrientation(LinearLayout.VERTICAL);// 水平排列
        //设置宽高
        this.setLayoutParams( new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        View view = LayoutInflater.from(context).inflate(
                R.layout.floatingwindows, null);
        this.addView(view);
        tv_pakage = (TextView)view.findViewById(R.id.tv_floating_pakage);
        tv_newfps = (TextView)view.findViewById(R.id.tv_floating_fps);
        tv_frames = (TextView)view.findViewById(R.id.tv_floating_frames);
        tv_jank = (TextView)view.findViewById(R.id.tv_floating_jank);
        tv_avg = (TextView)view.findViewById(R.id.tv_floating_avg);
        //注册广播接收器
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.cn21.speedtest.CpuCallInfo");
        context.registerReceiver(new MyBroadcastReciver(), intentFilter);
    }
    /**
     * 广播接收器
     */
    private class MyBroadcastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("com.cn21.speedtest.CpuCallInfo")) {
                if(User.totalCpuRate.get(0).size()>0) {
                    if (User.totalCpuRate.get(0).size() < 100) {
                        amount += User.totalCpuRate.get(0).get(User.totalCpuRate.get(0).size() - 1);
                    } else {
                        amount = amount - User.inChangeFirstFps;
                        amount += User.totalCpuRate.get(0).get(99);
                    }
                }
                float avg = amount/User.totalCpuRate.get(0).size();
                String fps =intent.getStringExtra("fps");
                String frames = intent.getStringExtra("frames");
                String jank = intent.getStringExtra("jank");
                tv_newfps.setText(fps);
                tv_frames.setText(frames);
                tv_jank.setText(jank);
                tv_avg.setText(String.valueOf(avg));
                // tv_newfps.setText(User.totalCpuRate.get(0).get(User.totalCpuRate.get(0).size()-1).toString());
            }
        }
    }
}
