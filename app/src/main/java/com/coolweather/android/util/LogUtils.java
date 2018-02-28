package com.coolweather.android.util;

import android.util.Log;

import com.coolweather.android.BuildConfig;

/**
 * @author:nsh
 * @data:2018/1/30. 下午1:07
 */

public class LogUtils {

    public static final boolean DEBUG_MODE = BuildConfig.DEBUG;


    public static void d(String tag, String msg) {
        if (DEBUG_MODE) {
            Log.d(tag, msg + " - tag:" + tag);
        }
    }

    public static void v(String tag, String msg) {
        if (DEBUG_MODE) {
            Log.v(tag, msg + " - tag:" + tag);
        }
    }

    public static void w(String tag, String msg, Throwable e) {
        if (DEBUG_MODE) {
            Log.w(tag, msg + " - tag:" + tag, e);
        }
    }

    // 更多log输出方法 ....

    public static boolean isDebug() {
        return DEBUG_MODE;
    }
}
