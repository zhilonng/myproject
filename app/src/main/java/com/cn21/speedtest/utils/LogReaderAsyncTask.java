package com.cn21.speedtest.utils;

import android.os.AsyncTask;
import android.os.Build;

import com.cn21.speedtest.model.LogLine;
import com.cn21.speedtest.utils.LogcatHelper;
import com.cn21.speedtest.utils.RuntimeHelper;
import com.cn21.speedtest.utils.SuperUserHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class LogReaderAsyncTask extends AsyncTask<Void, LogLine, Boolean> {

    @Override
    protected Boolean doInBackground(Void... voids) {
        //若sdk版本大于4.1且手机没有获取root权限，则返回false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN//JELLY_BEAN表示android4.1版本
                && !SuperUserHelper.requestRoot()) {
            return false;
        }
        //若获取了root权限
        Process process = null;
        BufferedReader reader = null;
        boolean ok = true;

        try {

            // clear buffer first
            clearLogcatBuffer();

            process = LogcatHelper.getLogcatProcess(LogcatHelper.BUFFER_MAIN);
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()), 8192);

            while (!isCancelled()) {
                final String line = reader.readLine();
                if (line != null) {
                    // publish result
                    publishProgress(new LogLine(line));
                }
            }

        } catch (IOException e) {

            e.printStackTrace();
            ok = false;

        } finally {



            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN
                    && reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return ok;

    }

    private void clearLogcatBuffer() {
        try {
            Process process = RuntimeHelper.exec(new ArrayList<String>(Arrays.asList("logcat", "-c")));
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

