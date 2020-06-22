package com.example.daoyun.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.daoyun.HttpBean.DefaultResultBean;
import com.example.daoyun.HttpBean.SearchListBean;
import com.example.daoyun.R;
import com.example.daoyun.activities.CourseInfoActivity;
import com.example.daoyun.activities.CreateCourseActivity;
import com.example.daoyun.adapters.SearchListAdapter;
import com.example.daoyun.http.BaseObserver;
import com.example.daoyun.http.HttpUtil;
import com.example.daoyun.session.SessionKeeper;
import com.example.daoyun.utils.LogUtil;
import com.example.daoyun.utils.ToastUtil;
import com.example.daoyun.zxing.android.CaptureActivity;
import com.google.gson.Gson;
import com.kongzue.dialog.v2.TipDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddCourseFragment extends Fragment implements SearchListAdapter.OnListListener, SearchView.OnQueryTextListener {
    private static final int WHAT_GET_DATA_SUCCESS = 1;
    private static final int WHAT_GET_DATA_FAILED = 2;
    private static final int WHAT_ADD_COURSE_FAILED = 3;
    private static final int WHAT_ADD_COURSE_SUCCESS = 4;
    @BindView(R.id.search_view)
    SearchView searchView;
    Unbinder unbinder;

    String searchString = "";
    @BindView(R.id.recycle_view)
    RecyclerView recycleView;
    @BindView(R.id.refresh_view)
    SwipeRefreshLayout refreshView;

    SearchListAdapter mAdapter;

    int page = 1;
    int page_size = 10;
    int oldState;

    boolean noMoreData = false;
    boolean loadingAppendData = false;

    List<SearchListBean> data = new ArrayList<>();
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.focus)
    LinearLayout focus;
    //二维码扫描
    @BindView(R.id.scan)
    Button scan;
    private static final String DECODED_CONTENT_KEY = "codedContent";
    private static final String DECODED_BITMAP_KEY = "codedBitmap";
    private static final int REQUEST_CODE_SCAN = 0x0000;
    @OnClick(R.id.scan)
    public void onScanClicked() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            goScan();
        }
    }
    /**
     * 跳转到扫码界面扫码
     */
    private void goScan(){
        Intent intent = new Intent(getActivity(), CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SCAN);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    goScan();
                } else {
                    ToastUtil.showMessage(getActivity(), "您拒绝了权限申请，可能无法打开相机扫码", ToastUtil.LENGTH_LONG);
                }
                break;
            default:
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == getActivity().RESULT_OK) {
            if (data != null) {
                //返回的文本内容
                String content = data.getStringExtra(DECODED_CONTENT_KEY);
                //返回的BitMap图像
                Bitmap bitmap = data.getParcelableExtra(DECODED_BITMAP_KEY);
                ToastUtil.showMessage(getActivity(),content, ToastUtil.LENGTH_LONG);
                searchCourse(content);
            }
        }
    }
    public AddCourseFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_course, container, false);
        unbinder = ButterKnife.bind(this, view);
