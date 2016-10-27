package com.cn21.speedtest.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by luwy on 2016/8/9.
 */
public class DeviceInfoUtil {
    Handler handler;

    public String getRooted() {
        String rooted;
        if(isDeviceRooted()){
            rooted="是";
        }
        else{
            rooted="否";
        }
        return rooted;
    }

    public String getModel() {
        return Build.MODEL;
    }

    public String getBrand() {

        return Build.BRAND;
    }

    public int getSdk() {

        return  Build.VERSION.SDK_INT ;
    }

    public String getRelease() {
        return Build.VERSION.RELEASE;
    }


    public int getDisplayMetricsHeight(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        return height;

    }

    public int getDisplayMetricsWidth(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        return width;

    }

    public String getDisplayMetricsSize(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density= dm.densityDpi;
        int height = dm.heightPixels;
        int width = dm.widthPixels;
        double longth=Math.sqrt(height*height + width *width);
        double msize= longth/density;
        DecimalFormat decimalFormat=new DecimalFormat(".0");
        String size=decimalFormat.format(msize);
        return size;
    }

    public String getMacAdrs(Context context) {
        String mac;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getMacAddress() != null) {
            mac = wifiInfo.getMacAddress();
        } else {
            mac = "Fail";
        }
        return mac;
    }

    public String getIpInfo(Context context) {

        int ip;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getMacAddress() != null) {
            ip = wifiInfo.getIpAddress();
        } else {
            ip = 0;
        }
        return intToIp(ip);
    }
    //转换IP格式
    private String intToIp(int i)  {
        return (i & 0xFF)+ "." + ((i >> 8 ) & 0xFF) + "." + ((i >> 16 ) & 0xFF) +"."+((i >> 24 ) & 0xFF );
    }

    public void getNetIp(Handler handler) {
        this.handler=handler;
        thread.start();
    }

    public String getImsi(Context context) {

        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = mTelephonyMgr.getSubscriberId();

        return imsi;
    }

    public String getImei(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = mTelephonyMgr.getDeviceId();
        return imei;
    }

    public String getBoard() {
        return Build.BOARD;
    }

    public String getDevice() {
        return Build.DEVICE;
    }

    public String getDisplay() {
        return Build.DISPLAY;
    }

    public String getManufacturer() {
        return Build.MANUFACTURER;
    }

    public String getProduct() {
        return Build.PRODUCT;
    }

    public String getSerial() {
        return Build.SERIAL;
    }

    public String getTime() {
        Long timenum = Build.TIME ;
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String time= simpleDateFormat.format(new Date(timenum));
        return time;
    }

    public  String getCpuName() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("/proc/cpuinfo"));
            String text = br.readLine();
            //text 的值为   Processor	  : ARMv7 Processor rev 1 (v7l)
            //利用正则表达式将 ：及任意个空格后的内容输出,并进行截取
            String[] array = text.split(":\\s+", 2);
            return array[1].substring(0,5);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getNumCores() {
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                if (Pattern.matches("cpu[0-9]", pathname.getName())) {
                    return true;
                }
                return false;
            }
        }
        try {
            File dir = new File("/sys/devices/system/cpu/");
            File[] files = dir.listFiles(new CpuFilter());
            return files.length;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    public String getMaxCpuFreq() {
        String result = "";
        ProcessBuilder cmd;
        try {
            String[] args = {"/system/bin/cat",
                    "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"};
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result.trim();
    }

    public String getRamMemory(Context context) {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;
        try {
            BufferedReader localBufferedReader = new BufferedReader(new FileReader(str1));
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小
            //str2的输出为 MemTotal:        1907412 kB
            arrayOfString = str2.split("\\s+");
            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
            localBufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Formatter.formatFileSize(context, initial_memory);// 内存大小规格化

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public String getRomMemorySize(Context context) {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        return  Formatter.formatFileSize(context,totalBlocks * blockSize );
    }

    Thread thread=new Thread(new Runnable() {
        public void run() {
            URL infoUrl ;
            String line ;
            InputStream inStream ;
            try {
                infoUrl = new URL("http://1212.ip138.com/ic.asp");
                URLConnection connection = infoUrl.openConnection();
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                int responseCode = httpConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    inStream = httpConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(inStream, "gb2312"));
                    StringBuilder strber = new StringBuilder();
                    while ((line = reader.readLine()) != null)
                        strber.append(line + "\n");
                    inStream.close();
                    int start = strber.indexOf("[");
                    int end=strber.indexOf("]", start + 1);
                    line=strber.substring(start+1,end);
                    Message msg=new Message();
                    msg.what=2;
                    Bundle bundle=new Bundle();
                    bundle.putString("address",line);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    });



    public boolean isDeviceRooted() {
        if (checkRootMethod1()){return true;}
        if (checkRootMethod2()){return true;}
        if (checkRootMethod3()){return true;}

        return false;
    }

    private boolean checkRootMethod3() {
        if ((!new File("/system/bin/su").exists()) && (!new File("/system/xbin/su").exists())){
            return false;
        } else {
            return  true;
        }
    }


    private boolean checkRootMethod1(){
        String buildTags = Build.TAGS;
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true;
        }
        return false;
    }
    private boolean checkRootMethod2(){
        try {
            File file = new File("/system/app/Superuser.apk");
            if (file.exists()) {
                return true;
            }
        } catch (Exception e) { }
        return false;
    }






}


