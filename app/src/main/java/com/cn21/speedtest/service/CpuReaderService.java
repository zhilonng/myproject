package com.cn21.speedtest.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Debug;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;

import com.cn21.speedtest.utils.LogUtil;
import com.cn21.speedtest.utils.SDCardUtils;
import com.cn21.speedtest.utils.ShellUtils;
import com.cn21.speedtest.utils.User;
import com.cn21.speedtest.view.FloatingWindows;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by huangzhilong on 16/8/9.
 * content：cpu模块适配器
 */
public class CpuReaderService extends Service {
    private static final java.lang.String TAG = "CpuReaderService" ;
    //总cpu占用=100*(work-workBefore)/(totalCpu-totalCpuBefore)
    private static Long totalCpu,totalCpuBefore,work,workBefore,processCpuBefore,processCpu;
    //public static long thread_time_interval =1000;
    private static int Fps = 0,FpsBefore = 0;
    private Thread thread;
    //private static List<Long> processCpu = new ArrayList<>(),processCpuBefore=new ArrayList<>();//多个进程
    private static List<Float> Draw = new ArrayList<>();
    private static List<Float> Process = new ArrayList<>();
    private static List<Float> Excute = new ArrayList<>();
    private static List<Float> FpsRealTime = new ArrayList<>();
    private static int i =0;

    //全局
    public static boolean isbegin = true;
    public static int cpu_id = 0;
    public static int fps_id = 1;
    public static int memory_id = 2;
    public static int power_id = 3;
    public static int isFirst =1;
    public static boolean runningCpu = false;
    public static boolean runningFps = false;
    public static boolean runningMemory = false;
    public static boolean runningPower = false;
    ActivityManager activityManager;

    //浮动窗口
    WindowManager windowManager;
    WindowManager.LayoutParams  layoutParams;
    FloatingWindows myLayout;

    public CpuReaderService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate(){
        super.onCreate();
        LogUtil.d(TAG,"onCreate() executed");
        emptyfile();

        //初始化浮动窗口
        windowManager = (WindowManager) getApplicationContext().getSystemService(getApplicationContext().WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.x = 0;
        layoutParams.y = 0;
        // myLayout is the customized layout which contains textview
        myLayout = new FloatingWindows(getApplicationContext());
        //初始化User类全局变量
        User.fpsPakage = new StringBuffer("com.cn21.speedtest");
        User.inChoosePid =new ArrayList<>();
        User.PrcessCpuRate = new ArrayList();
        User.processCpuRate = new Float[10];
        User.totalCpuRate = new ArrayList<>();
        User.totalCpuRate.add(new ArrayList<Float>());
        User.totalCpuRate.add(new ArrayList<Float>());
        User.totalCpuRate.add(new ArrayList<Float>());
        User.totalCpuRate.add(new ArrayList<Float>());
        activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);


        //用一个线程来定时监听
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    if(isbegin) {
                        try {
                            generateCpuData();
                        } catch (Exception e) {
                        }
                        try {
                            generateFpsData();
                        } catch (Exception e) {
                            LogUtil.e("开启dumpsys gfxinfo功能,并确保您是用android原生手机测试");
                        }
                        try {
                            generateMemoryData();
                        } catch (Exception e) {

                        }
                        try {
                            generateAppPowerData();
                        } catch (Exception e) {
                            LogUtil.e("android5.0以上方可使用本功能");
                        }

                        //发送广播提醒列表更新数据
                        Intent intent = new Intent("com.cn21.speedtest.CpuChartAndInfo");
                        sendBroadcast(intent);
                    }
                    synchronized (thread){
                        try {
                            thread.wait(User.thread_time_interval);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        });
        thread.start();

    }

