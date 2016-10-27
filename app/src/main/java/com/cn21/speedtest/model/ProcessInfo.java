package com.cn21.speedtest.model;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shenpeng on 2016/8/9.
 */
public class ProcessInfo{

    private static final String PACKAGE_NAME = "com.cn21.speedtest";
    private static final int ANDROID_M = 22;

    /**
     * get information of all running processes,including package name ,process
     * name ,icon ,pid and uid.
     *
     * @param context
     *            context of activity
     * @return running processes list
     */
    public List<Programe> getRunningProcess(Context context) {
        List<Programe> programeList = new ArrayList<Programe>();
        PackageManager packageManager = context.getPackageManager();

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> run=activityManager.getRunningAppProcesses();
        for (ApplicationInfo applicationInfo : getPackagesInfo(context)) {
            Programe programe = new Programe();
            if (((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) || ((applicationInfo.processName != null) & (applicationInfo.processName).equals(PACKAGE_NAME))) {
                continue;
            }
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo:run){
                if ((runningAppProcessInfo.processName!=null)&&runningAppProcessInfo.processName.equals(applicationInfo.processName)){
                    programe.setPid(runningAppProcessInfo.pid);
                    programe.setUid(runningAppProcessInfo.uid);
                    break;
                }
            }
            programe.setPackageName(applicationInfo.processName);
            programe.setPackageName(applicationInfo.loadLabel(packageManager).toString());
            programe.setIcon(applicationInfo.loadIcon(packageManager));
            programeList.add(programe);
        }
//        Collections.sort(programeList);
        return programeList;
    }
    /**
     * get information of all applications.
     *
     * @param context
     *            context of activity
     * @return packages information of all applications
     */
    public List<ApplicationInfo> getPackagesInfo(Context context) {
        PackageManager packageManager = context.getApplicationContext().getPackageManager();
        List<ApplicationInfo> applicationInfoList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        return applicationInfoList;
    }
    /**
     * get pid by package name
     *
     * @param context
     *            context of activity
     * @return pid
     */
    public int getPidByPackageName(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        // Note: getRunningAppProcesses return itself in API 22
        if (Build.VERSION.SDK_INT < ANDROID_M) {
            List<RunningAppProcessInfo> run = am.getRunningAppProcesses();
            for (RunningAppProcessInfo runningProcess : run) {
                if ((runningProcess.processName != null)
                        && runningProcess.processName.equals(packageName)) {
                    return runningProcess.pid;
                }
            }
        } else {
            try {
                Process p = Runtime.getRuntime().exec("top -m 100 -n 1");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                        p.getInputStream()));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.contains(packageName)) {
                        line = line.trim();
                        String[] splitLine = line.split("\\s+");
                        if (packageName.equals(splitLine[splitLine.length - 1])) {
                            return Integer.parseInt(splitLine[0]);
                        }
                    }
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return 0;
    }
    /**
     * get information of all installed packages
     *
     * @param context
     *            context of activity
     * @return all installed packages
     */
    public List<Programe> getAllPackages(Context context) {
        List<Programe> progressList = new ArrayList<Programe>();
        PackageManager pm = context.getPackageManager();

        for (ApplicationInfo appinfo : getPackagesInfo(context)) {
            Programe programe = new Programe();
            if (((appinfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0)
                    || ((appinfo.processName != null) && (appinfo.processName
                    .equals(PACKAGE_NAME)))) {
                continue;
            }
            programe.setPackageName(appinfo.processName);
            programe.setProcessName(appinfo.loadLabel(pm).toString());
            programe.setIcon(appinfo.loadIcon(pm));
            progressList.add(programe);
        }
        return progressList;
    }
    /**
     * get programe by package name
     *
     * @param context
     *            context of activity
     * @param packageName
     *            package name of monitoring app
     * @return pid
     */
    public Programe getProgrameByPackageName(Context context, String packageName) {
        if (Build.VERSION.SDK_INT < ANDROID_M) {
            List<Programe> processList = getRunningProcess(context);
            for (Programe programe : processList) {
                if ((programe.getPackageName() != null)
                        && (programe.getPackageName().equals(packageName))) {
                    return programe;
                }
            }
        } else {
            Programe programe = new Programe();
            int pid = getPidByPackageName(context, packageName);
            programe.setPid(pid);
            programe.setUid(0);
            return programe;
        }
        return null;
    }
    /**
     * get top activity name
     *
     * @param context
     *            context of activity
     * @return top activity name
     */
    public static String getTopActivity(Context context) {
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        // Note: getRunningTasks is deprecated in API 21(Official)
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
        if (runningTaskInfos != null)
            return (runningTaskInfos.get(0).topActivity).toString();
        else
            return null;
    }
}
