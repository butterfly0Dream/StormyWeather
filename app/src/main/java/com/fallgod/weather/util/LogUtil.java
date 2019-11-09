package com.fallgod.weather.util;

import android.content.IntentFilter;
import android.util.Log;

/**
 * Created by JackPan on 2019/10/30
 * Describe:log工具类
 */
public class LogUtil {

    public static final int VERBOSE = 2;
    public static final int DEBUG = 3;
    public static final int INFO = 4;
    public static final int WARN = 5;
    public static final int ERROR = 6;
    public static final int ASSERT = 7;
    public static final int NOTHING = 9;

    public static int level = VERBOSE;

    public static void v(String tag,String msg){
        if (level <= VERBOSE){
            Log.v(tag, msg);
        }
    }

    public static void d(String tag,String msg){
        if (level <= DEBUG){
            Log.d(tag, msg);
        }
    }

    public static void i(String tag,String msg){
        if (level <= INFO){
            Log.i(tag, msg);
        }
    }

    public static void w(String tag,String msg){
        if (level <= WARN){
            Log.w(tag, msg);
        }
    }

    public static void e(String tag,String msg){
        if (level <= ERROR){
            Log.e(tag, msg);
        }
    }

}
