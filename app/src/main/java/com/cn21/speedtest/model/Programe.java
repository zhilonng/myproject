package com.cn21.speedtest.model;

import android.graphics.drawable.Drawable;

import java.util.Comparator;

/**
 * Created by shenpeng on 2016/8/10.
 */
public class Programe implements Comparator<Programe>{
    private Drawable icon;
    private String processName;
    private String packageName;
    private double memSize;
    private int pid;
    private int uid;
    private float time;


    private double cpu;

    public double getCpu() {
        return cpu;
    }

    public void setCpu(double cpu) {
        this.cpu = cpu;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public double getMemSize() {
        return memSize;
    }

    public void setMemSize(double memSize) {
        this.memSize = memSize;
    }


    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int compareTo(Programe programe){
        return (this.getProcessName().compareTo(programe.getProcessName()));
    }

    @Override
    public int compare(Programe programe, Programe t1) {
        return 0;
    }
}
