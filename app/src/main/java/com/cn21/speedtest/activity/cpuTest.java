package com.cn21.speedtest.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.cn21.speedtest.R;
import com.cn21.speedtest.utils.LogUtil;
import com.cn21.speedtest.utils.ShellUtils;

import java.util.ArrayList;
import java.util.List;

class cpuTest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpu_test);
        List<String> commnandList = new ArrayList();
        commnandList.add("adb shell dumpsys cpuinfo");
        ShellUtils.CommandResult response = ShellUtils.execCommand(commnandList, true);
        LogUtil.e(response.toString());
    }
    public void createTree(){

    }
}
