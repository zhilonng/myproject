package com.cn21.speedtest.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.cn21.speedtest.R;
import com.cn21.speedtest.adapter.DatabaseAdapter;
import com.cn21.speedtest.model.SettingDatabase;
import com.cn21.speedtest.utils.TrafficMonitoring;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by lenovo on 2016/8/17.
 */
public class TrafficMonitorService extends Service {

    private DatabaseAdapter dbAdapter;
    private SettingDatabase settingDb;
    private Handler handler = new Handler() ;
    private Handler checkFloatHandler = new Handler();
    private Handler checkStatHandler = new Handler();
    private Handler flowRemainHandler = new Handler();
    private boolean lastStat=true;
    private Handler mHandler;
    private long mobileRx = 0 , mobileTx = 0 ,totalRx = 0 , totalTx = 0 ,wifiRx = 0 ,wifiTx = 0;
    private long old_mobileRx = 0 ,old_mobileTx = 0  ,old_wifiRx = 0, old_wifiTx= 0 ;
    private long mrx = 0,mtx = 0 , wrx = 0 ,wtx = 0 ;
    private long traffic_data=0,old_totalRx=0,old_totalTx=0;
    private long mobileRx_all= 0 ,mobileTx_all= 0 ,wifiRx_all = 0,wifiTx_all = 0 ;
    private static int remainTraffic=10;
    private Intent in = new Intent("Runnable");
    int threadNum; // �u�{��
    static int count = 12;
    NetworkInfo nwi;
    private boolean killRunnable=false;

    public IBinder onBind(Intent intent) {
        return null;
    }
    //ArrayList<AppInfo> appList = new ArrayList<AppInfo>();

    public void onCreate() {

        settingDb = new SettingDatabase(this);
        settingDb.open();
        if(!settingDb.check())
        {
            settingDb.insertData(1, 1, 1, (long)0,(long)0);
        }
        settingDb.updateStartStatistic(1);
        settingDb.close();

        old_mobileRx = TrafficStats.getMobileRxBytes();
        old_mobileTx = TrafficStats.getMobileTxBytes();

        // ���������������B�o�e�ƾ��`�q
        totalRx = TrafficStats.getTotalRxBytes();
        totalTx = TrafficStats.getTotalTxBytes();

        // �p��WiFi���������B�o�e�ƾ��`�q
        old_wifiRx = totalRx - old_mobileRx;
        old_wifiTx = totalTx - old_mobileTx;

        handler.post(thread);

        super.onCreate();
//
//        final FloatWindow view = new FloatWindow(this);
//        view.show();
        checkFloatHandler.post(checkFloatWindowThread);
        checkStatHandler.post(checkStatThread);
        flowRemainHandler.post(flowRemainThread);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {

                    try {
                        switch(isCMWAP(getApplicationContext()))
                        {
//                            case 0:
//                                view.tv_show.setText("W " + msg.obj);
//                                break;
//                            case 1:
//                                view.tv_show.setText("G " + msg.obj);
//                                break;
//                            case 2:
//                                view.tv_show.setText("E " + msg.obj);
//                                break;
//                            default:
//                                view.tv_show.setText("!NONET ");
//                                //view.tv_show.setText("! "+ msg.obj); //��ե�
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    //view.tv_show.setText((CharSequence) msg.obj);

                }
                else if(msg.what == 2)
                {
                    if(msg.obj=="false")
                    {
//                        view.closeWindow();
                    }
                    else
                    {   Log.i("visible","set visible");
//                        view.view.setVisibility(View.VISIBLE);
                    }
                }
                else if(msg.what == 3)
                {
                    if(msg.obj=="false")
                    {	Log.i("startStat","Stop Stat");
                        handler.removeCallbacks(thread);
                        lastStat=false;
                    }
                    else if(lastStat==false)
                    {	Log.i("startStat","Start Stat");
                        handler.post(thread);
                        lastStat=true;
                    }
                }
                else if(msg.what == 4)
                {
                    Log.i("alert","remainalert");
//                    view.rl.setBackgroundColor(Color.RED);

                    int s_nNotificationId = 0;

                    Intent intentNotify = new Intent();

                    // Prepare the pending-intent for the notification,
                    //  and let it trigger intentNotify, which does nothing
                    PendingIntent intentContent = PendingIntent.getActivity(TrafficMonitorService.this, 0, intentNotify, 0);

                    // Create notification.
                    //  The string will be shown on top bar of device.
                    Notification notification = new Notification(R.drawable.ic_launcher, getString(R.string.app_name) + ": " + msg.obj+"�y�q�Y�N���j����", System.currentTimeMillis());

                    // The notification will disappear automatically when user clicks it
                    notification.flags |= Notification.FLAG_AUTO_CANCEL;

                    // Here sets the title and message of the notification,
                    //  which will be shown in the dropdown notification list.
                    //notification.setLatestEventInfo(TrafficMonitorService.this, getString(R.string.app_name), msg.obj+"�y�q�Y�N���j����", intentContent);



                    // Send out notify
                    NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(s_nNotificationId++, notification);

                }
                else if(msg.what == 5)
                {
//                    view.rl.setBackgroundColor(Color.BLACK);
                }
            }
        };
    }



