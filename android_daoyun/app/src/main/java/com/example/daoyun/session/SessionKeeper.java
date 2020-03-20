package com.example.daoyun.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.daoyun.HttpBean.LoginBean;
import com.example.daoyun.utils.LogUtil;
import com.google.gson.Gson;

public class SessionKeeper {
    //得到TAG
    protected static final String TAG = SessionKeeper.class.getSimpleName();
    //Token
    protected static final String TOKEN = "token";
    //自动登录
    protected static final String AUTO_LOGIN = "autoLogin";
    //用户ID
    protected static final String USER_ID = "userId";
    //用户名
    protected static final String USER_NAME = "email";
    //昵称
    protected static final String USER_NICK_NAME = "nickName";
    //用户密码
    protected static final String USER_PASSWORD = "userPassword";
    //用户头像
    protected static final String USER_AVATAR = "userAvatar";
    //用户信息
    protected static final String USER_INFO = "userInfo";
    //用户类型
    protected static final String USER_TYPE = "userType";
//    //个人资料公开
//    protected static final String SHOW_PERSONAL_INFO = "showPersonalInfo";
//    //反馈
//    protected static final String FEED_BACK = "feedBack";
//    //显示地点
//    protected static final String SHOW_LOCATION = "showLocation";
//    //消息提醒
//    protected static final String IS_NOTIFY = "isNotify";
//    //声音
//    protected static final String IS_SOUND = "isSound";
//    //震动
//    protected static final String IS_SHOCK = "isShock";
//    //旅途数
//    protected static final String TRAVEL_NUMBER = "travelNumber";

    public static void keepUserInfo(Context context, LoginBean info) {
        SharedPreferences sp = context.getSharedPreferences(TAG, 0);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        editor.putString(USER_INFO, gson.toJson(info));
        editor.apply();
    }

    public static LoginBean getUserInfo(Context context) {
        return new Gson().fromJson(context.getSharedPreferences(TAG, 0).getString(USER_INFO, null),LoginBean.class);
    }

    public static void keepToken(Context context, String token) {
        SharedPreferences sp = context.getSharedPreferences(TAG, 0);
        SharedPreferences.Editor editor = sp.edit();
        Log.d("token","1122 "+token);
        editor.putString(TOKEN, token);
        editor.apply();
    }

    public static String getToken(Context context) {
        return context.getSharedPreferences(TAG, 0).getString(TOKEN, "");
    }

    public static void keepUserType(Context context, String type) {
        SharedPreferences sp = context.getSharedPreferences(TAG, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(USER_TYPE, type);
        editor.apply();
    }

    public static String getUserType(Context context) {
        return context.getSharedPreferences(TAG, 0).getString(USER_TYPE, "3");
    }

    public static void keepAutoLogin(Context context, boolean autoLogin) {
        SharedPreferences sp = context.getSharedPreferences(TAG, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(AUTO_LOGIN, autoLogin);
        editor.apply();
    }

    public static boolean getAutoLogin(Context context) {
        return context.getSharedPreferences(TAG, 0).getBoolean(AUTO_LOGIN, false);
    }

    public static void keepUserId(Context context, String userId) {
        SharedPreferences sp = context.getSharedPreferences(TAG, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(USER_ID, userId);
        editor.apply();
    }

    public static String getUserId(Context context) {
        SharedPreferences sp = context.getSharedPreferences(TAG, 0);
        return sp.getString(USER_ID, "");
    }

    public static void keepUserEmail(Context context, String email) {
        SharedPreferences sp = context.getSharedPreferences(TAG, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(USER_NAME, email);
        editor.apply();
    }

    public static String getUserEmail(Context context) {
        SharedPreferences sp = context.getSharedPreferences(TAG, 0);
        return sp.getString(USER_NAME, "");
    }

    public static void keepUserNickName(Context context, String userNickName) {
        SharedPreferences sp = context.getSharedPreferences(TAG, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(USER_NICK_NAME, userNickName);
        editor.apply();
    }

    public static String getUserNickName(Context context) {
        SharedPreferences sp = context.getSharedPreferences(TAG, 0);
        return sp.getString(USER_NICK_NAME, "");
    }

    public static void keepUserAvatar(Context context, String userAvatar) {
        SharedPreferences sp = context.getSharedPreferences(TAG, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(USER_AVATAR, userAvatar);
        editor.apply();
    }

    public static String getUserAvatar(Context context) {
        SharedPreferences sp = context.getSharedPreferences(TAG, 0);
        return sp.getString(USER_AVATAR, "");
    }

    public static void keepUserPassword(Context context, String userPassword) {
        SharedPreferences sp = context.getSharedPreferences(TAG, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(USER_PASSWORD, userPassword);
        editor.apply();
    }

    public static String getUserPassword(Context context) {
        SharedPreferences sp = context.getSharedPreferences(TAG, 0);
        return sp.getString(USER_PASSWORD, "");
    }

    public static void clearSession(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(TAG, 0).edit();
        editor.clear();
        editor.apply();
    }

    public static void loginSave(Context context,LoginBean loginBean){
        LogUtil.d("save data",String.valueOf(loginBean.getAvatar()));
        keepToken(context,loginBean.getToken());
        keepUserId(context,String.valueOf(loginBean.getUid()));
        keepUserAvatar(context,loginBean.getAvatar());
        keepUserEmail(context,loginBean.getEmail());
        keepUserType(context,String.valueOf(loginBean.getType()));
        keepUserNickName(context,loginBean.getNick_name());
        keepUserInfo(context,loginBean);
    }
}
