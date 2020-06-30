package com.example.ten_daoyun.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.droidbond.loadingbutton.LoadingButton;
import com.example.ten_daoyun.httpBean.DefaultResultBean;
import com.example.ten_daoyun.httpBean.DictInfoListBean;
import com.example.ten_daoyun.httpBean.LoginBean;
import com.example.ten_daoyun.R;
import com.example.ten_daoyun.http.BaseObserver;
import com.example.ten_daoyun.http.HttpUtil;
import com.example.ten_daoyun.session.SessionKeeper;
import com.example.ten_daoyun.utils.ToastUtil;
import com.kongzue.dialog.v2.BottomMenu;
import com.kongzue.dialog.v2.DialogSettings;
import com.kongzue.dialog.v2.InputDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserInfoActivity extends AppCompatActivity {
    private static final int WHAT_SAVE_SUCCESS = 1;
    private static final int WHAT_SAVE_FAILED = 2;
    private static final int WHAT_QUIT = 3;
    @BindView(R.id.nick_name)
    EditText nickName;
    @BindView(R.id.phone)
    EditText phone;
    @BindView(R.id.gender)
    EditText gender;
    @BindView(R.id.stu_code)
    EditText stuCode;
    @BindView(R.id.school)
    EditText school;
    @BindView(R.id.department)
    EditText department;
    @BindView(R.id.profession)
    EditText profession;
    @BindView(R.id.save)
    LoadingButton save;
    @BindView(R.id.school_input)
    TextInputLayout schoolInput;
    @BindView(R.id.department_input)
    TextInputLayout departmentInput;
    @BindView(R.id.profession_input)
    TextInputLayout professionInput;

    String uid = "";
    LoginBean user;
    int schoolId = 0;
    int departmentId = 0;
    int professionId = 0;
    DataForm schoolForm = new DataForm();
    DataForm departmentForm = new DataForm();
    DataForm professionForm = new DataForm();
    List<List<DictInfoListBean>> data = new ArrayList<>();
    @BindView(R.id.userinfo_back)
    Button userinfodBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        uid = getIntent().getStringExtra("uid");
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
                    ToastUtil.showMessage(UserInfoActivity.this, dictInfoListBean.getResult_desc(), ToastUtil.LENGTH_SHORT);
            }

            @Override
            protected void onFailure(Throwable e, boolean isNetWorkError) {
                if (isNetWorkError)
                    ToastUtil.showMessage(UserInfoActivity.this, "网络出错，请检查网络", ToastUtil.LENGTH_LONG);
                else
                    ToastUtil.showMessage(UserInfoActivity.this, e.getMessage(), ToastUtil.LENGTH_LONG);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        user = SessionKeeper.getUserInfo(this);
        nickName.setText(user.getNick_name());
        phone.setText(user.getPhone());
        gender.setText(user.getGender());
        stuCode.setText(user.getStu_code());
        school.setText(user.getSchool());
        department.setText(user.getDepartment());
        profession.setText(user.getProfession());
        school.setOnTouchListener(this::onActUserSchoolTouched);
        department.setOnTouchListener(this::onActUserDepartmentTouched);
        profession.setOnTouchListener(this::onActUserProfessionTouched);
    }
    @OnClick(R.id.userinfo_back)
    public void onUserinfodBackClicked() {
        onBackPressed();
    }
    @OnClick(R.id.save)
    public void onSaveClicked() {
        save.setClickable(false);
        Map<String, String> params = new HashMap<>();
        params.put("token", SessionKeeper.getToken(getApplicationContext()));
        params.put("uid", uid);
        params.put("nick_name", nickName.getText().toString());
        params.put("phone", phone.getText().toString());
        params.put("gender", gender.getText().toString());
        params.put("stu_code", stuCode.getText().toString());
        params.put("school", school.getText().toString());
        params.put("department", department.getText().toString());
        params.put("profession", profession.getText().toString());
        HttpUtil.modifyUserInfo(params, new BaseObserver<DefaultResultBean<Object>>() {
            @Override
            protected void onSuccess(DefaultResultBean<Object> objectDefaultResultBean) {
                if (objectDefaultResultBean.getResult_code().equals("200"))
                    mHandler.sendEmptyMessage(WHAT_SAVE_SUCCESS);
                else {
                    mHandler.sendEmptyMessage(WHAT_SAVE_FAILED);
                    ToastUtil.showMessage(UserInfoActivity.this, objectDefaultResultBean.getResult_desc());
                }
            }

            @Override
            protected void onFailure(Throwable e, boolean isNetWorkError) {
                mHandler.sendEmptyMessage(WHAT_SAVE_FAILED);
                if (isNetWorkError)
                    ToastUtil.showMessage(UserInfoActivity.this, "网络出错，请检查网络", ToastUtil.LENGTH_LONG);
                else
                    ToastUtil.showMessage(UserInfoActivity.this, e.getMessage(), ToastUtil.LENGTH_LONG);
            }
        });
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
            BottomMenu.show(UserInfoActivity.this, schoolForm.getInfo(), (text, position) -> {
                schoolId = schoolForm.getIds().get(position);
                school.setText(text);
                if (position == 0) {
                    school.setText("");
                    InputDialog.show(UserInfoActivity.this,
                            "学校名称",
                            "输入学校名称",
                            "确定", (dialog, inputText) -> {
                                school.setText(inputText);
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
            BottomMenu.show(UserInfoActivity.this, departmentForm.getInfo(), (text, position) -> {
                departmentId = departmentForm.getIds().get(position);
                department.setText(text);
                if (position == 0) {
                    department.setText("");
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
            BottomMenu.show(UserInfoActivity.this, professionForm.getInfo(), (text, position) -> {
                professionId = professionForm.getIds().get(position);
                profession.setText(text);
                if (position == 0) {
                    profession.setText("");
                    showInputProfession();
                }
            });
        }
        return true;
    }

    private boolean showInputProfession() {
        InputDialog.show(UserInfoActivity.this,
                "专业名称",
                "输入专业名称",
                "确定", (dialog, inputText) -> {
                    dialog.dismiss();
                    profession.setText(inputText);
                }, "取消", (dialog, which) -> {
                });
        return true;
    }

    private boolean showInputDepartment() {
        InputDialog.show(UserInfoActivity.this,
                "学院名称",
                "输入学院名称",
                "确定", (dialog, inputText) -> {
                    dialog.dismiss();
                    department.setText(inputText);
                }, "取消", (dialog, which) -> {
                });
        return true;
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_SAVE_SUCCESS:
                    save.setClickable(true);
                    ToastUtil.showMessage(UserInfoActivity.this, "保存成功");
                    saveUserInfo();
                    break;
                case WHAT_SAVE_FAILED:
                    save.setClickable(true);
                    break;
                case WHAT_QUIT:
                    finish();
                    break;
            }
        }
    };

    private void saveUserInfo() {
        SessionKeeper.keepUserNickName(this,nickName.getText().toString());
        LoginBean bean = SessionKeeper.getUserInfo(this);
        bean.setNick_name(nickName.getText().toString());
        bean.setPhone(phone.getText().toString());
        bean.setGender(gender.getText().toString());
        bean.setStu_code(stuCode.getText().toString());
        bean.setSchool(school.getText().toString());
        bean.setDepartment(department.getText().toString());
        bean.setProfession(profession.getText().toString());
        SessionKeeper.keepUserInfo(this,bean);
        mHandler.sendEmptyMessageDelayed(WHAT_QUIT,1000);
    }
}
