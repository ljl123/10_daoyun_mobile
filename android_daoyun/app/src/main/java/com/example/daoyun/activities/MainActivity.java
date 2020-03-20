package com.example.daoyun.activities;

import android.Manifest;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.daoyun.R;
import com.example.daoyun.fragments.AddCourseFragment;
import com.example.daoyun.fragments.MainFragment;
import com.example.daoyun.fragments.MyInfoFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {
    static final String[] LOCATIONGPS = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE};
    private static final int BAIDU_READ_PHONE_STATE = 100;//定位权限请求
    private static final int PRIVATE_CODE = 1315;//开启GPS权限
    @BindView(R.id.frame_layout_main)
    FrameLayout frameLayout;
    @BindView(R.id.navigation_main)
    BottomNavigationView navigation;

    FragmentManager mFragmentManager;
    Fragment mMainFragment = new MainFragment();
    Fragment mAddCourseFragment = new AddCourseFragment();
    Fragment mMyInfoFragment = new MyInfoFragment();

    int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        showGPSContacts();
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mFragmentManager = getSupportFragmentManager();
        startFragment();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_my_course:
                    if(position==0)return false;
                    position = 0;
                    mFragmentManager.beginTransaction().replace(frameLayout.getId(),mMainFragment).commit();
                    return true;
                case R.id.navigation_add_course:
                    if(position==1)return false;
                    position = 1;
                    mFragmentManager.beginTransaction().replace(frameLayout.getId(),mAddCourseFragment).commit();
                    return true;
                case R.id.navigation_my_info:
                    if(position==2)return false;
                    position = 2;
                    mFragmentManager.beginTransaction().replace(frameLayout.getId(),mMyInfoFragment).commit();
                    return true;
            }
            return false;
        }
    };

    public void startFragment(){
        mFragmentManager.beginTransaction().replace(frameLayout.getId(),mMainFragment).commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void showGPSContacts() {
        LocationManager lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        boolean ok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (ok) {//开了定位服务
            if (Build.VERSION.SDK_INT >= 23) { //判断是否为android6.0系统版本，如果是，需要动态添加权限
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PERMISSION_GRANTED) {// 没有权限，申请权限。
                    ActivityCompat.requestPermissions(this, LOCATIONGPS,
                            BAIDU_READ_PHONE_STATE);
                }
            }
        } else {
            Toast.makeText(this, "系统检测到未开启GPS定位服务,请开启", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, PRIVATE_CODE);
        }
    }
}
