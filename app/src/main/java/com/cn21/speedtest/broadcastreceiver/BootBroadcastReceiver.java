package com.cn21.speedtest.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cn21.speedtest.model.SettingDatabase;
import com.cn21.speedtest.service.TrafficMonitorService;

/**
 * Created by lenovo on 2016/8/17.
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    private SettingDatabase dbAdapter;

    @Override
    public void onReceive(Context context, Intent intent) {
        dbAdapter = new SettingDatabase(context);
        dbAdapter.open();

        if ((intent.getAction().equals(ACTION))) {
            if(dbAdapter.checkautoStartup())
            {
                Intent myIntent = new Intent(context, TrafficMonitorService.class);
                context.startService(myIntent);
                Log.i("autostartup", "enable autostartup");
            }
            else
            {
                Log.i("autostartup", "disable autostartup");
            }
        }
        dbAdapter.close();
    }

}
