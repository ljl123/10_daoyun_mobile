package com.example.ten_daoyun.utils;

import android.util.Log;

public class LogUtil {
    public static final int LOG_DEBUG = 2;
    public static final int LOG_PRODUCTION = 3;
    public static final int LOG_ALL = 1;
    public static final int LOG_LEVEL = LOG_DEBUG;

    public static void d(String tag, String s) {
        if (tag != null && s != null)
            if (LOG_LEVEL < LOG_PRODUCTION)
                Log.d(tag, s);
    }

    public static void d(String tag, String s, int level) {
        if (tag != null && s != null)
            if (level < LOG_PRODUCTION)
                Log.d(tag, s);
    }

    public static void e(String tag, String s, Throwable err) {
        if (tag != null && s != null && err != null)
            Log.e(tag, s, err);
    }

    public static void e(String tag, String s) {
        if (tag != null && s != null)
            Log.e(tag, s);
    }
}