//        fakeData();
        initView();
        return view;
    }

    private void fakeData() {
        String fake_data = "{\n" +
                "      \"course_id\":1241,\n" +
                "      \"course_name\":\"XXX课程\",\n" +
                "      \"teacher\":\"xxx\",\n" +
                "      \"time\":12414134\n" +
                "    }";
        for (int i = 0; i < 10; i++)
            data.add((new Gson().fromJson(fake_data, SearchListBean.class)));
        LogUtil.d("fake data size", String.valueOf(data.size()));
    }

    @SuppressLint("RestrictedApi")
    private void initView() {
        if (SessionKeeper.getUserType(getActivity()).equals("3"))
            fab.setVisibility(View.GONE);
        searchView.onActionViewExpanded();
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        refreshView.setOnRefreshListener(this::refreshData);
        refreshView.setColorSchemeColors(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
        mAdapter = new SearchListAdapter(data, getActivity(), SessionKeeper.getUserType(getActivity()), this);
        recycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycleView.setAdapter(mAdapter);
        recycleView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (!recyclerView.canScrollVertically(1) && !noMoreData && !loadingAppendData)
                    appendData();
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void searchCourse(String s) {
        noMoreData = false;
        page = 1;
        Map<String, String> params = new HashMap<>();
        params.put("token", SessionKeeper.getToken(getActivity()));
        params.put("keys", s);
        params.put("page", String.valueOf(page));
        params.put("page_size", String.valueOf(page_size));
        data.clear();
        sendMsg2Server(params);
    }

    private void sendMsg2Server(Map<String, String> params) {
        HttpUtil.searchCourse(params, new BaseObserver<SearchListBean>() {
            @Override
            protected void onSuccess(SearchListBean searchListBean) {
                if (searchListBean.getResult_code().equals("200")) {
                    data.addAll(searchListBean.getData());
                    mHandler.sendEmptyMessage(WHAT_GET_DATA_SUCCESS);
                } else {
                    mHandler.sendEmptyMessage(WHAT_GET_DATA_FAILED);
                    ToastUtil.showMessage(getActivity(), searchListBean.getResult_desc());
                }
            }

            @Override
            protected void onFailure(Throwable e, boolean isNetWorkError) {
                mHandler.sendEmptyMessage(WHAT_GET_DATA_FAILED);
                if (isNetWorkError)
                    ToastUtil.showMessage(getActivity(), "网络出错，请检查网络", ToastUtil.LENGTH_LONG);
                else
                    ToastUtil.showMessage(getActivity(), e.getMessage(), ToastUtil.LENGTH_LONG);

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void refreshData() {
        Map<String, String> params = new HashMap<>();
        params.put("token", SessionKeeper.getToken(getActivity()));
        params.put("keys", searchString);
        params.put("page", "1");
        params.put("page_size", String.valueOf(page_size * page));
        data.clear();
        sendMsg2Server(params);
    }

    private void appendData() {
        loadingAppendData = true;
        page++;
        Map<String, String> params = new HashMap<>();
        params.put("token", SessionKeeper.getToken(getActivity()));
        params.put("keys", searchString);
        params.put("page", String.valueOf(page));
        params.put("page_size", String.valueOf(page_size));
        HttpUtil.searchCourse(params, new BaseObserver<SearchListBean>() {
            @Override
            protected void onSuccess(SearchListBean searchListBean) {
                loadingAppendData = false;
                if (searchListBean.getResult_code().equals("200")) {
                    if (searchListBean.getData().size() == 0) {
                        noMoreData = true;
                        ToastUtil.showMessage(getActivity(), "没有更多数据了");
                    }
                    data.addAll(searchListBean.getData());
                    mHandler.sendEmptyMessage(WHAT_GET_DATA_SUCCESS);
                } else {
                    mHandler.sendEmptyMessage(WHAT_GET_DATA_FAILED);
                    ToastUtil.showMessage(getActivity(), searchListBean.getResult_desc());
                }
            }

            @Override
            protected void onFailure(Throwable e, boolean isNetWorkError) {
                loadingAppendData = false;
                mHandler.sendEmptyMessage(WHAT_GET_DATA_FAILED);
                if (isNetWorkError)
                    ToastUtil.showMessage(getActivity(), "网络出错，请检查网络", ToastUtil.LENGTH_LONG);
                else
                    ToastUtil.showMessage(getActivity(), e.getMessage(), ToastUtil.LENGTH_LONG);

            }
        });
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_GET_DATA_SUCCESS:
                    if (refreshView.isRefreshing())
                        refreshView.setRefreshing(false);
                    mAdapter.notifyDataSetChanged();
                    break;
                case WHAT_GET_DATA_FAILED:
                    if (refreshView.isRefreshing())
                        refreshView.setRefreshing(false);
                    break;
                case WHAT_ADD_COURSE_SUCCESS:
                    ToastUtil.showMessage(getActivity(), "添加成功");
                    refreshData();
                    break;
                case WHAT_ADD_COURSE_FAILED:

                    break;
            }
        }
    };

    @Override
    public void onButtonClick(View view, int id) {
        Map<String, String> params = new HashMap<>();
        params.put("token", SessionKeeper.getToken(getActivity()));
        params.put("uid", SessionKeeper.getUserId(getActivity()));
        params.put("course_id", String.valueOf(id));
        HttpUtil.addCourse(params, new BaseObserver<DefaultResultBean<Object>>() {
            @Override
            protected void onSuccess(DefaultResultBean<Object> objectDefaultResultBean) {
                if (objectDefaultResultBean.getResult_code().equals("200")) {
                    mHandler.sendEmptyMessage(WHAT_ADD_COURSE_SUCCESS);
                } else {
                    mHandler.sendEmptyMessage(WHAT_ADD_COURSE_FAILED);
                    ToastUtil.showMessage(getActivity(), objectDefaultResultBean.getResult_desc());
                }
            }

            @Override
            protected void onFailure(Throwable e, boolean isNetWorkError) {
                mHandler.sendEmptyMessage(WHAT_ADD_COURSE_FAILED);
                if (isNetWorkError)
                    ToastUtil.showMessage(getActivity(), "网络出错，请检查网络", ToastUtil.LENGTH_LONG);
                else
                    ToastUtil.showMessage(getActivity(), e.getMessage(), ToastUtil.LENGTH_LONG);

            }
        });
    }

    @Override
    public void onItemClick(View view, int id) {
        Intent intent = new Intent(getActivity(), CourseInfoActivity.class);
        intent.putExtra("fromSearch", true);
        intent.putExtra("course_id", String.valueOf(id));
        startActivity(intent);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        searchString = s;
        if (!s.isEmpty())
            searchCourse(s);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        LogUtil.d("onQueryTextChange", s);
        return false;
    }

    @OnClick(R.id.fab)
    public void onViewClicked() {
        startActivity(new Intent(getActivity(), CreateCourseActivity.class));
    }

    @Override
    public void onResume() {
        try {
            View view = Objects.requireNonNull(getActivity()).getCurrentFocus();
            if (view != null) {
                view.clearFocus();
                searchView.clearFocus();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            searchView.clearFocus();
        }
        focus.setFocusable(true);
        focus.setFocusableInTouchMode(true);
        focus.requestFocus();
        super.onResume();
    }
}
