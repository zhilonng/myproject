package com.cn21.speedtest.utils;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by huangzhilong on 16/8/11.
 */
public class User {
    public static int inChooseProcessPid = 0;
    public static String inChoosePakageName;
    public static List<List<Float>> totalCpuRate;
    public static List<Float> PrcessCpuRate;
    public static List<String> inChoosePid;
    public static Float[] processCpuRate;
    public static boolean isFirstOpenCpu = true;
    public static float inChangeFirstFps;
    public static StringBuffer fpsPakage;
    public static String receiver;
    public static long thread_time_interval = 1000;
    public static List<String> documents = new ArrayList<>();
    public static List<Map<String, String>> mListItems = new ArrayList<Map<String, String>>();

    public static final int DEFAULT_COLOR = Color.parseColor("#DFDFDF");
    public static final int COLOR_BLUE = Color.parseColor("#33B5E5");
    public static final int COLOR_VIOLET = Color.parseColor("#AA66CC");
    public static final int COLOR_GREEN = Color.parseColor("#99CC00");
    public static final int COLOR_ORANGE = Color.parseColor("#FFBB33");
    public static final int COLOR_RED = Color.parseColor("#FF4444");


}
