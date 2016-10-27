package com.cn21.speedtest.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.ListView;

import com.cn21.speedtest.R;
/**
 * Created by huangzhilong on 16/8/9.
 * content:获取进程pid
 */
public class GetProcessActivity extends BaseActivity {

    private  ListView listView;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_get_process);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.cn21.speedtest.getprocess");
        registerReceiver(new MyBroadcastReciver(), intentFilter);
    }

    @Override
    protected void initEvent() {
//        List<Programe> processInfoList = GetAppInfo.queryProcessInfo(GetProcessActivity.this);
//        ProcessAdapter adapter= new ProcessAdapter(GetProcessActivity.this, R.layout.process_item, processInfoList);
//        listView = (ListView)findViewById(R.id.process_list_view);
//        listView.setAdapter(adapter);
    }

    private class MyBroadcastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("com.cn21.speedtest.getprocess")) {
                //String author = intent.getStringExtra("pid");
                finish();
            }
        }
    }
}
