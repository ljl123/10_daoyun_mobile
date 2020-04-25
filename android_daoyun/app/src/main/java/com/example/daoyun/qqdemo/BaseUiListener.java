package com.example.daoyun.qqdemo;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.transition.Explode;
import android.util.Log;
import android.widget.Toast;

import com.example.daoyun.HttpBean.LoginBean;
import com.example.daoyun.HttpBean.RegisterBean;
import com.example.daoyun.activities.LoginActivity;
import com.example.daoyun.activities.MainActivity;
import com.example.daoyun.activities.RegisterActivity;
import com.example.daoyun.http.BaseObserver;
import com.example.daoyun.http.HttpUtil;
import com.example.daoyun.session.SessionKeeper;
import com.example.daoyun.utils.Util;
import com.kongzue.dialog.v2.TipDialog;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.daoyun.utils.Util.md5;

public class BaseUiListener implements IUiListener {
    private static final String TAG = "qq";
    private Context context;
    private Tencent mTencent;
    private UserInfo mUserInfo;
    private int type;
    public BaseUiListener(Context context,Tencent mTencent,int type){
        this.context=context;
        this.mTencent=mTencent;
        this.type=type;
    }
    @Override
    public void onComplete(Object response) {
        Toast.makeText(context, "授权成功", Toast.LENGTH_SHORT).show();
        Log.e(TAG, "response:" + response);
        JSONObject obj = (JSONObject) response;
        try {
            String openID = obj.getString("openid");
            String accessToken = obj.getString("access_token");
            String expires = obj.getString("expires_in");
            mTencent.setOpenId(openID);
            mTencent.setAccessToken(accessToken, expires);
            QQToken qqToken = mTencent.getQQToken();
            mUserInfo = new UserInfo(context.getApplicationContext(), qqToken);
            mUserInfo.getUserInfo(new IUiListener() {
                @Override
                public void onComplete(Object response) {
                    //是一个json串response.tostring，直接使用gson解析就好
                    Log.e(TAG, "登录成功" + response.toString());
                    //登录成功后进行Gson解析即可获得你需要的QQ头像和昵称
                    // Nickname  昵称
                    //Figureurl_qq_1 //头像
                    try {
                        String avatar = ((JSONObject) response).getString("figureurl_2");
                        String nickName = ((JSONObject) response).getString("nickname");
                        LoginBean loginBean=new LoginBean();
                        loginBean.setToken(accessToken);
                        loginBean.setUid(1000);//由于openID可能太长了，先直接就固定一个用户吧
                        loginBean.setType(type);
                        loginBean.setNick_name(nickName);
                       // loginBean.setToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtc2ciOiI1LDE1ODc3NDA2MzgyNjkiLCJpYXQiOjE1ODc3NDEwMDEsImV4cCI6MTU4ODEwMTAwMX0.LIsXFro1xCDf0aB5AnLlZBjQaEUfIEEcj37qXLeo-aU");
                        saveData(loginBean);
                        loginSuccess();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(UiError uiError) {
                    Log.e(TAG, "登录失败" + uiError.toString());
                }

                @Override
                public void onCancel() {
                    Log.e(TAG, "登录取消");

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onError(UiError uiError) {
        Toast.makeText(context, "授权失败", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onCancel() {
        Toast.makeText(context, "授权取消", Toast.LENGTH_SHORT).show();

    }
    private void saveData(LoginBean loginBean) {
        SessionKeeper.loginSave(context, loginBean);
    }
    private void loginSuccess() {
       // SessionKeeper.keepAutoLogin(this, true);

        Intent i2 = new Intent(context, MainActivity.class);
        context.startActivity(i2);
    }
}
