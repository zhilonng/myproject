package com.cn21.speedtest.model;

import android.graphics.drawable.Drawable;

import java.util.Calendar;

/**
 * Created by lenovo on 2016/8/17.
 */
public class Programme {


    private Drawable icon;


    private String name;


    private int uid;

    private long send;


    private long receive;


    private String netType;


    private Calendar linkDate;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public long getSend() {
        return send;
    }

    public void setSend(long send) {
        this.send = send;
    }

    public long getReceive() {
        return receive;
    }

    public void setReceive(long receive) {
        this.receive = receive;
    }

    public String getNetType() {
        return netType;
    }

    public void setNetType(String netType) {
        this.netType = netType;
    }

    public Calendar getLinkDate() {
        return linkDate;
    }

    public void setLinkDate(Calendar linkDate) {
        this.linkDate = linkDate;
    }

}