    Runnable thread = new Runnable(){

        public void run() {
            // TODO Auto-generated method stub

            dbAdapter = new DatabaseAdapter(TrafficMonitorService.this);
            dbAdapter.open();

//			 try {
//				Thread.sleep(5000);
//			} catch (InterruptedException e) {}

            // �I�ܱҰʾ���
            // ���ʺ��������B�o�e�ƾ��`�q�A��쬰byte�A�H�U�P�W
            mobileRx = TrafficStats.getMobileRxBytes();
            mobileTx = TrafficStats.getMobileTxBytes();
            // ���������������B�o�e�ƾ��`�q
            totalRx = TrafficStats.getTotalRxBytes();
            totalTx = TrafficStats.getTotalTxBytes();
            // �p��WiFi���������B�o�e�ƾ��`�q
            wifiRx = totalRx - mobileRx;
            wifiTx = totalTx - mobileTx;
            if (mobileRx == -1 && mobileTx == -1) {
                in.putExtra("mobileRx", "No");
                in.putExtra("mobileTx", "No");
            }
            else {
                mrx = (mobileRx - old_mobileRx); // �o������GPRS�U��y�q
                old_mobileRx = mobileRx;
                mtx = (mobileTx - old_mobileTx) ; // �o������GPRS�W��y�q
                old_mobileTx = mobileTx;

                mrx = (long) ((float) (Math.round(mrx * 100.0)) / 100);
                mtx = (long) ((float) (Math.round(mtx * 100.0)) / 100);

                in.putExtra("mobileRx", mrx / 1024 + "KB");
                in.putExtra("mobileTx", mtx/ 1024 + "KB");
                traffic_data=mrx+mtx;
            }
            if (wifiRx == -1 && wifiTx == -1) {
                in.putExtra("wifiRx", "No");
                in.putExtra("wifiTx", "No");
            }
            else {
                wrx = (wifiRx - old_wifiRx);
                old_wifiRx = wifiRx;
                wtx = (wifiTx - old_wifiTx);
                old_wifiTx = wifiTx;
                wrx = (long) ((float) (Math.round(wrx * 100.0)) / 100);
                wtx = (long) ((float) (Math.round(wtx * 100.0)) / 100);
                in.putExtra("wifiRx", wrx / 1024 + "KB");
                in.putExtra("wifiTx", wtx + "KB");
                traffic_data+=wrx+wtx;
            }
            Date date = new Date() ;
            mobileRx_all += mrx;
            if(mobileRx_all<0)
                mobileRx_all -= mrx;
            mobileTx_all += mtx;
            if(mobileTx_all<0)
                mobileTx_all -= mtx;
            wifiTx_all += wtx;
            if(wifiTx_all<0)
                wifiTx_all -= wtx;
            wifiRx_all += wrx;
            if(wifiRx_all<0)
                wifiRx_all -= wrx;
            if(count==12){


                if(mobileTx_all!=0||mobileRx_all!=0){
                    Cursor checkMobile = dbAdapter.check(1, date);//1 �� GPRS�y�q����
                    if(checkMobile.moveToNext()){
                        long up = dbAdapter.getProFlowUp(1, date);
                        long dw = dbAdapter.getProFlowDw(1, date);
                        mobileTx_all += up ;
                        mobileRx_all += dw ;
                        dbAdapter.updateData(mobileTx_all, mobileRx_all, 1, date);
                        System.out.println("��s�F GPRS �y�q �W��"+mobileTx_all+"�U��"+mobileRx_all);
                        mobileTx_all=0;
                        mobileRx_all=0;

                    }
                    if(!checkMobile.moveToNext()){

                        dbAdapter.insertData(mobileTx_all, mobileRx_all, 1, date);
                        System.out.println("��s�F GPRS �y�q �W��"+mobileTx_all+"�U��"+mobileRx_all);

                    }
                    checkMobile.close();
                }

                //�p�G�s�b�Ӥ�WIFI�y�q���O��h��s����O��
                if(wifiTx_all!=0 ||wifiRx_all!=0){
                    Cursor checkWifi = dbAdapter.check(0, date);//0�� wifi�y�q����
                    long up = dbAdapter.getProFlowUp(0, date);
                    long dw = dbAdapter.getProFlowDw(0, date);
                    if(checkWifi.moveToNext()){
                        wifiTx_all += up ;
                        wifiRx_all += dw ;
                        dbAdapter.updateData(wifiTx_all, wifiRx_all, 0, date);
                        System.out.println("��s�F WIFI �y�q �W��"+wifiTx_all+"�U��"+wifiRx_all);
                        wifiTx_all = 0 ;
                        wifiRx_all = 0 ;

                    }
                    else{

                        dbAdapter.insertData(wifiTx_all, wifiRx_all, 0, date);
                        System.out.println("��s�F WIFI �y�q �W��"+wifiTx_all+"�U��"+wifiRx_all);

                    }
                    checkWifi.close();
                }
                count = 1 ;
            }
            count++;
            dbAdapter.close();
            refresh(); //��s�a�B���f�ƾ�
            traffic_data=0;
            handler.postDelayed(thread, 1000);
        }

    };

