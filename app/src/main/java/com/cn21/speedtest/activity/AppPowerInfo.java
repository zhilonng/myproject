package com.cn21.speedtest.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.cn21.speedtest.R;
import com.cn21.speedtest.model.DefaultApplication;
import com.cn21.speedtest.utils.ShellUtils;
import com.cn21.speedtest.utils.calculate.Calculate;
import com.cn21.speedtest.utils.calculate.LineChartDataCalculate;
import java.util.ArrayList;
import java.util.List;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

/**
 *
 * 数据来源：dumpsys batterystats工具
 * 应用即时耗电量的查看（大概2s内的耗电量）   安卓5.0以上  使用时不能外接充电源
 * Created by liangzhj  on 2016/8/25.
 */
public class AppPowerInfo extends BaseActivity {
    private TextView textView;
    private TextView textPackageName;
    private boolean flag = true;
    private String testPkgName;
    private List<List<Float>> myData = new ArrayList<>();

    private LineChartView chart;
    private LineChartData data;


    private List<String> cmds  = new ArrayList<String>();

    @Override
    protected void initView() {
        setContentView(R.layout.power_app_use);
        textView = (TextView) findViewById(R.id.app_power_use_info);
        textPackageName = (TextView) findViewById(R.id.text_pgk_name);
        myData.add(new ArrayList<Float>());
        myData.get(0).clear();

        textView.setText("当前该应用耗电速率：");
        chart = (LineChartView)findViewById(R.id.power_chart);
        chart.setOnValueTouchListener(new ValueTouchListener());
        chart.computeScroll();
        generateData();
    }

    @Override
    protected void initData() {
        super.initData();
        testPkgName = DefaultApplication.getPkgName();
        if (testPkgName == ""){
            testPkgName = "com.cn21.speedtest";
        }
        textPackageName.setText(testPkgName);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (flag){
                    cmds.clear();
                    cmds.add("dumpsys batterystats --reset");
                    cmds.add("dumpsys batterystats "+testPkgName);
                    ShellUtils.CommandResult commandResult = ShellUtils.execCommand(cmds,true,true);
                    if (commandResult.result ==0) {
                        float batteryUse = searchStrng(commandResult.successMsg);
                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putFloat("use",batteryUse);
                        bundle.putString("all",commandResult.successMsg);
                        message.what = 0;
                        message.setData(bundle);
                        myHandler.sendMessage(message);
                    }
                }
            }
        }).start();

    }

    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0 :
                    textView.setText("当前该应用耗电速率："+Float.toString(msg.getData().getFloat("use"))+"mah/s");
                    if(myData.get(0).size()<100)
                        myData.get(0).add(msg.getData().getFloat("use")*100000);
                    else{
                        myData.get(0).clear();
                        myData.get(0).add(msg.getData().getFloat("use")*100000);
                    }
                    generateData();
                    break;

            }
        }
    };

    @Override
    protected void initEvent() {

    }

    //数据检索功能
    private float searchStrng(String successInfo){
        List<String> uidPowerList = new ArrayList<>();
        uidPowerList.clear();
        float returnResult = 0;
        String beginStr = "Estimated power use";                     //应该耗电量信息标志
        String beginUid1 = "Uid";                                    //每条UID耗电量标志
        String beginUid2 = ": ";                                     //数据开始标志
        String endUid = " ";                                         //数据结束标志
        int begin,end;
        String beginIn = "";
        begin = successInfo.indexOf(beginStr);
        if (begin != -1)
            beginIn = successInfo.substring(begin);
        /**
         * 依次获取UID后面对应的耗电量
         * beginIn 为检索字符串段
         */
        for(;;) {
            if (beginIn.indexOf(beginUid1)!= -1) {
                begin = beginIn.indexOf(beginUid1);
                beginIn = beginIn.substring(begin+4);
                begin = beginIn.indexOf(beginUid2);
                beginIn = beginIn.substring(begin+2);
                Log.d("字段",beginIn);
                end = beginIn.indexOf(endUid);
                uidPowerList.add(beginIn.substring(0, end));
                beginIn = beginIn.substring(end);
            }else
                break;
        }

        for(int i = 0;i < uidPowerList.size();i++){                 //将List里面每个UID的数值转化为float再加起来
            returnResult = returnResult + Float.valueOf(uidPowerList.get(i));
        }
        myData.get(0).add(returnResult);


        return returnResult;
    }

    @Override
    protected void onDestroy() {
        flag = false;
        super.onDestroy();
    }

    private void generateData() {
        Calculate cal = new LineChartDataCalculate();
        data = cal.calculateLineChartData(myData,0,"时间","速率*10E-5");
        chart.setLineChartData(data);
    }

    private class ValueTouchListener implements LineChartOnValueSelectListener {

        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
            Toast.makeText(AppPowerInfo.this, "Selected: " + value, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub
        }
    }
}
