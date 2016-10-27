package com.cn21.speedtest.utils;

import android.os.SystemClock;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by luwy on 2016/8/25.
 * 更改设置系统时间
 */
public class SetTimeUtil {

    static final String TAG = "SystemDateTime";

    public static void setDateTime(int year, int month, int day, int hour, int minute) throws IOException, InterruptedException {

        //requestPermission();
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        long when = c.getTimeInMillis();
        if (when / 1000 < Integer.MAX_VALUE) {
            SystemClock.setCurrentTimeMillis(when);
        }
        long now = Calendar.getInstance().getTimeInMillis();
        if(now - when > 1000){
            throw new IOException("failed to set Date.");
        }

        }
    public  static void requestPermission()  {
        Process process ;
        DataOutputStream os = null;
        try {
            process = new ProcessBuilder("su").start();
            String cmd = "chmod 666 /dev/alarm";
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                }
            }
        }
    }
    }

