package com.cn21.speedtest.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.cn21.speedtest.R;
import com.cn21.speedtest.utils.User;

/**
 * Created by huangzhilong on 16/8/22.
 */
public class CpuCalInfoViewHolder extends RecyclerView.ViewHolder {
    private TextView tv_newfps;
    private TextView tv_frames;
    private TextView tv_jank;
    private TextView tv_avg;
    private static float amount =0;
    public CpuCalInfoViewHolder(View itemView) {
        super(itemView);
        tv_newfps = (TextView)itemView.findViewById(R.id.tv_newfps);
        tv_frames = (TextView)itemView.findViewById(R.id.tv_frames);
        tv_jank = (TextView)itemView.findViewById(R.id.tv_jank);
        tv_avg = (TextView)itemView.findViewById(R.id.tv_avg);
        //注册广播接收器
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.cn21.speedtest.CpuCallInfo");
        itemView.getContext().registerReceiver(new MyBroadcastReciver(), intentFilter);
    }

    /**
     * 广播接收器
     */
    private class MyBroadcastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("com.cn21.speedtest.CpuCallInfo")) {
                if (User.totalCpuRate.get(0).size()<100){
                    amount += User.totalCpuRate.get(0).get(User.totalCpuRate.get(0).size()-1);
                }else {
                    amount = amount - User.inChangeFirstFps;
                    amount +=User.totalCpuRate.get(0).get(99);
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
