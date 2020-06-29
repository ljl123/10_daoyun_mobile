package com.example.ten_daoyun.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    public static final int LENGTH_LONG = 1;
    public static final int LENGTH_SHORT = 0;

    /**
     * 吐司显示消息
     *
     * @param context 上下文
     * @param text    信息
     * @param time    持续时间 1：Long 0：Short
     */
    public static void showMessage(Context context, String text, int time) {
        Toast.makeText(context, text, time == 1 ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    /**
     * 吐司显示消息 默认短时显示
     *
     * @param context 上下文
     * @param text    信息
     */
    public static void showMessage(Context context, String text) {
        showMessage(context, text, LENGTH_SHORT);
    }

    public static void showNetworkErrorMsg(Context context){
        showMessage(context,"网络错误，请检查网络",LENGTH_SHORT);
    }
}
