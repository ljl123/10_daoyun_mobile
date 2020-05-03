package com.example.daoyun.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.daoyun.HttpBean.DefaultResultBean;
import com.example.daoyun.HttpBean.DictInfoListBean;
import com.example.daoyun.HttpBean.RegisterBean;
import com.example.daoyun.R;
import com.example.daoyun.http.BaseObserver;
import com.example.daoyun.http.HttpUtil;
import com.example.daoyun.session.SessionKeeper;
import com.example.daoyun.utils.LogUtil;
import com.example.daoyun.utils.ToastUtil;
import com.example.daoyun.utils.Util;
import com.kongzue.dialog.v2.BottomMenu;
import com.kongzue.dialog.v2.DialogSettings;
import com.kongzue.dialog.v2.InputDialog;
import com.kongzue.dialog.v2.TipDialog;
import com.kongzue.dialog.v2.WaitDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {

    //发送验证码
    private static final int WHAT_SEND_EMAIL = 1001;
    //验证码正确
    private static final int WHAT_NETWORK_ERROR = 1002;
    //可重新发送短信
    private static final int WHAT_CAN_RESEND = 1003;
    //设置倒数时间
    private static final int WHAT_SET_TIME = 1004;
    //操作正确
    private static final int WHAT_OPERATION_SUCCESS = 1005;
    //操作失败
    private static final int WHAT_OPERATION_FAIL = 1006;
    //取消加载按钮动画
    private static final int WHAT_HIDE_LOADING = 1007;
    //注册成功 返回
    private static final int WHAT_BACK = 1008;
    @BindView(R.id.regist_back)
    Button registBack;
    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.et_verify_code)
    EditText etVerifyCode;
    @BindView(R.id.send_verify_code)
    Button sendVerifyCode;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.et_repeatpassword)
    EditText etRepeatpassword;
    @BindView(R.id.user_name)
    EditText userName;
    @BindView(R.id.et_user_code)
    EditText etUserCode;
    @BindView(R.id.et_user_school)
    EditText etUserSchool;
    @BindView(R.id.et_user_department)
    EditText etUserDepartment;
    @BindView(R.id.et_user_profession)
    EditText etUserProfession;
    @BindView(R.id.bt_go)
    Button btGo;
    @BindView(R.id.bt_switch_type)
    Button btSwitchType;
    @BindView(R.id.ll_stu)
    LinearLayout llStu;
    @BindView(R.id.ll_teacher)
    LinearLayout llTeacher;
    @BindView(R.id.act_user_school)
    AppCompatAutoCompleteTextView actUserSchool;
    @BindView(R.id.act_user_department)
    AppCompatAutoCompleteTextView actUserDepartment;
    @BindView(R.id.act_user_profession)
    AppCompatAutoCompleteTextView actUserProfession;

    Thread sendCodeThread;
    int count = 60;
    boolean typeStateStu = true;//是否注册学生版
    int schoolId = 0;
    int departmentId = 0;
    int professionId = 0;
    ArrayAdapter<String> adapterSchool;
    ArrayAdapter<String> adapterDepartment;
    ArrayAdapter<String> adapterProfession;
    DataForm schoolForm = new DataForm();
    DataForm departmentForm = new DataForm();
    DataForm professionForm = new DataForm();
    List<List<DictInfoListBean>> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        initData();
        initView();
        DialogSettings.style = DialogSettings.STYLE_MATERIAL;
        DialogSettings.tip_theme = DialogSettings.THEME_LIGHT;
        DialogSettings.dialog_theme = DialogSettings.THEME_LIGHT;
        DialogSettings.use_blur = false;
    }

    private void initData() {
        data.add(new ArrayList<>());
        data.add(new ArrayList<>());
        data.add(new ArrayList<>());
        HttpUtil.getDictInfo(SessionKeeper.getToken(this), getResources().getString(R.string.http_get_school_info), new BaseObserver<DictInfoListBean>() {
            @Override
            protected void onSuccess(DictInfoListBean dictInfoListBean) {
                if (dictInfoListBean.getResult_code().equals("200")) {
                    if (dictInfoListBean.getData() != null)
                        for (DictInfoListBean info : dictInfoListBean.getData())
                            data.get(info.getType_level() - 1).add(info);
                } else
                    ToastUtil.showMessage(RegisterActivity.this, dictInfoListBean.getResult_desc(), ToastUtil.LENGTH_SHORT);
            }

            @Override
            protected void onFailure(Throwable e, boolean isNetWorkError) {
                if (isNetWorkError)
                    ToastUtil.showMessage(RegisterActivity.this, "网络出错，请检查网络", ToastUtil.LENGTH_LONG);
                else
                    ToastUtil.showMessage(RegisterActivity.this, e.getMessage(), ToastUtil.LENGTH_LONG);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        switchType();
        actUserSchool.setOnTouchListener(this::onActUserSchoolTouched);
        actUserDepartment.setOnTouchListener(this::onActUserDepartmentTouched);
        actUserProfession.setOnTouchListener(this::onActUserProfessionTouched);
        setEditTextListener();
    }
    @OnClick(R.id.regist_back)
    public void onRegistBackClicked() {
        onBackPressed();
    }

    @OnClick(R.id.send_verify_code)
    public void onSendVerifyCodeClicked() {
        if (!etUsername.getText().toString().isEmpty() && etUsername.getError() == null) {
            mHandler.sendEmptyMessage(WHAT_SEND_EMAIL);
            sendEmail(etUsername.getText().toString());
        }
    }

    private void sendEmail(String email) {
        HttpUtil.sendEmail(email, new BaseObserver<DefaultResultBean<Object>>() {
            @Override
            protected void onSuccess(DefaultResultBean<Object> objectDefaultResultBean) {
                if (objectDefaultResultBean.getResult_code().equals("200")) {
                    ToastUtil.showMessage(RegisterActivity.this, "发送成功", ToastUtil.LENGTH_LONG);
                } else {
                    mHandler.sendEmptyMessage(WHAT_CAN_RESEND);
                    ToastUtil.showMessage(RegisterActivity.this, objectDefaultResultBean.getResult_desc(), ToastUtil.LENGTH_LONG);
                }
            }

            @Override
            protected void onFailure(Throwable e, boolean isNetWorkError) {
                mHandler.sendEmptyMessage(WHAT_CAN_RESEND);
                LogUtil.e("send email", "send email error", e);
                if (isNetWorkError)
                    ToastUtil.showMessage(RegisterActivity.this, "请求失败，请检查网络", ToastUtil.LENGTH_SHORT);
                else
                    ToastUtil.showMessage(RegisterActivity.this, "发送失败", ToastUtil.LENGTH_SHORT);
            }
        });
    }

    @OnClick(R.id.bt_go)
    public void onBtGoClicked() {
        if (checkInput()) {
            WaitDialog.show(RegisterActivity.this, "注册中...");
            Map<String, String> p = new HashMap<>();
            p.put("email", etUsername.getText().toString());
            p.put("email_code", etVerifyCode.getText().toString());
            p.put("phone", etPhone.getText().toString());
            p.put("password", Util.md5(etPassword.getText().toString()));
            p.put("type", typeStateStu ? "3" : "2");
            p.put("name", userName.getText().toString());
            p.put("stu_code", etUserCode.getText().toString());
            p.put("school", typeStateStu ? actUserSchool.getText().toString() : etUserSchool.getText().toString());
            p.put("department", typeStateStu ? actUserDepartment.getText().toString() : etUserDepartment.getText().toString());
            p.put("profession", typeStateStu ? actUserProfession.getText().toString() : etUserProfession.getText().toString());
            HttpUtil.registerUser(p, new BaseObserver<RegisterBean>() {
                @Override
                protected void onSuccess(RegisterBean registerBean) {
                    WaitDialog.dismiss();
                    if (registerBean.getResult_code().equals("200")) {
                        TipDialog.show(RegisterActivity.this, "注册成功", TipDialog.SHOW_TIME_SHORT, TipDialog.TYPE_FINISH);
                        mHandler.sendEmptyMessageDelayed(WHAT_BACK, 1500);
                    } else
                        TipDialog.show(RegisterActivity.this, registerBean.getResult_desc(), TipDialog.SHOW_TIME_SHORT, TipDialog.TYPE_ERROR);
                }

                @Override
                protected void onFailure(Throwable e, boolean isNetWorkError) {
                    WaitDialog.dismiss();
                    if (isNetWorkError)
                        TipDialog.show(RegisterActivity.this, "请检查网络连接", TipDialog.SHOW_TIME_SHORT, TipDialog.TYPE_ERROR);
                    else
                        TipDialog.show(RegisterActivity.this, e.getMessage(), TipDialog.SHOW_TIME_SHORT, TipDialog.TYPE_ERROR);
                }
            });
        }
    }

    private boolean checkInput() {
        if (etUsername.getError() != null) return false;
        if (etPassword.getError() != null) return false;
        if (etRepeatpassword.getError() != null) return false;
        return etPhone.getError() == null;
    }


    @OnClick(R.id.bt_switch_type)
    public void onViewClicked() {
        typeStateStu = !typeStateStu;
        switchType();
    }


    public boolean onActUserSchoolTouched(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (schoolForm.getInfo().size() == 0) {
                schoolForm.clear();
                schoolForm.add("手动输入", -1);
                if (data != null && data.get(0) != null)
                    for (DictInfoListBean info : data.get(0))
                        schoolForm.add(info.getInfo(), info.getId());
            }
            BottomMenu.show(RegisterActivity.this, schoolForm.getInfo(), (text, position) -> {
                schoolId = schoolForm.getIds().get(position);
                actUserSchool.setText(text);
                if (position == 0) {
                    actUserSchool.setText("");
                    InputDialog.show(RegisterActivity.this,
                            "学校名称",
                            "输入学校名称",
                            "确定", (dialog, inputText) -> {
                                actUserSchool.setText(inputText);
                                dialog.dismiss();
                            }, "取消", (dialog, which) -> {
                            });
                }
            });
        }
        return true;
    }

    public boolean onActUserDepartmentTouched(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (schoolId == -1) return showInputDepartment();
            departmentForm.clear();
            departmentForm.add("手动输入", -1);
            if (data != null && data.get(1) != null)
                for (DictInfoListBean info : data.get(1))
                    if (info.getType_belong() == schoolId)
                        departmentForm.add(info.getInfo(), info.getId());
            BottomMenu.show(RegisterActivity.this, departmentForm.getInfo(), (text, position) -> {
                departmentId = departmentForm.getIds().get(position);
                actUserDepartment.setText(text);
                if (position == 0) {
                    actUserDepartment.setText("");
                    showInputDepartment();
                }
            });
        }
        return true;
    }

    public boolean onActUserProfessionTouched(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (departmentId == -1) return showInputProfession();
            professionForm.clear();
            professionForm.add("手动输入", -1);
            if (data != null && data.get(2) != null)
                for (DictInfoListBean info : data.get(2))
                    if (info.getType_belong() == departmentId)
                        professionForm.add(info.getInfo(), info.getId());
            BottomMenu.show(RegisterActivity.this, professionForm.getInfo(), (text, position) -> {
                professionId = professionForm.getIds().get(position);
                actUserProfession.setText(text);
                if (position == 0) {
                    actUserProfession.setText("");
                    showInputProfession();
                }
            });
        }
        return true;
    }

    private boolean showInputProfession() {
        InputDialog.show(RegisterActivity.this,
                "专业名称",
                "输入专业名称",
                "确定", (dialog, inputText) -> {
                    dialog.dismiss();
                    actUserProfession.setText(inputText);
                }, "取消", (dialog, which) -> {
                });
        return true;
    }

    private boolean showInputDepartment() {
        InputDialog.show(RegisterActivity.this,
                "学院名称",
                "输入学院名称",
                "确定", (dialog, inputText) -> {
                    dialog.dismiss();
                    actUserDepartment.setText(inputText);
                }, "取消", (dialog, which) -> {
                });
        return true;
    }

    private void switchType() {
        btSwitchType.setText(typeStateStu ? "切换注册教师" : "切换注册学生");
        llStu.setVisibility(typeStateStu ? View.VISIBLE : View.GONE);
        llTeacher.setVisibility(typeStateStu ? View.GONE : View.VISIBLE);
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
                    ToastUtil.showMessage(RegisterActivity.this, "注册成功", ToastUtil.LENGTH_LONG);
                    mHandler.sendEmptyMessageDelayed(WHAT_BACK, 500);
                    break;
                case WHAT_OPERATION_FAIL:
                    ToastUtil.showMessage(RegisterActivity.this, (String) msg.obj, ToastUtil.LENGTH_LONG);
                    break;
                case WHAT_BACK:
                    RegisterActivity.this.onBackPressed();
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
    private void setEditTextListener() {
        etUsername.addTextChangedListener(new TextWatcher() {
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
                        etUsername.setError("请输入正确的邮箱");
                    } else {
                        etUsername.setError(null);
                    }
                }
            }
        });
        etPassword.addTextChangedListener(new TextWatcher() {
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
                        etPassword.setError(Util.Password_Rule);
                    } else {
                        etPassword.setError(null);
                    }
                }
            }
        });
        etRepeatpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    if (!s.toString().equals(etPassword.getText().toString())) {
                        etRepeatpassword.setError("两次输入的密码不同");
                    } else {
                        etRepeatpassword.setError(null);
                    }
                }
            }
        });
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    if (!Pattern.matches(Util.Phone_Reg, s.toString())) {
                        etPhone.setError("请输入正确的手机号");
                    } else {
                        etPhone.setError(null);
                    }
                }
            }
        });
    }
}

class DataForm {
    List<String> info;
    List<Integer> ids;

    DataForm() {
        info = new ArrayList<>();
        ids = new ArrayList<>();
    }

    public DataForm(List<String> info, List<Integer> ids) {
        this.info = info;
        this.ids = ids;
    }

    public List<String> getInfo() {
        return info;
    }

    public void setInfo(List<String> info) {
        this.info = info;
    }

    public List<Integer> getIds() {
        return ids;
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }

    public void clear() {
        info.clear();
        ids.clear();
    }

    public void add(String info, int belong) {
        this.info.add(info);
        this.ids.add(belong);
    }
}
