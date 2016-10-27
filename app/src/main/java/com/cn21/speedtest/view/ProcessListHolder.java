package com.cn21.speedtest.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.TextView;

import com.cn21.speedtest.R;
import com.cn21.speedtest.service.CpuReaderService;
import com.cn21.speedtest.utils.User;

/**
 * Created by huangzhilong on 16/8/10.
 */
public class ProcessListHolder {
    TextView tv_percent;
    TextView tv_pakage;
    int position;

    public ProcessListHolder(View convertView , String pakage , String pid , int position) {
        //super(convertView.getContext());
        this.position = position;
        tv_percent = (TextView)convertView.findViewById(R.id.tv_percent);
        tv_pakage = (TextView)convertView.findViewById(R.id.tv_project);
        tv_pakage.setText(pakage);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.cn21.speedtest.CpuChartAndInfo");
        convertView.getContext().registerReceiver(new MyBroadcastReciver(), intentFilter);
        if (CpuReaderService.runningCpu){
            tv_pakage.setTextColor(getColor((position) % 5));
            tv_percent.setTextColor(getColor((position) % 5));
        }else {
            tv_pakage.setTextColor(getColor(position % 5));
            tv_percent.setTextColor(getColor(position % 5));
        }
    }

    /**
     * 颜色选择
     * @param i
     * @return
     */
    private int getColor(int i) {
        switch (i) {
            case 0:
                return User.COLOR_RED;
            case 1:
                return User.COLOR_GREEN;
            case 2:
                return User.COLOR_ORANGE;
            case 3:
                return User.COLOR_BLUE;
            case 4:
                return User.COLOR_VIOLET;
            case 5:
                return User.DEFAULT_COLOR;
            default:
                break;
        }
        return User.COLOR_BLUE;
    }

    /**
     * 自定义广播
     */
    private class MyBroadcastReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case "com.cn21.speedtest.CpuChartAndInfo" :
                    if (User.processCpuRate[position]!=null){
                        if (position == CpuReaderService.cpu_id){
                            tv_percent.setText(User.processCpuRate[position].toString()+"%");
                       }else if (position == CpuReaderService.fps_id){
                            tv_percent.setText(User.processCpuRate[position].toString());
                       }else if (position == CpuReaderService.memory_id){
                            tv_percent.setText(User.processCpuRate[position].toString()+"MB");
                       }else if (position == CpuReaderService.power_id){
                            tv_percent.setText(User.processCpuRate[position].toString()+"mah/s");
                       }
                    }
                    break;
                default:break;
            }
        }
    }
}
