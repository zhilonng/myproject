package com.cn21.speedtest.view;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.cn21.speedtest.R;
import com.cn21.speedtest.service.CpuReaderService;
import com.cn21.speedtest.utils.LogUtil;
import com.cn21.speedtest.utils.ShellUtils;

/**
 * Created by huangzhilong on 16/8/22.
 */
public class FpsViewHolder extends RecyclerView.ViewHolder {
    private Switch sw_cputofps;
    private ProgressDialog progDialog = null;// 搜索时进度条
    public FpsViewHolder(final View itemView) {
        super(itemView);

        //注册广播接收器
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.cn21.speedtest.closeFpsDialog");
        itemView.getContext().registerReceiver(new MyBroadcastReciver(), intentFilter);
        sw_cputofps = (Switch)itemView.findViewById(R.id.sw_cputofps);
        sw_cputofps.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){

                    if(ShellUtils.checkRootPermission()) {
                        showProgressDialog();
                        LogUtil.e("CPU TO FPS");
                        FloatingWindows.amount = 0;
                        CpuReaderService.runningCpu = false;
                        CpuReaderService.runningFps = true;
                    }else {
                        showNeedRootDialog();
                    }
                }else {
                    LogUtil.e("FPS TO CPU");
                    FloatingWindows.amount = 0;
                    CpuReaderService.runningCpu = true;
                    CpuReaderService.runningFps = false;
                }
            }
        });
    }

    private void showNeedRootDialog() {
        android.support.v7.app.AlertDialog.Builder builder =
                new android.support.v7.app.AlertDialog.Builder(itemView.getContext());
        builder.setTitle(R.string.fps_needroot);
        builder.setMessage(R.string.fps_message);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }
    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(itemView.getContext());
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(false);
        progDialog.setMessage("正在跳转fps...");
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }
    private class MyBroadcastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("com.cn21.speedtest.closeFpsDialog")) {
                //String a = intent.getStringExtra("a");
                //Toast.makeText(itemView.getContext(),a,Toast.LENGTH_SHORT).show();
                dissmissProgressDialog();

            }
        }
    }
}
