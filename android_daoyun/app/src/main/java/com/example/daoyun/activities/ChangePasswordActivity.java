package com.example.daoyun.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.droidbond.loadingbutton.LoadingButton;
import com.example.daoyun.HttpBean.DefaultResultBean;
import com.example.daoyun.R;
import com.example.daoyun.http.BaseObserver;
import com.example.daoyun.http.HttpUtil;
import com.example.daoyun.utils.LogUtil;
import com.example.daoyun.utils.ToastUtil;
import com.example.daoyun.utils.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangePasswordActivity extends AppCompatActivity {
    private static final int WHAT_SEND_EMAIL = 1001;
    private static final int WHAT_NETWORK_ERROR = 1002;
    private static final int WHAT_CAN_RESEND = 1003;
    private static final int WHAT_SET_TIME = 1004;
    private static final int WHAT_OPERATION_SUCCESS = 1005;
    private static final int WHAT_OPERATION_FAIL = 1006;
    private static final int WHAT_HIDE_LOADING = 1007;
    private static final int WHAT_BACK = 1008;

    @BindView(R.id.forget_password_back)
    Button forgetPasswordBack;
    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.et_verify_code)
    EditText etVerifyCode;
    @BindView(R.id.send_verify_code)
    Button sendVerifyCode;
    @BindView(R.id.et_new_pwd)
    EditText etNewPwd;
    @BindView(R.id.et_repeat_password)
    EditText etRepeatPassword;
    @BindView(R.id.loading_button)
    LoadingButton loadingButton;
    @BindView(R.id.text_input_email)
    TextInputLayout textInputEmail;
    @BindView(R.id.text_input_verify_code)
    TextInputLayout textInputVerifyCode;
    @BindView(R.id.text_input_new_pwd)
    TextInputLayout textInputNewPwd;
    @BindView(R.id.text_input_repeat_pwd)
    TextInputLayout textInputRepeatPwd;

    Thread sendCodeThread;
    int count = 60;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
        setListener();
    }


    @OnClick(R.id.forget_password_back)
    public void onForgetPasswordBackClicked() {
        onBackPressed();
    }

    @OnClick(R.id.send_verify_code)
    public void onSendVerifyCodeClicked() {
        if (!etEmail.getText().toString().isEmpty() && etEmail.getError() == null){
            sendEmail(etEmail.getText().toString());
            mHandler.sendEmptyMessage(WHAT_SEND_EMAIL);
        }
    }

    @OnClick(R.id.loading_button)
    public void onLoadingButtonClicked() {
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

    private void sendEmail(String email) {
        HttpUtil.sendEmail(email, new BaseObserver<DefaultResultBean<Object>>() {
            @Override
            protected void onSuccess(DefaultResultBean<Object> objectDefaultResultBean) {
                if (objectDefaultResultBean.getResult_code().equals("200")) {
                    ToastUtil.showMessage(ChangePasswordActivity.this, "发送成功", ToastUtil.LENGTH_LONG);
                } else {
                    mHandler.sendEmptyMessage(WHAT_CAN_RESEND);
                    ToastUtil.showMessage(ChangePasswordActivity.this, objectDefaultResultBean.getResult_desc(), ToastUtil.LENGTH_LONG);
                }
            }

            @Override
            protected void onFailure(Throwable e, boolean isNetWorkError) {
                mHandler.sendEmptyMessage(WHAT_CAN_RESEND);
                LogUtil.e("send email", "send email error", e);
                if (isNetWorkError)
                    ToastUtil.showMessage(ChangePasswordActivity.this, "请求失败，请检查网络", ToastUtil.LENGTH_SHORT);
                else
                    ToastUtil.showMessage(ChangePasswordActivity.this, "发送失败", ToastUtil.LENGTH_SHORT);
            }
        });
    }

    private boolean checkInput() {
        if (textInputEmail.getError() != null) return false;
        if (textInputNewPwd.getError() != null)
            return false;
        return textInputRepeatPwd.getError() == null;
    }

    private void changePassword() {
        Map<String, String> params = new HashMap<>();
        params.put("email", etEmail.getText().toString());
        params.put("email_code", etVerifyCode.getText().toString());
        params.put("new_pwd", Util.md5(etRepeatPassword.getText().toString()));
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
            super.handleMessage(msg);
            switch (msg.what) {
                //点击发送验证码后 按钮文字改为提示多久可再次发送
                case WHAT_SEND_EMAIL:
                    sendVerifyCode.setClickable(false);
                    sendCodeThread = new Thread(canSendCode);
                    sendCodeThread.start();
                    break;
                //设置为可再发送
                case WHAT_CAN_RESEND:
                    count = 60;
                    sendVerifyCode.setText("发送验证码");
                    sendVerifyCode.setClickable(true);
                    break;
                //设置倒数时间
                case WHAT_SET_TIME:
                    String s = count + "秒";
                    sendVerifyCode.setText(s);
                    count--;
                    break;
                case WHAT_OPERATION_SUCCESS:
                    ToastUtil.showMessage(ChangePasswordActivity.this, "操作成功", ToastUtil.LENGTH_LONG);
                    loadingButton.showSuccess();
                    mHandler.sendEmptyMessageDelayed(WHAT_BACK, 500);
                    break;
                case WHAT_OPERATION_FAIL:
                    ToastUtil.showMessage(ChangePasswordActivity.this, (String) msg.obj, ToastUtil.LENGTH_LONG);
                    loadingButton.showError();
                    mHandler.sendEmptyMessageDelayed(WHAT_HIDE_LOADING, 1000);
                    break;
                case WHAT_HIDE_LOADING:
                    loadingButton.hideLoading();
                    break;
                case WHAT_BACK:
                    ChangePasswordActivity.this.onBackPressed();
                    break;
            }
        }
    };

    /**
     * 发送验证码后倒数的线程
     */
    Runnable canSendCode = () -> {
        while (true) {
            if (count != 0) {
                mHandler.sendEmptyMessage(WHAT_SET_TIME);
            } else {
                mHandler.sendEmptyMessage(WHAT_CAN_RESEND);
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        if (sendCodeThread != null && !sendCodeThread.isInterrupted())
            sendCodeThread.interrupt();
    }

    private void setListener() {
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    if (!Pattern.matches(Util.Email_Reg, s.toString())) {
                        textInputEmail.setErrorEnabled(true);
                        textInputEmail.setError("邮箱格式不正确");
                    } else {
                        textInputEmail.setErrorEnabled(false);
                        textInputEmail.setError(null);
                    }
                }
            }
        });
        etNewPwd.addTextChangedListener(new TextWatcher() {
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
                        textInputNewPwd.setErrorEnabled(true);
                        textInputNewPwd.setError(Util.Password_Rule);
                    } else {
                        textInputNewPwd.setErrorEnabled(false);
                        textInputNewPwd.setError(null);
                    }
                }
            }
        });

        etRepeatPassword.addTextChangedListener(new TextWatcher() {
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
                        textInputRepeatPwd.setErrorEnabled(true);
                        textInputRepeatPwd.setError(Util.Password_Rule);
                        hasError = true;
                    } else {
                        textInputRepeatPwd.setError(null);
                        hasError = false;
                    }
                    if (!s.toString().equals(etNewPwd.getText().toString())) {
                        String pwd_str = String.valueOf(textInputRepeatPwd.getError());
                        textInputRepeatPwd.setError((!hasError ? "" : pwd_str + "\n") + "两次输入的密码不同");
                        hasError = true;
                    } else {
                        //两次密码正确
                        String e = String.valueOf(textInputRepeatPwd.getError()).split("\n")[0];
                        if (hasError)
                            textInputRepeatPwd.setError(e);
                    }
                    textInputRepeatPwd.setErrorEnabled(hasError);
                    if (!hasError)
                        textInputRepeatPwd.setError(null);
                }
            }
        });
    }
}
