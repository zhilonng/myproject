package com.cn21.speedtest.model;

import android.graphics.drawable.Drawable;


public class PackageClass {

    private Drawable mIcon;
    private String mLable;
    private  String mPackageName;

    public Drawable getmIcon() {
        return mIcon;
    }

    public void setmIcon(Drawable mIcon) {
        this.mIcon = mIcon;
    }

    public String getmLable() {
        return mLable;
    }

    public void setmLable(String mLable) {
        this.mLable = mLable;
    }

    public String getmPackageName() {
        return mPackageName;
    }

    public void setmPackageName(String mPackageName) {
        this.mPackageName = mPackageName;
    }
}
