package com.cn21.speedtest.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cn21.speedtest.R;
import com.cn21.speedtest.adapter.DatabaseAdapter;
import com.cn21.speedtest.model.SettingDatabase;
import com.cn21.speedtest.service.TrafficMonitorService;
import com.cn21.speedtest.utils.TrafficMonitoring;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

/**
 * Created by lenovo on 2016/8/17.
 */
public class NetworkMonitor extends Activity {

    TextView tv_gsmFreeTraffic, tv_gsmMonthTotal, tv_gsmMonthSend, tv_gsmMonthRecv, tv_gsmTodayTotal, tv_gsmTodaySend, tv_gsmTodayRecv, tv_wlanFreeTraffic, tv_wlanMonthTotal, tv_wlanMonthSend, tv_wlanMonthRecv, tv_wlanTodayTotal, tv_wlanTodaySend, tv_wlanTodayRecv;
    TextView board;
    Calendar currentCa;
    DatabaseAdapter dbAdapter;
    private SettingDatabase settingDbAdapter;
    String boardSrc = "https://dl.dropboxusercontent.com/u/168982523/NetworkMonitor/board.txt";
    StringBuffer news = new StringBuffer();
    boolean isRunning = true; //thread shared variable
    Thread downloadThread;
    Handler downloadHandler;
    String strTmp; // board message temp string

