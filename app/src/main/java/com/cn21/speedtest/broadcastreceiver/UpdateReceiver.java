package com.cn21.speedtest.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.cn21.speedtest.R;

/**
 * Created by shenpeng on 2016/8/10.
 */
public class UpdateReceiver extends BroadcastReceiver {
    private boolean isServiceStop = false;
    private Button btnTest;

    @Override
    public void onReceive(Context context, Intent intent) {
        isServiceStop = intent.getExtras().getBoolean("isServiceStop");
        if (isServiceStop) {
            btnTest.setText(R.string.start_test);
        }
    }
}