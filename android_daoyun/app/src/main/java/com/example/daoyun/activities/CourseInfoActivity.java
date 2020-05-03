package com.example.daoyun.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.daoyun.HttpBean.CourseInfoBean;
import com.example.daoyun.HttpBean.DefaultResultBean;
import com.example.daoyun.HttpBean.StudentsListBean;
import com.example.daoyun.R;
import com.example.daoyun.adapters.StudentListAdapter;
import com.example.daoyun.http.BaseObserver;
import com.example.daoyun.http.HttpBase;
import com.example.daoyun.http.HttpUtil;
import com.example.daoyun.session.SessionKeeper;
import com.example.daoyun.utils.GPSUtils;
import com.example.daoyun.utils.LogUtil;
import com.example.daoyun.utils.ToastUtil;
import com.kongzue.dialog.v2.BottomMenu;
import com.kongzue.dialog.v2.InputDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CourseInfoActivity extends AppCompatActivity {
    private static final int WHAT_REQUEST_FAILED = 1;
    private static final int WHAT_CHECK_SUCCESS = 2;
    private static final int WHAT_CHECK_FAILED = 3;
    private static final int WHAT_NETWORK_ERROR = 4;
    private static final int WHAT_GET_STUDENTS_SUCCESS = 5;
    private static final int WHAT_START_CHECK_SUCCESS = 6;
    private static final int WHAT_START_CHECK_FAILED = 7;
    private static final int WHAT_GET_CAN_CHECK_SUCCESS = 8;
    private static final int WHAT_GET_CAN_CHECK_FAILED = 9;
    private static final int REQUEST_TAKE_MEDIA = 102;


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.course_name)
    TextView courseName;
    @BindView(R.id.teacher_name)
    TextView teacherName;
    @BindView(R.id.course_code)
    TextView courseCode;
    @BindView(R.id.stu_count)
    TextView stuCount;
    @BindView(R.id.sign_count)
    TextView signCount;
    @BindView(R.id.text_use_web)
    TextView textUseWeb;
    @BindView(R.id.check)
    Button check;
    @BindView(R.id.recycle_view)
    RecyclerView recycleView;
    @BindView(R.id.notify_face)
    TextView notifyFace;
    @BindView(R.id.place)
    TextView place;

    String courseId;
    String userType;

    List<StudentsListBean> students = new ArrayList<>();
    StudentListAdapter mAdapter = new StudentListAdapter(students, this);
    //默认1分钟
    long duration_time = 1000 * 60;

    ProgressDialog progressDialog;


    boolean canCheck = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_info);
        ButterKnife.bind(this);
        userType = SessionKeeper.getUserType(this);
        courseId = getIntent().getStringExtra("course_id");
        initView();
        initData();
