package com.example.daoyun.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.droidbond.loadingbutton.LoadingButton;
import com.example.daoyun.HttpBean.DefaultResultBean;
import com.example.daoyun.R;
import com.example.daoyun.http.BaseObserver;
import com.example.daoyun.http.HttpUtil;
import com.example.daoyun.session.SessionKeeper;
import com.example.daoyun.utils.ToastUtil;
import com.example.daoyun.utils.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ModifyPasswordActivity extends AppCompatActivity {
    private static final int WHAT_OPERATION_SUCCESS = 1005;
    private static final int WHAT_OPERATION_FAIL = 1006;
    private static final int WHAT_HIDE_LOADING = 1007;
    private static final int WHAT_NETWORK_ERROR = 1002;
    private static final int WHAT_BACK = 1008;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.old_pwd)
    EditText oldPwd;
    @BindView(R.id.new_pwd)
    EditText newPwd;
    @BindView(R.id.repeat_pwd)
    EditText repeatPwd;
    @BindView(R.id.loading_button)
    LoadingButton loadingButton;
    @BindView(R.id.input_new_pwd)
    TextInputLayout inputNewPwd;
    @BindView(R.id.input_repeat_pwd)
    TextInputLayout inputRepeatPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        newPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    if (!Pattern.matches(Util.Password_Reg, s.toString())) {
                        inputNewPwd.setErrorEnabled(true);
                        inputNewPwd.setError(Util.Password_Rule);
                    } else {
                        inputNewPwd.setErrorEnabled(false);
                        inputNewPwd.setError(null);
                    }
                }
            }
        });
        repeatPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean hasError = false;
                if (!s.toString().equals("")) {
                    if (!Pattern.matches(Util.Password_Reg, s.toString())) {
                        inputRepeatPwd.setErrorEnabled(true);
                        inputRepeatPwd.setError(Util.Password_Rule);
                        hasError = true;
                    } else {
                        inputRepeatPwd.setError(null);
                        hasError = false;
                    }
                    if (!s.toString().equals(newPwd.getText().toString())) {
                        String pwd_str = String.valueOf(inputRepeatPwd.getError());
                        inputRepeatPwd.setError((!hasError ? "" : pwd_str + "\n") + "两次输入的密码不同");
                        hasError = true;
                    } else {
                        //两次密码正确
                        String e = String.valueOf(inputRepeatPwd.getError()).split("\n")[0];
                        if (hasError)
                            inputRepeatPwd.setError(e);
                    }
                    inputRepeatPwd.setErrorEnabled(hasError);
                    if (!hasError)
                        inputRepeatPwd.setError(null);
                }
            }
        });
    }

    @OnClick(R.id.loading_button)
    public void onViewClicked() {
        if (!loadingButton.isLoading()) {
            loadingButton.showLoading();
            if (checkInput())
                changePassword();
            else {
                loadingButton.showError();
                mHandler.sendEmptyMessageDelayed(WHAT_HIDE_LOADING, 1000);
            }
        }
    }

    private boolean checkInput() {
        if (newPwd.getError() != null) return false;
        return repeatPwd.getError() == null;
    }

    private void changePassword() {
        Map<String, String> params = new HashMap<>();
        params.put("email", SessionKeeper.getUserEmail(ModifyPasswordActivity.this));
        params.put("token", SessionKeeper.getToken(ModifyPasswordActivity.this));
        params.put("new_pwd", Util.md5(newPwd.getText().toString()));
        params.put("old_pwd", Util.md5(oldPwd.getText().toString()));
        HttpUtil.forgotPwd(params, new BaseObserver<DefaultResultBean<Object>>() {
            @Override
            protected void onSuccess(DefaultResultBean<Object> objectDefaultResultBean) {
                if (objectDefaultResultBean.getResult_code().equals("200")) {
                    mHandler.sendEmptyMessage(WHAT_OPERATION_SUCCESS);
                } else {
                    Message msg = new Message();
                    msg.what = WHAT_OPERATION_FAIL;
                    msg.obj = objectDefaultResultBean.getResult_desc();
                    mHandler.sendMessage(msg);
                }
            }

            @Override
            protected void onFailure(Throwable e, boolean isNetWorkError) {
                if (isNetWorkError) {
                    mHandler.sendEmptyMessage(WHAT_NETWORK_ERROR);
                } else {
                    Message msg = new Message();
                    msg.what = WHAT_OPERATION_FAIL;
                    msg.obj = e.getMessage();
                    mHandler.sendMessage(msg);
                }
            }
        });
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_OPERATION_SUCCESS:
                    ToastUtil.showMessage(ModifyPasswordActivity.this, "操作成功", ToastUtil.LENGTH_LONG);
                    loadingButton.showSuccess();
                    mHandler.sendEmptyMessageDelayed(WHAT_BACK, 500);
                    break;
                case WHAT_OPERATION_FAIL:
                    ToastUtil.showMessage(ModifyPasswordActivity.this, (String) msg.obj, ToastUtil.LENGTH_LONG);
                    loadingButton.showError();
                    mHandler.sendEmptyMessageDelayed(WHAT_HIDE_LOADING, 1000);
                    break;
                case WHAT_HIDE_LOADING:
                    loadingButton.hideLoading();
                    break;
                case WHAT_BACK:
                    ModifyPasswordActivity.this.onBackPressed();
                    break;
            }
        }
    };
}
