package com.cn21.speedtest.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.cn21.speedtest.R;
import com.cn21.speedtest.adapter.BrowseMemoryAdapter;
import com.cn21.speedtest.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by shenpeng on 2016/8/15.
 */
public class ViewMemory extends BaseActivity {
    private LineChartData data;
    private LineChartView mem_linechart;
    private boolean hasAxes = true;
    private boolean hasAxesNames = true;
    private boolean hasLines = true;
    private boolean hasPoints = false;
    private ValueShape shape = ValueShape.CIRCLE;
    private boolean isFilled = false;
    private boolean hasLabels = true;
    private boolean isCubic = false;
    private boolean hasLabelForSelected = false;
    private ActivityManager mActivityManager;
    List memSizeList;
    boolean Flag = true;
    static int PID;
    int i=0;

    //case 1:用于刷新图数据，@author:shenp
    //case 2：用于获取内存PID UID PACKGENAME数据,@author:shenp
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int msgId = msg.what;
            switch (msgId) {
                case 1:
                    generateData();
                    break;
//                case GET_PROCESS_FINISH:
//                    BrowseMemoryAdapter mprocessInfoAdapter = new BrowseMemoryAdapter(MemoryActivity.this, programeList);
//                    listViewProcess.setAdapter(mprocessInfoAdapter);
//                    tvTotalProcessNo.setText("当前系统进程共有：" + programeList.size() + "个");
//                    PID = programeList.get(0).getPid();
//
//                    break;
            }
        }
    };

    @Override
    protected void initView() {
        setContentView(R.layout.viewmemory_chart);
        mem_linechart=(LineChartView)findViewById(R.id.line);
        mem_linechart.setOnValueTouchListener(new ValueTouchListener());
        mem_linechart.computeScroll();
        mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
    }

    @Override
    protected void initData() {
        super.initData();
        memSizeList=new ArrayList();
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        PID=bundle.getInt("PID");
        LogUtil.e(PID+"");
        thread.start();
    }

    @Override
    protected void initEvent() {

    }

    //每1s获取一次内存，并刷新一次图片
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (Flag) {
                try {
                    memSizeList.add(getUniqueProcessInfo(PID) / 1024);
                    LogUtil.e(memSizeList.get(0)+"");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // TODO Auto-generated method stub
                myHandler.sendEmptyMessage(1);
            }

        }
    });

    //根据PID，获取特定应用的内存
    public float getUniqueProcessInfo(int pid) {

        int[] myMempid = new int[]{pid};
        Debug.MemoryInfo[] memoryInfos = mActivityManager.getProcessMemoryInfo(myMempid);
        float memSize = memoryInfos[0].dalvikPrivateDirty;
        return memSize;
    }

    //产生图片数据，传递给LineChartData
    private void generateData() {
        List<Line> lines = new ArrayList<Line>();
        List<PointValue> values = new ArrayList<PointValue>();
        for (int j = i; j < memSizeList.size(); ++j) {
            values.add(new PointValue(j, (float) memSizeList.get(j)));
            //LogUtil.e(""+memSize.get(j));
            //平移图片，@author：shenp
            if ((j - i) > 10) {
                i++;
            }
        }
        Line line = new Line(values);
        line.setColor(ChartUtils.COLORS[0]);
        line.setShape(shape);
        line.setCubic(isCubic);
        line.setFilled(isFilled);
        line.setHasLabels(hasLabels);
        line.setHasLabelsOnlyForSelected(hasLabelForSelected);
        line.setHasLines(hasLines);
        line.setHasPoints(hasPoints);
        line.setPointColor(ChartUtils.COLORS[(1) % ChartUtils.COLORS.length]);
        lines.add(line);
        data = new LineChartData(lines);
        if (hasAxes) {
            Axis axisX = new Axis();
            axisX.setMaxLabelChars(5);
            Axis axisY = new Axis().setHasLines(true);
            if (hasAxesNames) {
                axisX.setName("时间");
                axisY.setName("内存占用(MB)");
            }
            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }
        //开始绘图
        data.setBaseValue(Float.NEGATIVE_INFINITY);
        mem_linechart.setInteractive(true);
        mem_linechart.setZoomType(ZoomType.HORIZONTAL);
        mem_linechart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        mem_linechart.setValueSelectionEnabled(false);
        mem_linechart.setLineChartData(data);
        mem_linechart.setVisibility(View.VISIBLE);
    }

    //设置绘图监听事件
    private class ValueTouchListener implements LineChartOnValueSelectListener {

        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
            Toast.makeText(getContext(), "Selected: " + value, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub
        }

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