    public void refresh(){
//    	totalRx = TrafficStats.getTotalRxBytes();
//		totalTx = TrafficStats.getTotalTxBytes();
//		traffic_data=( totalRx - old_totalRx );
//		old_totalRx=totalRx;
//		traffic_data+=( totalTx - old_totalTx );
//		old_totalTx=totalTx;

        Message msg = mHandler.obtainMessage();
        msg.what = 1;
//        try {
//			switch(isCMWAP(getApplicationContext()))
//			{
//				case 0:
//					msg.obj ="W " + TrafficMonitoring.convertTraffic(dbAdapter.calculateForMonth(year, month, 1))+" "+TrafficMonitoring.convertTraffic(traffic_data);
//					Log.i("Log", "WIFI MSG");
//					break;
//				case 1:
//					msg.obj ="G " + TrafficMonitoring.convertTraffic(dbAdapter.calculateForMonth(year, month, 0))+" "+TrafficMonitoring.convertTraffic(traffic_data);
//					break;
//				case 2:
//					msg.obj ="G " + TrafficMonitoring.convertTraffic(dbAdapter.calculateForMonth(year, month, 0))+" "+TrafficMonitoring.convertTraffic(traffic_data);
//					break;
//				default:
//					//view.tv_show.setText("!NONET ");
//					msg.obj ="! "+ TrafficMonitoring.convertTraffic(traffic_data);
//					Log.i("Log", "NONET");
//			}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        if(traffic_data<0)
            traffic_data=0;
        msg.obj =TrafficMonitoring.convertTraffic(traffic_data);
        mHandler.sendMessage(msg);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        handler.post(thread);
        return super.onStartCommand(intent, flags, startId);
    }
    public static long monitoringEachApplicationReceive(int uid) {
        long   receive=TrafficStats.getUidRxBytes(uid);
        if(receive==-1)receive=0;
        return receive;
    }

