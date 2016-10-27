/*
 * Copyright (C) 2013 readyState Software Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cn21.speedtest.service;


import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;

import com.cn21.speedtest.R;
import com.cn21.speedtest.adapter.LogAdapter;
import com.cn21.speedtest.model.DefaultApplication;
import com.cn21.speedtest.model.LogLine;
import com.cn21.speedtest.model.Programe;
import com.cn21.speedtest.utils.Constants;
import com.cn21.speedtest.utils.LogReaderAsyncTask;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class LogService extends Service implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String ACTION_ROOT_FAILED = "com.readystatesoftware.ghostlog.ROOT_FAILED";

    private static final String TAG = "LogService";
    private static final int LOG_BUFFER_LIMIT = 2000;
    private static final SimpleDateFormat LOGCAT_TIME_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS");

    private static boolean sIsRunning = false;
    private int mTestAppPid;
    private String mTestAppPkg;
    private boolean mIntegrationEnabled = false;
    private String mLogLevel;
    private String mTagFilter;
    private String mKeyFilter;
    private boolean mAppFilter;
    private boolean mSavelog;
    private SharedPreferences mPrefs;
    private ListView mLogListView;
    private LogAdapter mAdapter;
    private BufferedWriter writelog;
    private LinkedList<LogLine> mLogBuffer;//先把日志全部读出来，放在此列表中
    private LinkedList<LogLine> mLogBufferFiltered;//再根据各种过滤条件结合后进行过滤，存放到此列表进行展示
    private Programe programe=new Programe();

    private Handler mLogBufferUpdateHandler = new Handler();
    private LogReaderAsyncTask mLogReaderTask;//异步任务读日志

    public static boolean isRunning() {
        return sIsRunning;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mPrefs.registerOnSharedPreferenceChangeListener(this);
        mLogLevel = mPrefs.getString(getString(R.string.pref_log_level), LogLine.LEVEL_VERBOSE);
        mTagFilter = mPrefs.getString(getString(R.string.pref_tag_filter), null);
        mKeyFilter = mPrefs.getString(getString(R.string.pref_key_filter), null);
        mAppFilter = mPrefs.getBoolean(getString(R.string.pref_app_filter), false);
        mSavelog=mPrefs.getBoolean(getString(R.string.pref_save_text),false);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sIsRunning = true;
        createSystemWindow();//打开获取日志窗口
        if (mAppFilter) {
            startAppfilter();//获取被测试app的pid号
        }
        startLogReader();//开始读取日志
          if(mSavelog){
              createSavelogdir();
          }
        return Service.START_STICKY;
    }

    private void createSavelogdir() {
        File sdcard= Environment.getExternalStorageDirectory();
        String savelogpath=sdcard.getPath()+"/Goastlogs";
        File Savelogpath=new File(savelogpath);
        if(!Savelogpath.exists()){
            Savelogpath.mkdir();
        }
        String date=LOGCAT_TIME_FORMAT.format(new Date());
        String Savelogfile=savelogpath+"/"+mTestAppPkg+date+".txt";
        File Savelog=new File(Savelogfile);
        try {
            FileWriter filerWriter = new FileWriter(Savelog, true);
            writelog=new BufferedWriter(filerWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void Savelog(String log) {
        File sdcard= Environment.getExternalStorageDirectory();
        String savelogpath=sdcard.getPath()+"/Goastlogs";
        File Savelogpath=new File(savelogpath);
        if(!Savelogpath.exists()){
            Savelogpath.mkdir();
        }
        try {
            String date=LOGCAT_TIME_FORMAT.format(new Date());
            String Savelogfile=savelogpath+"/"+mTestAppPkg+date+".txt";
            File Savelog=new File(Savelogfile);
            FileWriter filerWriter = new FileWriter(Savelog, true);
            writelog=new BufferedWriter(filerWriter);
           writelog.write(log);
            writelog.newLine();
        } catch (IOException e) {
            Log.e("e","can't open file");
        }
        }


    private void startAppfilter() {
        mTestAppPid= DefaultApplication.getPID();
        mTestAppPkg=DefaultApplication.getPkgName();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sIsRunning = false;
        mPrefs.unregisterOnSharedPreferenceChangeListener(this);
        stopLogReader();
        if (mIntegrationEnabled) {
            sendIntegrationBroadcast(false);
        }
        removeSystemWindow();
        if(writelog!=null) {
            try {
                writelog.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not implemented");
    }


    private void createSystemWindow() {
        final WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                //WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                0,
                PixelFormat.TRANSLUCENT
        );
        final LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        mLogListView = (ListView) inflator.inflate(R.layout.window_log, null);
        mLogBuffer = new LinkedList<LogLine>();
        mLogBufferFiltered = new LinkedList<LogLine>();
        mAdapter = new LogAdapter(this, mLogBufferFiltered);
        mLogListView.setAdapter(mAdapter);
        wm.addView(mLogListView, lp);
    }

    private void removeSystemWindow() {
        if (mLogListView != null && mLogListView.getParent() != null) {
            final WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            wm.removeView(mLogListView);
        }
    }

    private void sendIntegrationBroadcast(boolean enable) {
        Intent intent = new Intent(Constants.ACTION_COMMAND);
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.EXTRA_ENABLED, enable);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }

    private void startLogReader() {
        mLogBuffer = new LinkedList<LogLine>();
        mLogBufferFiltered = new LinkedList<LogLine>();
        mLogReaderTask = new LogReaderAsyncTask() {
            @Override
            protected void onProgressUpdate(LogLine... values) {
                // process the latest logcat lines
                for (LogLine line : values) {
                    updateBuffer(line);
                }
            }

            @Override
            protected void onPostExecute(Boolean ok) {
                if (!ok) {
                    // not root - notify activity
                    LocalBroadcastManager.getInstance(LogService.this)
                            .sendBroadcast(new Intent(ACTION_ROOT_FAILED));
                    // enable integration
                    mIntegrationEnabled = true;
                    sendIntegrationBroadcast(true);
                    updateBuffer(new LogLine("0 " + LOGCAT_TIME_FORMAT.format(new Date())
                            + " 0 0 " + getString(R.string.canned_integration_log_line)));
                }
            }
        };
        mLogReaderTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        Log.i(TAG, "Log reader task started");
    }

    private void stopLogReader() {
        if (mLogReaderTask != null) {
            mLogReaderTask.cancel(true);
        }
        mLogReaderTask = null;
        Log.i(TAG, "Log reader task stopped");
    }

    private void updateBuffer() {
        updateBuffer(null);
    }

    private void updateBuffer(final LogLine line) {
        mLogBufferUpdateHandler.post(new Runnable() {
            @Override
            public void run() {

                // update raw buffer
                if (line != null && line.getLevel() != null) {
                    mLogBuffer.add(line);
                }

                // update filtered buffer 根据过滤条件从mLogBuffer里面获取所需信息

                for (LogLine bufferedLine : mLogBuffer) {
                    if (!isFiltered(bufferedLine)) {//若不用过滤，保存到列表中
                        mLogBufferFiltered.add(bufferedLine);
                        if(mSavelog&&mAppFilter){
                            try {
                                writelog.write(bufferedLine.toString());
                                writelog.newLine();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }

                // update adapter若没有
                    mAdapter.setData(mLogBufferFiltered);


                // purge old entries
                while (mLogBuffer.size() > LOG_BUFFER_LIMIT) {
                    mLogBuffer.remove();
                }
                while (mLogBuffer.size() > LOG_BUFFER_LIMIT) {
                    mLogBuffer.remove();
                }
            }
        });
    }

    //判断是否过滤，返回true，表示过滤，否则不过滤
    private boolean isFiltered(LogLine line) {
        if (line != null) {
            if (mAppFilter && mTestAppPid != 0) {//只展示正在前台运行的app的日志
                if (line.getPid() != mTestAppPid) {
                    return true;//满足条件的就过滤掉
                }
            }
                if (!LogLine.LEVEL_VERBOSE.equals(mLogLevel)) {
                    if (line.getLevel() != null && !line.getLevel().equals(mLogLevel)) {
                        return true;
                    }
                }
                if (mTagFilter != null) {
                    if (line.getTag() == null || !line.getTag().toLowerCase().contains(mTagFilter.toLowerCase())) {
                        return true;
                    }
                }
                if (mKeyFilter != null) {
                    if (line.getMessage() == null || !line.getMessage().toLowerCase().contains(mKeyFilter.toLowerCase())) {
                        return true;
                    }
                }
                return false;
            } else {
                return true;
            }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_log_level))) {
            mLogLevel = mPrefs.getString(getString(R.string.pref_log_level), LogLine.LEVEL_VERBOSE);
            updateBuffer();
        }else if (key.equals(getString(R.string.pref_app_filter))) {
            mAppFilter = mPrefs.getBoolean(getString(R.string.pref_app_filter), false);
            if (mAppFilter) {
                startAppfilter();
                mLogBufferFiltered.clear();
            }
            updateBuffer();
        }
        else if (key.equals(getString(R.string.pref_tag_filter))) {
            mTagFilter = mPrefs.getString(getString(R.string.pref_tag_filter), null);
            updateBuffer();
        }else if (key.equals(getString(R.string.pref_key_filter))) {
            mKeyFilter = mPrefs.getString(getString(R.string.pref_key_filter), null);
            updateBuffer();
        }
        else if(key.equals(getString(R.string.pref_save_text))){
            mSavelog=mPrefs.getBoolean(getString(R.string.pref_save_text),false);
            if (mSavelog) {
                createSavelogdir();
            }
        }

    }

    public void onIntegrationDataReceived(String line) {
        if (mIntegrationEnabled) {
            updateBuffer(new LogLine(line));
        }
    }
}
