package com.cn21.speedtest.model;

import android.content.Intent;
import android.graphics.drawable.Drawable;

/**
 * Created by Host on 2016/8/9.
 */
public class AppInfo {

    private String appLabel;    //应用程序标签
    private Drawable appIcon;  //应用程序图像
    private Intent intent;     //启动应用程序的Intent ，一般是Action为Main和Category为Lancher的Activity
    private String pkgName;    //应用程序所对应的包名
    private String className;         //应用程序入口
    private long cacheSize;
    private long dataSize;
    private long codeSize;
    private long totalSize;
    private int UID;
    private int PID;
    private boolean flagRunning = false;  //是否正面运行
    private boolean flagSystem = false;   //是否系统应用

    public AppInfo() {
    }

    public String getAppLabel() {
        return appLabel;
    }

    public void setAppLabel(String appName) {
        this.appLabel = appName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public long getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(long cacheSize) {
        this.cacheSize = cacheSize;
    }

    public long getDataSize() {
        return dataSize;
    }

    public void setDataSize(long dataSize) {
        this.dataSize = dataSize;
    }

    public long getCodeSize() {
        return codeSize;
    }

    public void setCodeSize(long codeSize) {
        this.codeSize = codeSize;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public void setUID(int UID){
        this.UID = UID;
    }

    public int getUID(){
        return this.UID;
    }

    public void setPID(int PID){
        this.PID= PID;
    }

    public int getPID(){
        return this.PID;
    }

    public void setFlagSystem(){
        this.flagSystem = true;
    }

    public boolean getFlagSystem(){
        return flagSystem;
    }

    public void setFlagRunning(){
        this.flagRunning = true;
    }

    public boolean getFlagRunning(){
        return this.flagRunning;
    }

}
