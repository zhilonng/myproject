package com.cn21.speedtest.utils;

import android.util.Log;

/**
 * Log统一管理类，用来打印日志
 *
 * Copyright (c) 2016. shenpeng (sx_shenp@corp.21cn.com)
 * @version 1.0
 * 创建时间：2016/8/3.
 * 创建人：申鹏
 */
public class LogUtil {

    private LogUtil(){
        throw new UnsupportedOperationException("cannot not instantiated");
    }
    //default value is true,it can initialize in application's onCreate method
    public static boolean isDebug=true;
    public static void enableLog(){
        isDebug=true;
    }
    public static void disableLog(){
        isDebug=false;
    }
    /**
     * 调用系统log
     */
    private static final String TAG="com.cn21.unicorn.Log";
    public static void v(String msg){
        if(isDebug)
            Log.v(TAG,msg);
    }
    public static void d(String msg) {
        if (isDebug)
            Log.d(TAG, msg);
    }
    public static void i(String msg){
        if(isDebug)
            Log.i(TAG,msg);
    }
    public static void w(String msg){
        if (isDebug)
            Log.w(TAG,msg);
    }
    public static void e(String msg){
        if(isDebug)
            Log.e(TAG,msg);
    }


    /**
     * 下面是自定义TAG
     */
    public static void v(String tag,String msg){
        if(isDebug)
            Log.v(tag,msg);
    }
    public static void d(String tag,String msg){
        if(isDebug)
            Log.d(tag,msg);
    }
    public static void i(String tag,String msg){
        if (isDebug)
            Log.i(tag,msg);
    }
    public static void w(String tag,String msg){
        if (isDebug)
            Log.w(tag,msg);
    }
    public static void e(String tag,String msg){
        if(isDebug)
            Log.e(tag,msg);
    }
    public static String getStackTraceMsg(){
        StackTraceElement[] str=Thread.currentThread().getStackTrace();
        if (str==null){
            return null;
        }
        for (StackTraceElement st:str){
            if (st.isNativeMethod()){
                continue;
            }
            if (st.getClassName().equals(Thread.class.getName())){
                continue;
            }
            return "["+Thread.currentThread().getName()+"("+Thread.currentThread().getId()+")"+st.getFileName()+":"+st.getLineNumber()+"]";
        }
        return null;
    }
}