    /**
     * 生成fps数据
     */
    private void generateFpsData() {
        FpsBefore = Fps;
        if (runningFps) {
            List<String> commnandList = new ArrayList();
            commnandList.add("dumpsys gfxinfo '"+User.inChoosePakageName+"' framestats");
            ShellUtils.CommandResult response = ShellUtils.execCommand(commnandList, true);
            if (response != null) {
                String stat = searcharray(response.successMsg);
                if (stat.equals("1")) {
                    int jank_count =0;
                    int vsync_overtime = 0;
                    float fps;
                    for (int i = 0; i < Draw.size(); i++) {
                        FpsRealTime.add(Draw.get(i) + Process.get(i) + Excute.get(0));
                        if (FpsRealTime.get(i)>16.67){
                            jank_count++;
                            if (FpsRealTime.get(i) %16.67 ==0){
                                vsync_overtime +=(int)(FpsRealTime.get(i)/16.67) - 1;
                            }else{
                                vsync_overtime += (int)(FpsRealTime.get(i)/16.67);
                            }
                        }
                    }

                    fps = (float)(FpsRealTime.size()*60/(FpsRealTime.size()+vsync_overtime));
                    if (User.totalCpuRate.get(fps_id).size() < 100) {
                        User.totalCpuRate.get(fps_id).add(fps);
                    } else {
                        User.inChangeFirstFps = User.totalCpuRate.get(fps_id).get(0);
                        User.totalCpuRate.get(fps_id).remove(0);
                        User.totalCpuRate.get(fps_id).add(fps);
                    }
                    User.processCpuRate[fps_id] = fps;

                    Intent intent = new Intent("com.cn21.speedtest.CpuCallInfo");
                    intent.putExtra("fps",String.valueOf(fps));
                    intent.putExtra("frames",String.valueOf(FpsRealTime.size()));
                    intent.putExtra("jank",String.valueOf(jank_count));
                    sendBroadcast(intent);
                }
            }
        }
    }

