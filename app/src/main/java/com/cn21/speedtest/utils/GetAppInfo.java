package com.cn21.speedtest.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.cn21.speedtest.model.AppInfo;
import com.cn21.speedtest.model.DefaultApplication;
import com.cn21.speedtest.model.ProcessInfo;
import com.cn21.speedtest.model.Programe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**获取App跟Process信息
 * 两个静态方法
 * queryAppInfo获取系统App的信息 跟getAppInfo方法一起用
 * queryProcessInfo 获取进程信息 跟getProcessInfo方法一起用
 *
 * Created by 梁照江 on 2016/8/9.
 */
public class GetAppInfo {
    private static final int GET_ALL_APPS = 0;
    private static final int GET_SYSTEM_APPS = 1;
    private static final int GET_USER_APPS = 2;

    public static void getAllappInfo(Context context){
        PackageManager packageManager = context.getPackageManager();
        List<ApplicationInfo> listApplications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        Collections.sort(listApplications,new ApplicationInfo.DisplayNameComparator(packageManager));
        DefaultApplication.allAppInfo.clear();
        for (ApplicationInfo app :listApplications){
            AppInfo appInfo = new AppInfo();
            appInfo.setPkgName(app.packageName);
            appInfo.setAppIcon(app.loadIcon(packageManager));
            appInfo.setAppLabel(app.loadLabel(packageManager).toString());
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0){   //如果是系统应用就如此
                appInfo.setFlagSystem();
            }
            DefaultApplication.allAppInfo.add(appInfo);
        }
            List<Programe> programeList = GetAppInfo.queryProcess();
                int o = 0;
                DefaultApplication.LoadingFlag = false;
                for (int i = 0 ; i<DefaultApplication.allAppInfo.size();i++){
                    for (int k = 0 ;k<programeList.size();k++){
                        if (DefaultApplication.allAppInfo.get(i).getPkgName().equals(programeList.get(k).getProcessName())){
                            AppInfo appInfo = DefaultApplication.allAppInfo.get(i);
                            appInfo.setFlagRunning();
                            appInfo.setPID(programeList.get(k).getPid());
                            appInfo.setUID(programeList.get(k).getUid());
                        }
                    }
                }
    }

    public static List<AppInfo> queryAppInfo(Activity activity, int get_flag){
        PackageManager pm = activity.getPackageManager();
        List<ApplicationInfo> listApplications = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        Collections.sort(listApplications,
                new ApplicationInfo.DisplayNameComparator(pm));// 排序
        List<AppInfo> appInfos = new ArrayList<AppInfo>(); // 保存过滤查到的AppInfo
        // 根据条件来过滤
        switch (get_flag) {
            case GET_ALL_APPS:
                appInfos.clear();
                for (ApplicationInfo app : listApplications) {   //所有应用
                    appInfos.add(getAppInfo(app,pm));
                }
                break;
            case GET_SYSTEM_APPS:
                appInfos.clear();
                for (ApplicationInfo app : listApplications) {
                    if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                        appInfos.add(getAppInfo(app,pm));
                    }
                }
                break;
            case GET_USER_APPS:          //第三方软件
                appInfos.clear();
                for (ApplicationInfo app : listApplications)
                    if ((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                        appInfos.add(getAppInfo(app,pm));
                    } else if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                        appInfos.add(getAppInfo(app,pm));
                    }
                break;
            default:
                break;
        }
        return appInfos;
    }
    private static AppInfo getAppInfo(ApplicationInfo app ,PackageManager packageManager) {
        AppInfo appInfo = new AppInfo();
        appInfo.setAppLabel((String) app.loadLabel(packageManager));
        appInfo.setAppIcon(app.loadIcon(packageManager));
        appInfo.setPkgName(app.packageName);
        appInfo.setClassName(app.className);
        return appInfo;
    }
    public static  List<Programe> queryProcessInfo(Activity activity){
        List<Programe> processInfoList = new ArrayList<Programe>();
        ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> mRunningProcess = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info:mRunningProcess){
            processInfoList.add(getProcessInfo(info));
        }
        return  processInfoList;
    }

    private static Programe getProcessInfo(ActivityManager.RunningAppProcessInfo info) {
        Programe programe = new Programe();
        programe.setProcessName(info.processName);
        programe.setPid(info.pid);
        programe.setUid(info.uid);

        return programe;
    }

    public static List<Programe> queryProcess(){
        final List<Programe> programes = new ArrayList<>();
        List<ProcessUtil.Process> processes = ProcessUtil.getRunningProcesses();
        LogUtil.e(processes.get(0) + "");
        for (ProcessUtil.Process p : processes) {
            int pid = p.pid;
            int uid = p.uid;
            int cpu = p.cpu;
            String packageName = p.getPackageName();
            double memSize = p.vsize / (1024);
            //去掉空packagename
            if (packageName == null) continue;
            Programe programe = new Programe();
            programe.setPid(pid);
            programe.setUid(uid);
            programe.setCpu(cpu);
            programe.setMemSize(memSize);
            programe.setProcessName(packageName);
            programes.add(programe);
        }


        return  programes;
    }

    /**
     * 设置默认APP
     * @param
     */
    public static void setDefaultApplication(AppInfo info){
        DefaultApplication.setPkgName(info.getPkgName());
        DefaultApplication.setLabel(info.getAppLabel());
        DefaultApplication.setPID(info.getPID());
        DefaultApplication.setUID(info.getUID());
        DefaultApplication.setIcon(info.getAppIcon());

    }


}
