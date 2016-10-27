package com.cn21.speedtest.service;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.cn21.speedtest.R;
import com.cn21.speedtest.utils.ShellUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by luwy on 2016/8/29.
 */
public class ScreenShotService extends Service {

    SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    String strFileFullPath;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0 : Toast.makeText(getApplicationContext(),"截图成功，已保存至Files文件夹中",Toast.LENGTH_SHORT).show();
                    break;
                default: Toast.makeText(getApplicationContext(),"截图失败,错误码:"+Integer.toString(msg.what),Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
            String date=format.format(new Date());
            strFileFullPath = "/data/data/com.cn21.speedtest/files/"+"screenshot_"+date+".png";
            Thread thread= new Thread(new Runnable() {
                @Override
                public void run() {
                    ShellUtils.CommandResult result = ShellUtils.execCommand("screencap -p "+strFileFullPath,true,true );
                    handler.sendEmptyMessage(result.result);
                }
            });
            thread.start();

            //strFileFullPath="/sdcard/"+"screenshot_"+date+".png";
        /*
            Process process ;
            DataOutputStream os ;
            try {
                process = Runtime.getRuntime().exec("su");
                os = new DataOutputStream(process.getOutputStream());
                os.writeBytes("screencap -p " + strFileFullPath+"\n");
                os.writeBytes("exit\n");
                os.flush();
                os.close();
            }
            catch (Exception e){
            }
            finally{
            check();
        }
        */
           return Service.START_NOT_STICKY;
    }

    private void check() {
        while(true){
            File file=new File(strFileFullPath);
            if(  file.exists()){
                NotificationManager nm=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                Notification.Builder builder=new Notification.Builder(this);
                builder.setContentTitle("已抓取屏幕截图").setContentText("触摸可查看截图");
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file),"image/png");
                PendingIntent pendingIntent= PendingIntent.getActivity(this,0,intent,0);
                builder.setContentIntent(pendingIntent);
                nm.notify(0,builder.build());
                break;
            }


        }
    }
}


