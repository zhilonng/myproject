package com.cn21.speedtest.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.cn21.speedtest.R;
import com.cn21.speedtest.utils.calculate.Calculate;
import com.cn21.speedtest.utils.calculate.LineChartDataCalculate;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by Administrator on 2016/8/15.
 */
public class MemoryView extends BaseActivity {
    LineChartView mem_linechart;
    TextView text_pid;
    private LineChartData data;
    ActivityManager activityManager;
    List<List<Float>> memSizeList = new ArrayList<>();
    static int PID;
    boolean Flag = true;
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int msgId = msg.what;
            switch (msgId) {
                case 1:
                    generateData(memSizeList);
                    break;
            }
        }
    };

    @Override
    protected void initView() {
        setContentView(R.layout.viewmemory_chart);
        mem_linechart = (LineChartView) findViewById(R.id.line);
        text_pid = (TextView) findViewById(R.id.text_pid);
        activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        memSizeList.add(new ArrayList<Float>());
    }

    @Override
    protected void initData() {
        super.initData();
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        PID = bundle.getInt("PID");
        String packageName = bundle.getString("packageName");
        text_pid.setText(packageName);
        thread.start();

    }

    @Override
    protected void initEvent() {

    }

    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (Flag) {
                if (memSizeList.get(0).size()<100) {
                    memSizeList.get(0).add(getUniqueProcessInfo(PID) / 1024);
                } else {
                    memSizeList.get(0).remove(0);
                    memSizeList.get(0).add(getUniqueProcessInfo(PID) / 1024);
                }
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                myHandler.sendEmptyMessage(1);
            }
        }
    });

    public float getUniqueProcessInfo(int pid) {
        int[] myMenPid = new int[]{pid};
        Debug.MemoryInfo[] memoryInfos = activityManager.getProcessMemoryInfo(myMenPid);
        float memSize = memoryInfos[0].dalvikPrivateDirty;
        return memSize;
    }

    //产生图片数据，传递给LineChartData
    public void generateData(List<List<Float>> memSizeList) {
        Calculate cal = new LineChartDataCalculate();
        data = cal.calculateLineChartData(memSizeList,0,"时间","内存占用 /M");
        mem_linechart.setLineChartData(data);
    }

    //关闭线程，回收资源
    @Override
    protected void onStop() {
        Flag = false;
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
        super.onStop();
    }
}