    public static long monitoringEachApplicationSend(int uid) {
        long   send=TrafficStats.getUidRxBytes(uid);
        if(send==-1)send=0;
        return send;
    }

    public int getNetType() {
        if(nwi != null) {
            String net = nwi.getTypeName();
            if(net.equalsIgnoreCase("wifi")) {
                return 0;
            }
            else if(net.equalsIgnoreCase("mobile")) {
                return 1;
            }
        }
        return -1;
    }

    public static int isCMWAP(Context context) throws Exception {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info == null || !info.isAvailable()) {
            return -1;
        }
        else if (info.getTypeName().equalsIgnoreCase("wifi")) {
            return 0;
        }
        else if (info.getTypeName() != null
                && (info.getTypeName().equalsIgnoreCase("mobile")||info.getExtraInfo().equalsIgnoreCase("cmwap"))
                && info.getExtraInfo() != null) {
            return 1;
        }
        else if (info.getTypeName() != null
                && info.getTypeName().equalsIgnoreCase("edge")
                && info.getExtraInfo() != null) {
            return 2;
        }
        return -1;
    }

    //��ɺʱ��y�q�έp�O�_�Ұ�
    Runnable checkStatThread=new Runnable(){

        @Override
        public void run() {
            // TODO Auto-generated method stub
            boolean startStat=false;

            if(killRunnable==true)
                return;

            settingDb = new SettingDatabase(TrafficMonitorService.this);
            settingDb.open();

            startStat=settingDb.checkStartStat();
            settingDb.close();

            Message msg = mHandler.obtainMessage();
            msg.what = 3;
            msg.obj = Boolean.toString(startStat);
            mHandler.sendMessage(msg);
            checkStatHandler.postDelayed(checkStatThread, 1500);
        }

    };

    //��ɺʱ��a�B���f�O�_�Ŀ����
    Runnable checkFloatWindowThread=new Runnable(){

        @Override
        public void run() {
            // TODO Auto-generated method stub
            boolean floatWindow=false;

            if(killRunnable==true)
                return;

            settingDb = new SettingDatabase(TrafficMonitorService.this);
            settingDb.open();

            floatWindow=settingDb.checkFloatWindow();
            settingDb.close();

            Message msg = mHandler.obtainMessage();
            msg.what = 2;
            msg.obj = Boolean.toString(floatWindow);
            mHandler.sendMessage(msg);
            checkFloatHandler.postDelayed(checkFloatWindowThread, 1500);
        }

    };

    Runnable flowRemainThread=new Runnable(){

        @Override
        public void run() {
            // TODO Auto-generated method stub
            Long GSMLimit,WIFILimit;
            String month3GTraffic,monthWIFITraffic;

            Calendar currentCa =  Calendar.getInstance();
            int year = currentCa.get(Calendar.YEAR);
            int month = currentCa.get(Calendar.MONTH)+1;

            if(killRunnable==true)
                return;

            dbAdapter = new DatabaseAdapter(TrafficMonitorService.this);
            dbAdapter.open();
            settingDb = new SettingDatabase(TrafficMonitorService.this);
            settingDb.open();

            month3GTraffic = TrafficMonitoring.convertTraffic(dbAdapter.calculateForMonth(year, month, 1));
            monthWIFITraffic =TrafficMonitoring.convertTraffic(dbAdapter.calculateForMonth(year, month, 0));

            try {
                if(isCMWAP(TrafficMonitorService.this)==0)
                {
                    // �ѾlWIFI�y�q
                    WIFILimit=settingDb.checkWIFILimit();

                    String tempString2[];// �{�ɦs�xWIFI�y�q
                    if(WIFILimit!=0)
                    {
                        double remainWIFI;
                        if (monthWIFITraffic.contains("KB")) {
                            tempString2 = monthWIFITraffic.split("KB");
                            double temp = Double.valueOf(tempString2[0]);
                            remainWIFI = new BigDecimal(WIFILimit * 1000 - temp).divide(new BigDecimal(1000),2,1).doubleValue();
                        } else if (monthWIFITraffic.contains("MB")) {
                            tempString2 = monthWIFITraffic.split("MB");
                            double temp = Double.valueOf(tempString2[0]);
                            remainWIFI = WIFILimit - temp;
                        } else {
                            tempString2 = monthWIFITraffic.split("GB");
                            double temp = Double.valueOf(tempString2[0]);
                            remainWIFI = new BigDecimal(WIFILimit - temp * 1000).doubleValue();
                        }

                        if(remainWIFI<remainTraffic)
                        {
                            //�a�B���ܦ�
                            Message msg = mHandler.obtainMessage();
                            msg.what = 4;
                            msg.obj="WIFI";
                            mHandler.sendMessage(msg);
                            Log.i("alert","remainalert");
                        }
                        else
                        {
                            Message msg = mHandler.obtainMessage();
                            msg.what = 5;
                            mHandler.sendMessage(msg);
                            Log.i("alert","noalert");
                        }
                    }
                    else
                    {
                        Message msg = mHandler.obtainMessage();
                        msg.what = 5;
                        mHandler.sendMessage(msg);
                        Log.i("alert","noalert");
                    }
                }
                else if(isCMWAP(TrafficMonitorService.this)==1)
                {
                    // �Ѿl3G�y�q
                    GSMLimit=settingDb.checkGSMLimit();

                    String tempString[];// �{�ɦs�x3G�y�q
                    if(GSMLimit!=0)
                    {
                        double remain3G;
                        if (month3GTraffic.contains("KB")) {
                            tempString = month3GTraffic.split("KB");
                            double temp = Double.valueOf(tempString[0]);
                            remain3G = new BigDecimal(GSMLimit * 1000 - temp).divide(new BigDecimal(1000),2,1).doubleValue();
                        } else if (month3GTraffic.contains("MB")) {
                            tempString = month3GTraffic.split("MB");
                            double temp = Double.valueOf(tempString[0]);
                            remain3G = GSMLimit - temp;
                        } else {
                            tempString = month3GTraffic.split("GB");
                            double temp = Double.valueOf(tempString[0]);
                            remain3G = new BigDecimal(GSMLimit - temp * 1000).doubleValue();
                        }

                        if(remain3G<remainTraffic)
                        {
                            //�a�B���ܦ�
                            Message msg = mHandler.obtainMessage();
                            msg.what = 4;
                            msg.obj="3G";
                            mHandler.sendMessage(msg);
                        }
                        else
                        {
                            Message msg = mHandler.obtainMessage();
                            msg.what = 5;
                            mHandler.sendMessage(msg);
                        }
                    }
                    else
                    {
                        Message msg = mHandler.obtainMessage();
                        msg.what = 5;
                        mHandler.sendMessage(msg);
                        Log.i("alert","noalert");
                    }
                }
            } catch (NumberFormatException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            dbAdapter.close();
            settingDb.close();
            flowRemainHandler.postDelayed(flowRemainThread, 10000);
        }};

    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(thread);
        checkFloatHandler.removeCallbacks(checkFloatWindowThread);
        checkStatHandler.removeCallbacks(checkStatThread);
        flowRemainHandler.removeCallbacks(flowRemainThread);
        killRunnable=true;
        Log.v("CountService", "on destroy");
    }
}