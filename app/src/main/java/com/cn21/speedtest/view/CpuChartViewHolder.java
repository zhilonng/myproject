package com.cn21.speedtest.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cn21.speedtest.R;
import com.cn21.speedtest.service.CpuReaderService;
import com.cn21.speedtest.utils.LogUtil;
import com.cn21.speedtest.utils.User;
import com.cn21.speedtest.utils.calculate.Calculate;
import com.cn21.speedtest.utils.calculate.LineChartDataCalculate;

import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by huangzhilong on 16/8/9.
 * content：绘制cpu图表
 */
public class CpuChartViewHolder extends RecyclerView.ViewHolder {

    private LineChartView cpu_linechart;
    private LineChartData data;
    private static int time_interval=1;
    /**
     * 操作指定view
     * @param itemView
     */
    public CpuChartViewHolder(View itemView) {
        super(itemView);
        cpu_linechart = (LineChartView)itemView.findViewById(R.id.cpu_linechart);
        cpu_linechart.setOnValueTouchListener(new ValueTouchListener());
        cpu_linechart.computeScroll();

        //首次打开，清空后台
        if (User.isFirstOpenCpu != true) {
            for (int i=0;i<User.totalCpuRate.size();i++){
                User.totalCpuRate.get(i).clear();
                CpuReaderService.runningCpu = false;
                CpuReaderService.runningFps = false;
                CpuReaderService.runningMemory = false;
                CpuReaderService.runningPower = false;
                User.isFirstOpenCpu = true;
            }
        }
        User.isFirstOpenCpu = false;
        //chart的点击事件,设置cpu数据获取频率
        cpu_linechart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //time_interval = 1;
                if(CpuReaderService.isbegin)
                showDialog();
            }
        });

        //注册广播接收器
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.cn21.speedtest.CpuChartAndInfo");
        itemView.getContext().registerReceiver(new MyBroadcastReciver(), intentFilter);
    }

    /**
     * 提示框
     */
    private void showDialog() {
        LayoutInflater factory = LayoutInflater.from(itemView.getContext());
        View view =factory.inflate(R.layout.alertdialog_view,null);
        android.support.v7.app.AlertDialog.Builder builder =
                new android.support.v7.app.AlertDialog.Builder(itemView.getContext());
        builder.setView(view);
        final SeekBar timeSeek = (SeekBar)view.findViewById(R.id.seekBar);
        final TextView tv_time = (TextView)view.findViewById(R.id.tv_time);
        timeSeek.setMax(4);
        LogUtil.e(String.valueOf(time_interval-1));
        timeSeek.setProgress(time_interval - 1);
        tv_time.setText(String.valueOf(time_interval)+"s");
        timeSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_time.setText((progress+1)+"s");
                time_interval = progress+1;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                User.thread_time_interval = time_interval*1000;
                for (int i=0;i<User.totalCpuRate.size();i++){
                    User.totalCpuRate.get(i).clear();
                }
            }
        });
        builder.show();
    }

    /**
     * 接收CpuReaderService发送的广播
     */
    private class MyBroadcastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("com.cn21.speedtest.CpuChartAndInfo")) {
                generateData();
            }

        }
    }

    /**
     * 生成图表数据，绘图
     */
    private void generateData(){
            if (User.totalCpuRate != null) {
                Calculate cal = new LineChartDataCalculate();
                data = cal.calculateLineChartData(User.totalCpuRate,0,"时间"," ");
                cpu_linechart.setLineChartData(data);
                }
            }

    private class ValueTouchListener implements LineChartOnValueSelectListener {

        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
            Toast.makeText(itemView.getContext(), "Selected: " + value, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub
        }

    }
}
