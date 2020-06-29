package com.example.ten_daoyun.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ten_daoyun.HttpBean.CoursesListBean;
import com.example.ten_daoyun.R;
import com.example.ten_daoyun.activities.CourseInfoActivity;
import com.example.ten_daoyun.activities.LoginActivity;
import com.example.ten_daoyun.adapters.CoursesListAdapter;
import com.example.ten_daoyun.http.BaseObserver;
import com.example.ten_daoyun.http.HttpUtil;
import com.example.ten_daoyun.session.SessionKeeper;
import com.example.ten_daoyun.utils.LogUtil;
import com.example.ten_daoyun.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    private static final int WHAT_GET_DATA_SUCCESS = 1;
    private static final int WHAT_GET_DATA_FAILED = 2;
    //    private static final int WHAT_GET_DATA_SUCCESS = 3;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.refresh_course_list)
    SwipeRefreshLayout mRefreshCourseList;
    @BindView(R.id.recycle_main)
    RecyclerView mRecycleView;
    @BindView(R.id.no_class_notify)
    TextView noClassNotify;

    private String mParam1;
    private String mParam2;

    Unbinder unbinder;

    CoursesListAdapter mAdapter;

    List<CoursesListBean> data = new ArrayList<>();


    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        if (data.size() == 0)
            getData();
        return view;
    }

    private void getData() {
        mRefreshCourseList.setRefreshing(true);
        data.clear();
        Map<String, String> params = new HashMap<>();
        params.put("token", SessionKeeper.getToken(getActivity()));
        params.put("uid", String.valueOf(SessionKeeper.getUserId(getActivity())));
        params.put("type", String.valueOf(SessionKeeper.getUserType(getActivity())));
        HttpUtil.getCoursesList(params, new BaseObserver<CoursesListBean>() {
            @Override
            protected void onSuccess(CoursesListBean coursesListBean) {
                if (coursesListBean.getResult_code().equals("200")) {
                    data.addAll(coursesListBean.getData());
                    mHandler.sendEmptyMessage(WHAT_GET_DATA_SUCCESS);
                    LogUtil.d("加载课程列表", "加载成功", LogUtil.LOG_DEBUG);
                } else {
                    ToastUtil.showMessage(getActivity(), coursesListBean.getResult_desc(), ToastUtil.LENGTH_LONG);
                    mHandler.sendEmptyMessage(WHAT_GET_DATA_FAILED);
                }
            }

            @Override
            protected void onFailure(Throwable e, boolean isNetWorkError) {
                mHandler.sendEmptyMessage(WHAT_GET_DATA_FAILED);
                if (isNetWorkError)
                    ToastUtil.showMessage(getActivity(), "网络错误", ToastUtil.LENGTH_LONG);
                else
                    ToastUtil.showMessage(getActivity(), e.getMessage(), ToastUtil.LENGTH_LONG);
            }
        });
    }

    private void initView() {
        mAdapter = new CoursesListAdapter(data, getActivity());
        mAdapter.setOnItemClickListener((view, position) -> {
            Intent intent = new Intent(getActivity(), CourseInfoActivity.class);
            intent.putExtra("course_id", String.valueOf(data.get(position).getCourse_id()));
            startActivity(intent);
        });
        mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycleView.setAdapter(mAdapter);
        mRefreshCourseList.setColorSchemeColors(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
        mRefreshCourseList.setOnRefreshListener(this::getData);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_GET_DATA_SUCCESS:
                    mAdapter.notifyDataSetChanged();
                    if (mRefreshCourseList != null && mRefreshCourseList.isRefreshing())
                        mRefreshCourseList.setRefreshing(false);
                    noClassNotify.setVisibility(mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
                    break;
                case WHAT_GET_DATA_FAILED:
                    if (mRefreshCourseList != null && mRefreshCourseList.isRefreshing())
                        mRefreshCourseList.setRefreshing(false);
                    ToastUtil.showMessage(getActivity(), "如是QQ登录失败，请换普通登录", ToastUtil.LENGTH_LONG);
                    //网络失败的话返回登录界面
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };
}