    /**
     * 生成耗电量数据
     */
    private void generateAppPowerData(){
        if (runningPower){
            List<String> cmds  = new ArrayList<String>();
            cmds.add("dumpsys batterystats --reset");
            cmds.add("dumpsys batterystats "+User.inChoosePakageName);
            ShellUtils.CommandResult commandResult = ShellUtils.execCommand(cmds,true,true);
            //appPowerUser = 0;
            if (commandResult.result ==0) {
                float batteryUse = searchStrng(commandResult.successMsg);
                if (User.totalCpuRate.get(power_id).size() < 100) {
                    User.totalCpuRate.get(power_id).add(batteryUse);
                } else {
                    User.totalCpuRate.get(power_id).remove(0);
                    User.totalCpuRate.get(power_id).add(batteryUse);
                }
                User.processCpuRate[power_id] = batteryUse;
            }
        }
    }
    /**
     * fps检索数组，获取draw、process、exucte数据
     * @param successMsg
     * @return
     */
    private String searcharray(String successMsg) {
        Draw.clear();
        Process.clear();
        Excute.clear();
        FpsRealTime.clear();
        int firstProWay = successMsg.indexOf("Profile data in ms",1);
        if (firstProWay<0) return "0";
        int SecondProWay = successMsg.indexOf("Draw\tProcess\tExecute",firstProWay);
        if (SecondProWay < 0) return "0";
        if (SecondProWay>0){
            int thirthProWay = successMsg.indexOf("Draw\tProcess\tExecute",SecondProWay+6);
            String View = successMsg.substring(thirthProWay+20,thirthProWay+24);
            if (View.equals("View")) {
                return "0";
            }
            String lastMsg;
            LogUtil.e("SecondProWay"+SecondProWay);
            LogUtil.e("thirthProWay:"+thirthProWay);
            if (thirthProWay>0){
                lastMsg = successMsg.substring(thirthProWay + 21);
            }else {
                lastMsg = successMsg.substring(SecondProWay+21);
            }
            int WayOfView = lastMsg.indexOf("View");
            //if (WayOfView <0) return "0";
            int WayOfCom = lastMsg.indexOf("com");
            int WayOfAndroid = lastMsg.indexOf("/android.view");
            String DPE;
            int CheckIew = 0;
            if (WayOfCom<WayOfView&&WayOfCom<WayOfAndroid && WayOfCom != -1){
                DPE = lastMsg.substring(0,WayOfCom);
                CheckIew = DPE.indexOf("iew");
                if(CheckIew != -1) return "0";
                LogUtil.e("1:"+DPE.toString());
            }else if (WayOfAndroid<WayOfCom&&WayOfAndroid<WayOfView && WayOfAndroid != -1){
                DPE = lastMsg.substring(0,WayOfAndroid);
                CheckIew = DPE.indexOf("Input");
                if (CheckIew !=-1) return "0";
                LogUtil.e("2:"+DPE.toString());
            }else{ DPE = lastMsg.substring(0,WayOfView);LogUtil.e("3:"+DPE.toString());}
            if (DPE.isEmpty()) return "0";
            String isNumber = DPE.substring(0,1);
            if (!(isNumber.equals("0")||isNumber.equals("1")||isNumber.equals("2")||isNumber.equals("3")
                    ||isNumber.equals("4")||isNumber.equals("5")||isNumber.equals("6")||isNumber.equals("7")
                    ||isNumber.equals("8")||isNumber.equals("9"))) return "0";
            String isLastNumber = DPE.substring(DPE.length()-2,DPE.length()-1);
            if (!(isLastNumber.equals("0")||isLastNumber.equals("1")||isLastNumber.equals("2")||isLastNumber.equals("3")
                    ||isLastNumber.equals("4")||isLastNumber.equals("5")||isLastNumber.equals("6")||isLastNumber.equals("7")
                    ||isLastNumber.equals("8")||isLastNumber.equals("9"))) return "0";
            i = 0;
            while (i<(DPE.length()-2)){
                int f = DPE.indexOf("\t",i);
                Draw.add(Float.valueOf(DPE.substring(i, f)));
                i=f+1;
                int s = DPE.indexOf("\t",i);
                Process.add(Float.valueOf(DPE.substring(i, s)));
                i=s+1;
                int t = DPE.indexOf("\t",i);
                if (t == -1){
                    Excute.add(Float.valueOf(DPE.substring(i,DPE.length())));
                    i = DPE.length();
                }else {
                    Excute.add(Float.valueOf(DPE.substring(i, t)));
                    i=t+1;
                }
            }
            if (DPE.equals("")){
                return "0";
            }
                return "1";
        }
        return "0";
    }

