package com.cn21.speedtest.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.cn21.speedtest.R;
import com.cn21.speedtest.activity.MemoryActivity;
import com.cn21.speedtest.utils.User;

/**
 * Created by huangzhilong on 16/9/12.
 */
public class AppInfoViewHolder extends RecyclerView.ViewHolder{
    CardView cd_process;
    TextView tv_process,tv_pid;
    public AppInfoViewHolder(final View itemView) {
        super(itemView);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.cn21.speedtest.MemoryAct2AppHolder");
        itemView.getContext().registerReceiver(new MyBroadcastReciver(), intentFilter);
        cd_process = (CardView)itemView.findViewById(R.id.card_process);
        tv_process = (TextView)itemView.findViewById(R.id.tv_project1);
        tv_pid = (TextView)itemView.findViewById(R.id.tv_pid1);
        cd_process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(itemView.getContext(), MemoryActivity.class);
                intent.putExtra("Cpu","true");
                itemView.getContext().startActivity(intent);
            }
        });
    }
    private class MyBroadcastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("com.cn21.speedtest.MemoryAct2AppHolder")) {
                tv_process.setText(User.inChoosePakageName);
                tv_process.setTextColor(Color.BLUE);
                tv_pid.setText("pid:"+User.inChooseProcessPid);
                tv_pid.setTextColor(Color.BLUE);
            }
        }
    }
}
