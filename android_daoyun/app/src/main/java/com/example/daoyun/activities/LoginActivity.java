package com.example.daoyun.activities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.transition.Explode;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.daoyun.HttpBean.LoginBean;
import com.example.daoyun.R;
import com.example.daoyun.http.BaseObserver;
import com.example.daoyun.http.HttpUtil;
import com.example.daoyun.session.SessionKeeper;
import com.example.daoyun.utils.LogUtil;
import com.example.daoyun.utils.ToastUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.daoyun.utils.Util.md5;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.bt_go)
    Button btGo;
    @BindView(R.id.cv)
    CardView cv;
    @BindView(R.id.register_go)
    Button fab;
    @BindView(R.id.tv_forgot_password)
    TextView tvForgotPassword;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("登录中....");
        progressDialog.setCancelable(false);
    }

    private void initData() {
        verifyStoragePermissions(this);
        etUsername.setText(SessionKeeper.getUserEmail(LoginActivity.this));
        etPassword.setText(SessionKeeper.getUserPassword(LoginActivity.this));
        if (SessionKeeper.getAutoLogin(this)) {
            onBtGoClicked();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick(R.id.bt_go)
    public void onBtGoClicked() {
        progressDialog.show();
        btGo.setClickable(false);
        if (!etUsername.getText().toString().isEmpty() && !etPassword.getText().toString().isEmpty()) {
            String pwd = etPassword.getText().toString();
            Map<String, String> params = new HashMap<>();
            params.put("username", etUsername.getText().toString());
            params.put("password", pwd.length() > 20 ? pwd : md5(pwd));
            HttpUtil.login(params, new BaseObserver<LoginBean>() {
                @Override
                protected void onSuccess(LoginBean loginBean) {
                    progressDialog.dismiss();
                    if (loginBean.getResult_code().equals("200")) {
                        saveData(loginBean.getData());
                        loginSuccess();
                    } else {
                        btGo.setClickable(true);
                        SessionKeeper.keepAutoLogin(getApplicationContext(),false);
                        ToastUtil.showMessage(LoginActivity.this, loginBean.getResult_desc(), ToastUtil.LENGTH_LONG);
                    }
                }

                @Override
                protected void onFailure(Throwable e, boolean isNetWorkError) {
                    progressDialog.dismiss();
                    SessionKeeper.keepAutoLogin(getApplicationContext(),false);
                    btGo.setClickable(true);
                    if (isNetWorkError)
                        ToastUtil.showMessage(LoginActivity.this, "网络错误，请检查网络", ToastUtil.LENGTH_LONG);
                    else {
                        ToastUtil.showMessage(LoginActivity.this, e.getMessage(), ToastUtil.LENGTH_LONG);
                        LogUtil.e("LoginRequestError", "not network error ", e);
                    }
                }
            });
        }
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 10001;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    public static boolean verifyStoragePermissions(Activity activity) {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                LogUtil.d("权限获取", "未获得权限");
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
                return false;
            } else {
                LogUtil.d("权限获取", "已有权限");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void saveData(LoginBean loginBean) {
        String pwd = etPassword.getText().toString();
        SessionKeeper.loginSave(LoginActivity.this, loginBean);
        SessionKeeper.keepUserPassword(LoginActivity.this, pwd.length() > 20 ? pwd : md5(pwd));
    }

    @OnClick(R.id.register_go)
    public void onFabClicked() {
//        getWindow().setExitTransition(null);
//        getWindow().setEnterTransition(null);
//        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, fab, fab.getTransitionName());
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    private void loginSuccess() {
        btGo.setClickable(true);
        SessionKeeper.keepAutoLogin(this, true);
        Explode explode = new Explode();
        explode.setDuration(500);

        getWindow().setExitTransition(explode);
        getWindow().setEnterTransition(explode);
        ActivityOptionsCompat oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation(LoginActivity.this);
        Intent i2 = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i2, oc2.toBundle());
        finish();
    }

    @OnClick(R.id.tv_forgot_password)
    public void onViewClicked() {
        startActivity(new Intent(LoginActivity.this, ChangePasswordActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE");
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                LogUtil.d("权限获取", "获取失败");
            } else {
                LogUtil.d("权限获取", "获取成功");
            }
        }
    }
}