//        checkFaceInfo();
    }

    private void initData() {
        HttpUtil.getCourseInfo(SessionKeeper.getToken(this), courseId, new BaseObserver<CourseInfoBean>() {
            @Override
            protected void onSuccess(CourseInfoBean courseInfoBean) {
                if (courseInfoBean.getResult_code().equals("200")) {
                    courseName.setText("课程名称: " + courseInfoBean.getData().getCourse_name());
                    courseCode.setText("课程代码: " + courseInfoBean.getData().getCourse_code());
                    teacherName.setText("任课教师: " + courseInfoBean.getData().getTeacher());
                    stuCount.setText("学生数: " + String.valueOf(courseInfoBean.getData().getStu_count()));
                    place.setText("上课地点："+courseInfoBean.getData().getPlace());
                    if (userType.equals("3")) {
                        signCount.setVisibility(View.GONE);
                        textUseWeb.setVisibility(View.GONE);
                    } else if (userType.equals("2")) {
                        signCount.setVisibility(View.VISIBLE);
                        signCount.setText("点名次数: " + String.valueOf(courseInfoBean.getData().getCheck_count()));
                        textUseWeb.setVisibility(View.VISIBLE);
                        if (!String.valueOf(courseInfoBean.getData().getCreater_uid()).equals(SessionKeeper.getUserId(getActivityContext())) && (userType.equals("2") || userType.equals("1"))) {
                            check.setClickable(false);
                            check.setVisibility(View.GONE);
                        } else if (userType.equals("2")) {
                            if (!canCheck) {
                                check.setText("发起签到");
                                check.setClickable(true);
                                check.setVisibility(View.VISIBLE);
                            } else {
                                check.setText("签到中");
                                check.setClickable(false);
                                check.setVisibility(View.VISIBLE);
                            }
                        }

                    }

                } else {
                    ToastUtil.showMessage(getActivityContext(), courseInfoBean.getResult_desc());
                    check.setClickable(false);
                    check.setVisibility(View.GONE);
                }

            }

            @Override
            protected void onFailure(Throwable e, boolean isNetWorkError) {
                check.setClickable(false);
                check.setVisibility(View.GONE);
                if (isNetWorkError) {
                    mHandler.sendEmptyMessage(WHAT_NETWORK_ERROR);
                } else {
                    LogUtil.e("get course info", e.getMessage());
                    mHandler.sendEmptyMessage(WHAT_REQUEST_FAILED);
                }
            }
        });
        HttpUtil.getStudentsList(SessionKeeper.getToken(this), courseId, new BaseObserver<StudentsListBean>() {
            @Override
            protected void onSuccess(StudentsListBean studentsListBean) {
                if (studentsListBean.getResult_code().equals("200")) {
                    students.clear();
                    students.addAll(studentsListBean.getData());
                    mHandler.sendEmptyMessage(WHAT_GET_STUDENTS_SUCCESS);
                } else {
                    mHandler.sendEmptyMessage(WHAT_REQUEST_FAILED);
                }

            }

            @Override
            protected void onFailure(Throwable e, boolean isNetWorkError) {
                if (isNetWorkError) {
                    mHandler.sendEmptyMessage(WHAT_NETWORK_ERROR);
                } else {
                    LogUtil.e("get student list info", e.getMessage());
                    mHandler.sendEmptyMessage(WHAT_REQUEST_FAILED);
                }
            }
        });
    }

    private void setButtonState() {
        Map<String, String> params = new HashMap<>();
        params.put("token", SessionKeeper.getToken(getActivityContext()));
        params.put("course_id", courseId);
        HttpUtil.canCheck(params, new BaseObserver<DefaultResultBean<Boolean>>() {
            @Override
            protected void onSuccess(DefaultResultBean<Boolean> booleanDefaultResultBean) {
                if (booleanDefaultResultBean.getResult_code().equals("200")) {
                    canCheck = booleanDefaultResultBean.getData();
                    Message m = new Message();
                    m.what = WHAT_GET_CAN_CHECK_SUCCESS;
                    m.arg1 = booleanDefaultResultBean.getData() ? 1 : 0;
                    mHandler.sendMessage(m);
                } else {
                    mHandler.sendEmptyMessage(WHAT_GET_CAN_CHECK_FAILED);
                }
            }

            @Override
            protected void onFailure(Throwable e, boolean isNetWorkError) {
                canCheck = false;
                if (isNetWorkError) {
                    mHandler.sendEmptyMessage(WHAT_NETWORK_ERROR);
                } else {
                    LogUtil.e("get can check info", e.getMessage());
                    mHandler.sendEmptyMessage(WHAT_GET_CAN_CHECK_FAILED);
                }
            }
        });
    }

    private void initView() {
        setButtonState();
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        check.setText(userType.equals("3") ? "签到" : "发起签到");
        recycleView.setLayoutManager(new LinearLayoutManager(this));
        recycleView.setAdapter(mAdapter);
        recycleView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在签到.....");
        progressDialog.setCancelable(false);
    }

    private void checkFaceInfo() {
        if (!userType.equals("3")) return;
        HttpUtil.getFaceExist(SessionKeeper.getToken(CourseInfoActivity.this), new BaseObserver<DefaultResultBean<Object>>() {
            @Override
            protected void onSuccess(DefaultResultBean<Object> objectDefaultResultBean) {
                if (!objectDefaultResultBean.getResult_code().equals("200")) {
                    check.setClickable(false);
                   // check.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    notifyFace.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected void onFailure(Throwable e, boolean isNetWorkError) {
                if (isNetWorkError) {
                    mHandler.sendEmptyMessage(WHAT_NETWORK_ERROR);
                } else {
                    mHandler.sendEmptyMessage(WHAT_REQUEST_FAILED);
                }
            }
        });
    }

    @OnClick(R.id.check)
    public void onCheckClicked() {
        if (userType.equals("3")) {
            sign();
        } else {
            BottomMenu.show(CourseInfoActivity.this, new String[]{"选择持续时间", "1分钟", "2分钟", "5分钟", "10分钟", "30分钟", "手动输入"}, ((text, index) -> {
                if (index != 0) {
                    onTimePicked(index);
                }
            }), true);
        }
    }

    private void onTimePicked(int index) {
        switch (index) {
            case 2:
                duration_time = 2 * 1000 * 60;
                break;
            case 3:
                duration_time = 5 * 1000 * 60;
                break;
            case 4:
                duration_time = 10 * 1000 * 60;
                break;
            case 5:
                duration_time = 30 * 1000 * 60;
                break;
            case 6:
                InputDialog.show(getActivityContext(),
                        "输入持续时间",
                        "输入为分钟数",
                        "确定", (dialog, inputText) -> {
                            if (inputText.isEmpty())
                                ToastUtil.showMessage(getActivityContext(), "不能为空");
                            else {
                                Pattern p = Pattern.compile("[0-9]*");
                                if (p.matcher(inputText).matches()) {
                                    //noinspection ConstantConditions
                                    duration_time = (long) Double.parseDouble(inputText) * 60 * 1000;
//                                    Log.d("duration_time",String.valueOf(duration_time));
                                    dialog.dismiss();
                                    startCheck();
                                } else {
                                    ToastUtil.showMessage(getActivityContext(), "请输入数字");
                                }
                            }
                        },
                        "取消", (dialog, which) -> {
                            duration_time = 1000 * 60;
                        });
                break;
            default:
                duration_time = 1000 * 60;
        }
        if (index != 6) startCheck();
    }

    /**
     * 发起签到
     */
    private void startCheck() {
        Calendar calendar = Calendar.getInstance();
        Map<String, String> params = new HashMap<>();
        params.put("token", SessionKeeper.getToken(getActivityContext()));
        params.put("course_id", courseId);
        params.put("start_time", String.valueOf(calendar.getTimeInMillis()));
        params.put("duration", String.valueOf(duration_time));
        HttpUtil.startCheck(params, new BaseObserver<DefaultResultBean<Object>>() {
            @Override
            protected void onSuccess(DefaultResultBean<Object> objectDefaultResultBean) {
                if (objectDefaultResultBean.getResult_code().equals("200"))
                    mHandler.sendEmptyMessage(WHAT_START_CHECK_SUCCESS);
                else
                    mHandler.sendEmptyMessage(WHAT_START_CHECK_FAILED);
            }

            @Override
            protected void onFailure(Throwable e, boolean isNetWorkError) {
                if (isNetWorkError) {
                    mHandler.sendEmptyMessage(WHAT_NETWORK_ERROR);
                } else {
                    LogUtil.e("start check", e.getMessage());
                    mHandler.sendEmptyMessage(WHAT_START_CHECK_FAILED);
                }
            }
        });
    }

    /**
     * 签到
     */
    private void sign() {
        sendSignRequest();
    }

    private void sendSignRequest() {
        progressDialog.show();
        String now = String.valueOf(Calendar.getInstance().getTimeInMillis());
        Map<String, String> params = new HashMap<>();
        params.put("token", SessionKeeper.getToken(getActivityContext()));
        params.put("uid", SessionKeeper.getUserId(getActivityContext()));
        params.put("course_id", courseId);
        params.put("time", now);
        params.put("location", GPSUtils.getInstance(this).getLocationString());
        HttpUtil.check(params, new BaseObserver<DefaultResultBean<Boolean>>() {
            @Override
            protected void onSuccess(DefaultResultBean<Boolean> booleanDefaultResultBean) {
                if (booleanDefaultResultBean.getResult_code().equals("200")) {
                    if (booleanDefaultResultBean.getData()) {
                        ToastUtil.showMessage(getActivityContext(), "签到成功");
                        mHandler.sendEmptyMessage(WHAT_CHECK_SUCCESS);
                    } else {
                        mHandler.sendEmptyMessage(WHAT_CHECK_FAILED);
                        ToastUtil.showMessage(getActivityContext(), booleanDefaultResultBean.getResult_desc());
                    }
                } else {
                    mHandler.sendEmptyMessage(WHAT_CHECK_FAILED);
                    ToastUtil.showMessage(getActivityContext(), booleanDefaultResultBean.getResult_desc());
                }
            }

            @Override
            protected void onFailure(Throwable e, boolean isNetWorkError) {
                if (isNetWorkError) {
                    mHandler.sendEmptyMessage(WHAT_NETWORK_ERROR);
                } else {
                    LogUtil.e("check", e.getMessage());
                    mHandler.sendEmptyMessage(WHAT_CHECK_FAILED);
                    ToastUtil.showMessage(getActivityContext(), "签到失败");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    Context getActivityContext() {
        return this;
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_REQUEST_FAILED:
                    ToastUtil.showMessage(getActivityContext(), "请求失败，请重试");
                    break;
                case WHAT_CHECK_SUCCESS:
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                    check.setText("已签到");
                    check.setClickable(false);
                    break;
                case WHAT_CHECK_FAILED:
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                    break;
                case WHAT_NETWORK_ERROR:
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                    ToastUtil.showNetworkErrorMsg(getActivityContext());
                    break;
                case WHAT_GET_STUDENTS_SUCCESS:
                    mAdapter.notifyDataSetChanged();
                    break;
                case WHAT_START_CHECK_SUCCESS:
                    ToastUtil.showMessage(getActivityContext(), "开始签到成功");
                    setButtonState();
                    break;
                case WHAT_START_CHECK_FAILED:
                    ToastUtil.showMessage(getActivityContext(), "开始签到失败,请重试");
                    break;
                case WHAT_GET_CAN_CHECK_SUCCESS:
                    int canCheck = msg.arg1;
                    if (userType.equals("3")) {
                        if (canCheck == 0) {
                            check.setText("无签到");
                            check.setClickable(false);
                            //check.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        }
                    } else {
                        if (canCheck == 1) {
                            check.setText("签到中");
                            check.setClickable(false);
                            //check.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        } else {
                            check.setText("发起签到");
                            check.setClickable(true);
                           // check.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        }
                    }
                    break;
                case WHAT_GET_CAN_CHECK_FAILED:
                    check.setText("无法操作");
                    check.setClickable(false);
                  //  check.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_TAKE_MEDIA:
                if (resultCode == RESULT_OK) {
                    sendSignRequest();
                } else {
                    ToastUtil.showMessage(CourseInfoActivity.this, "拍照失败");
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