    Button setting;
    Button app_manager;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
            this.update();
            handler.postDelayed(this, 1000 * 3);//3s更新一次
        }

        void update() {

            currentCa = Calendar.getInstance();
            int year = currentCa.get(Calendar.YEAR);
            int month = currentCa.get(Calendar.MONTH) + 1;
            int day = currentCa.get(Calendar.DATE);

            tv_gsmFreeTraffic = (TextView) findViewById(R.id.gsm_free_traffic);
            tv_gsmMonthTotal = (TextView) findViewById(R.id.gsm_month_total);
            tv_gsmMonthSend = (TextView) findViewById(R.id.gsm_month_send);
            tv_gsmMonthRecv = (TextView) findViewById(R.id.gsm_month_recv);
            tv_gsmTodayTotal = (TextView) findViewById(R.id.gsm_today_total);
            tv_gsmTodaySend = (TextView) findViewById(R.id.gsm_today_send);
            tv_gsmTodayRecv = (TextView) findViewById(R.id.gsm_today_recv);
            tv_wlanFreeTraffic = (TextView) findViewById(R.id.wlan_free_traffic);
            tv_wlanMonthTotal = (TextView) findViewById(R.id.wlan_month_total);
            tv_wlanMonthSend = (TextView) findViewById(R.id.wlan_month_send);
            tv_wlanMonthRecv = (TextView) findViewById(R.id.wlan_month_recv);
            tv_wlanTodayTotal = (TextView) findViewById(R.id.wlan_today_total);
            tv_wlanTodaySend = (TextView) findViewById(R.id.wlan_today_send);
            tv_wlanTodayRecv = (TextView) findViewById(R.id.wlan_today_recv);
            String month3GTraffic;
            String day3GTraffic;
            String dayWIFITraffic;
            String monthWIFITraffic;
            Long GSMLimit, WIFILimit;
            long dup = dbAdapter.calculateUp(year, month, day, 1);
            long ddw = dbAdapter.calculateDw(year, month, day, 1);
            long dup_wifi = dbAdapter.calculateUp(year, month, day, 0);
            long ddw_wifi = dbAdapter.calculateDw(year, month, day, 0);



            month3GTraffic = TrafficMonitoring.convertTraffic(dbAdapter.calculateForMonth(year, month, 1));
            tv_gsmMonthTotal.setText(month3GTraffic);


            day3GTraffic = TrafficMonitoring.convertTraffic(dup + ddw);
            tv_gsmTodayTotal.setText(day3GTraffic);


            monthWIFITraffic = TrafficMonitoring.convertTraffic(dbAdapter.calculateForMonth(year, month, 0));
            tv_wlanMonthTotal.setText(monthWIFITraffic);


            dayWIFITraffic = TrafficMonitoring.convertTraffic(dup_wifi + ddw_wifi);
            tv_wlanTodayTotal.setText(dayWIFITraffic);



            tv_gsmMonthSend.setText(TrafficMonitoring.convertTraffic(dbAdapter.calculateUpForMonth(year, month, 1)));

            tv_gsmMonthRecv.setText(TrafficMonitoring.convertTraffic(dbAdapter.calculateDnForMonth(year, month, 1)));

            tv_gsmTodaySend.setText(TrafficMonitoring.convertTraffic(dup));

            tv_gsmTodayRecv.setText(TrafficMonitoring.convertTraffic(ddw));

            tv_wlanMonthSend.setText(TrafficMonitoring.convertTraffic(dbAdapter.calculateUpForMonth(year, month, 0)));

            tv_wlanMonthRecv.setText(TrafficMonitoring.convertTraffic(dbAdapter.calculateDnForMonth(year, month, 0)));

            tv_wlanTodaySend.setText(TrafficMonitoring.convertTraffic(dup_wifi));

            tv_wlanTodayRecv.setText(TrafficMonitoring.convertTraffic(ddw_wifi));


            GSMLimit = settingDbAdapter.checkGSMLimit();

            String tempString[];
            if (GSMLimit == 0) {
                tv_gsmFreeTraffic.setText("None");
                tv_gsmFreeTraffic.setTextColor(getResources().getColor(R.color.black));
            } else {
                double remain3G;
                if (month3GTraffic.contains("KB")) {
                    tempString = month3GTraffic.split("KB");
                    double temp = Double.valueOf(tempString[0]);
                    remain3G = new BigDecimal(GSMLimit * 1000 - temp).divide(new BigDecimal(1000), 2, 1).doubleValue();
                } else if (month3GTraffic.contains("MB")) {
                    tempString = month3GTraffic.split("MB");
                    double temp = Double.valueOf(tempString[0]);
                    remain3G = GSMLimit - temp;
                } else {
                    tempString = month3GTraffic.split("GB");
                    double temp = Double.valueOf(tempString[0]);
                    remain3G = new BigDecimal(GSMLimit - temp * 1000).doubleValue();
                }

                if (remain3G < 0) {
                    remain3G = 0;
                    tv_gsmFreeTraffic.setTextColor(Color.RED);
                } else {
                    tv_gsmFreeTraffic.setTextColor(getResources().getColor(R.color.black));
                }
                tv_gsmFreeTraffic.setText(remain3G + "MB");
            }


            WIFILimit = settingDbAdapter.checkWIFILimit();

            String tempString2[];
            if (WIFILimit == 0) {
                tv_wlanFreeTraffic.setText("None");
                tv_wlanFreeTraffic.setTextColor(getResources().getColor(R.color.black));
            } else {
                double remainWIFI;
                if (monthWIFITraffic.contains("KB")) {
                    tempString2 = monthWIFITraffic.split("KB");
                    double temp = Double.valueOf(tempString2[0]);
                    remainWIFI = new BigDecimal(WIFILimit * 1000 - temp).divide(new BigDecimal(1000), 2, 1).doubleValue();
                } else if (monthWIFITraffic.contains("MB")) {
                    tempString2 = monthWIFITraffic.split("MB");
                    double temp = Double.valueOf(tempString2[0]);
                    remainWIFI = WIFILimit - temp;
                } else {
                    tempString2 = monthWIFITraffic.split("GB");
                    double temp = Double.valueOf(tempString2[0]);
                    remainWIFI = new BigDecimal(WIFILimit - temp * 1000).doubleValue();
                }

                if (remainWIFI < 0) {
                    remainWIFI = 0;
                    tv_wlanFreeTraffic.setTextColor(Color.RED);
                } else {
                    tv_wlanFreeTraffic.setTextColor(getResources().getColor(R.color.black));
                }
                tv_wlanFreeTraffic.setText(remainWIFI + "MB");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_monitor);

        Intent intent = new Intent(NetworkMonitor.this, TrafficMonitorService.class);
        startService(intent);

        setting = (Button) findViewById(R.id.setting);
        app_manager = (Button) findViewById(R.id.app_Manager);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NetworkMonitor.this, Setting.class);
                startActivity(intent);
            }
        });
        app_manager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NetworkMonitor.this, NetList.class);
                startActivity(intent);
            }
        });
        downloadHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        board.setText("Loading board message failed.");
                        break;
                    case 2:
                        board.setText(strTmp);
                        break;
                }
            }

        };

        dbAdapter = new DatabaseAdapter(this);
        dbAdapter.open();
        settingDbAdapter = new SettingDatabase(this);
        settingDbAdapter.open();

        handler.post(runnable);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        board = (TextView) findViewById(R.id.board);
        downloadThread = new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (isRunning) {
                    strTmp = getTextFromUrl(boardSrc);
                    Message msg = new Message();
                    if (strTmp == null) {
                        msg.what = 1;
                        downloadHandler.sendMessage(msg);
                    } else {
                        msg.what = 2;
                        downloadHandler.sendMessage(msg);
                    }
                }
            }
        });
        downloadThread.start();
    }


    public String getTextFromUrl(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();

            byte[] data = new byte[1024];
            int len = 0;
            while ((len = input.read(data)) > 0) {
                news.append(new String(data));
            }
            return news.substring(0);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        isRunning = false;
        downloadThread.interrupt();
    }
}