    /**
     * 耗电量数据检索功能
     * @param successInfo
     * @return
     */
    private float searchStrng(String successInfo){
        List<String> uidPowerList = new ArrayList<>();
        uidPowerList.clear();
        float returnResult = 0;
        String beginStr = "Estimated power use";                     //应该耗电量信息标志
        String beginUid1 = "Uid";                                    //每条UID耗电量标志
        String beginUid2 = ": ";                                     //数据开始标志
        String endUid = " ";                                         //数据结束标志
        int begin,end;
        String beginIn = "";
        begin = successInfo.indexOf(beginStr);
        if (begin != -1)
            beginIn = successInfo.substring(begin);

         //依次获取UID后面对应的耗电量
         //beginIn 为检索字符串段
        for(;;) {
            if (beginIn.indexOf(beginUid1)!= -1) {

                begin = beginIn.indexOf(beginUid1);

                beginIn = beginIn.substring(begin+4);
                begin = beginIn.indexOf(beginUid2);
                beginIn = beginIn.substring(begin+2);
                Log.d("字段",beginIn);
                begin = 0;
                end = beginIn.indexOf(endUid);
                uidPowerList.add(beginIn.substring(0, end));
                beginIn = beginIn.substring(end);
            }else
                break;
        }

        for(int i = 0;i < uidPowerList.size();i++){                 //将List里面每个UID的数值转化为float再加起来
            returnResult = returnResult + Float.valueOf(uidPowerList.get(i));
        }
        return returnResult;
    }
    /**
     * cpu占用率计算
     */
    private void generateCpuData() {
        if (runningCpu){
      //      每次开始生成数据，将上次生成的数据保存
            totalCpuBefore = totalCpu;
        workBefore = work;
        String[] cpuInfos = null;
        //计算总cpu占用率
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        //totalCpu = user+system+nice+idle+iowait+irq+softtirq
        work = Long.parseLong(cpuInfos[2])
                + Long.parseLong(cpuInfos[3]) + Long.parseLong(cpuInfos[4]);
        totalCpu = work
                + Long.parseLong(cpuInfos[6]) + Long.parseLong(cpuInfos[5])
                + Long.parseLong(cpuInfos[7]) + Long.parseLong(cpuInfos[8]);
        if (totalCpuBefore == null) {
            totalCpuBefore = totalCpu;
            workBefore = work;
        }

        //获取活动进程cpu占用率
        if (User.inChooseProcessPid != 0) {
                processCpuBefore = processCpu;
                String[] cpuInfos2 = null;
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            new FileInputStream("/proc/" + User.inChooseProcessPid + "/stat")), 1000);
                    String load = reader.readLine();
                    reader.close();
                    cpuInfos2 = load.split(" ");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                processCpu = Long.parseLong(cpuInfos2[13])
                        + Long.parseLong(cpuInfos2[14]) + Long.parseLong(cpuInfos2[15])
                        + Long.parseLong(cpuInfos2[16]);
                if (processCpuBefore == null||isFirst ==0) {
                    processCpuBefore = processCpu;
                    isFirst =1;
                } else {
                    float ProcessCpuRate = 100 * ((float) (processCpu - processCpuBefore) / (float) (totalCpu - totalCpuBefore));
                    if (processCpuBefore == 0) {
                        ProcessCpuRate = 0;
                    }
                    if (User.totalCpuRate.get(cpu_id).size() < 100) {
                        User.totalCpuRate.get(cpu_id).add(ProcessCpuRate);
                    } else {
                        User.totalCpuRate.get(cpu_id).remove(0);
                        User.totalCpuRate.get(cpu_id).add(ProcessCpuRate);
                    }
                    User.processCpuRate[cpu_id] = ProcessCpuRate;
                }

        }
    }
    }

    /**
     * Memory数据
     */
    private void generateMemoryData(){
        if (runningMemory) {
            float processNewMemory;
            if (User.inChooseProcessPid != 0) {
                    processNewMemory =getUniqueProcessInfo(Integer.valueOf(User.inChooseProcessPid)) / 1024;
                    if (User.totalCpuRate.get(memory_id).size() < 100) {
                        User.totalCpuRate.get(memory_id).add(processNewMemory);
                    } else {
                        User.totalCpuRate.get(memory_id).remove(0);
                        User.totalCpuRate.get(memory_id).add(processNewMemory);
                    }
                    User.processCpuRate[memory_id] = processNewMemory;
            }
        }else {

        }
    }

    /**
     * 获取进程内存数据
     * @param pid
     * @return
     */
    public float getUniqueProcessInfo(int pid) {
        int[] myMenPid = new int[]{pid};
        Debug.MemoryInfo[] memoryInfos = activityManager.getProcessMemoryInfo(myMenPid);
        float memSize = memoryInfos[0].dalvikPrivateDirty;
        return memSize;
    }

    private void emptyfile() {
        User.documents.clear();
        File file = new File(getApplicationContext().getExternalFilesDir(null).getPath()+"/");
        LogUtil.e(getApplicationContext().getExternalFilesDir(null).getPath());
        SDCardUtils.deleteFile(file);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        LogUtil.d(TAG,"onDestroy() executed");
    }
}
