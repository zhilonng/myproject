package com.cn21.speedtest.model;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 梁照江 on 2016/8/17.
 */
public class DefaultApplication {
    public static List<AppInfo> allAppInfo = new ArrayList<AppInfo>();
    public static List<Float> battery = new ArrayList<Float>();
    private static String label="";
    private static String pkgName = "";
    private static int PID;
    private static int UID;
    private static Drawable icon = null;
    public static boolean LoadingFlag = false;
    public static boolean loadFinish = false;

    public static void setLabel(String label){
        DefaultApplication.label = label;
    }

    public static String getLabel(){
        return DefaultApplication.label;
    }

    public static void setPkgName(String pkgName){
        DefaultApplication.pkgName = pkgName;
    }

    public static String getPkgName(){
        return DefaultApplication.pkgName;
    }

    public static void setPID(int PID){
        DefaultApplication.PID = PID;
    }

    public static int getPID(){
        return DefaultApplication.PID;
    }

    public static void setIcon(Drawable icon){
        DefaultApplication.icon = icon;
    }

    public static Drawable getIcon() {
        return DefaultApplication.icon;
    }


    public static void setUID(int UID){
        DefaultApplication.UID = UID;
    }

    public static int getUID(){
        return DefaultApplication.UID;
    }
}
