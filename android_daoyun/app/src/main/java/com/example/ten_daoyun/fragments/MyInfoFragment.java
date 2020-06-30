package com.example.ten_daoyun.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ten_daoyun.httpBean.DefaultResultBean;
import com.example.ten_daoyun.httpBean.UploadAvatarBean;
import com.example.ten_daoyun.R;
import com.example.ten_daoyun.activities.LoginActivity;
import com.example.ten_daoyun.activities.UserInfoActivity;
import com.example.ten_daoyun.adapters.ChoosePhotoAdapter;
import com.example.ten_daoyun.http.BaseObserver;
import com.example.ten_daoyun.http.HttpUtil;
import com.example.ten_daoyun.session.SessionKeeper;
import com.example.ten_daoyun.utils.LogUtil;
import com.example.ten_daoyun.utils.ToastUtil;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.GridHolder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyInfoFragment extends Fragment {
    //选择头像 标记
    private static final int REQUEST_PICK_MEDIA = 101;
    private static final int REQUEST_TAKE_MEDIA = 102;
    private static final int REQUEST_CROP_MEDIA = 103;
    private static final int WHAT_CHANGE_AVATAR = 104;
    Uri imageUri = Uri.parse("file://" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/avatar.jpg");
    File take_photo_file = new File(Environment.getExternalStorageDirectory() + File.separator + "Pictures", "avatar.jpg");
    File take_face_file = new File(Environment.getExternalStorageDirectory() + File.separator + "Pictures", "face.jpg");
    Uri take_photo_file_Uri;
    @BindView(R.id.avatar)
    ImageView avatar;
    @BindView(R.id.nick_name)
    TextView nickName;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.edit_user_info)
    LinearLayout editUserInfo;
    @BindView(R.id.quit)
    LinearLayout quit;
    Unbinder unbinder;

    DialogPlus chooseDialog;

    public MyInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_info, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        initDialog();
        take_photo_file_Uri = FileProvider.getUriForFile(
                getActivity(),
                "com.example.ten_daoyun.fileprovider",
                take_photo_file);
        return view;
    }

    private void initView() {
        nickName.setText("昵称:" + SessionKeeper.getUserNickName(getActivity()));
        name.setText("姓名:" + SessionKeeper.getUserInfo(getActivity()).getName());
        Glide.with(this)
                .asBitmap()
                .load(SessionKeeper.getUserAvatar(getActivity()))
                .placeholder(R.drawable.ic_my_black_24dp)
                .error(R.drawable.ic_my_black_24dp)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(avatar);
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


    @OnClick(R.id.avatar)
    public void onAvatarClicked() {
        ToastUtil.showMessage(getActivity(), "暂不支持修改头像");
        //chooseDialog.show();
    }

    @OnClick(R.id.edit_user_info)
    public void onEditUserInfoClicked() {
        Intent userIntent = new Intent(getActivity(), UserInfoActivity.class);
        userIntent.putExtra("uid", SessionKeeper.getUserId(Objects.requireNonNull(getActivity())));
        startActivity(userIntent);
    }

    @OnClick(R.id.quit)
    public void onQuitClicked() {
        SessionKeeper.keepAutoLogin(getContext(), false);
        startActivity(new Intent(getActivity(), LoginActivity.class));
        Objects.requireNonNull(getActivity()).finish();
    }




    public Context getContext() {
        return getActivity();
    }

    private void refreshImageUri(Uri uri) {
        File file = new File(uri.getPath());
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initDialog() {
        chooseDialog = DialogPlus.newDialog(getActivity())
                .setOnItemClickListener((dialog, item, view, position) -> {
                    Intent intent;
                    switch (position) {
                        case 0:
                            //启动相册
                            refreshImageUri(imageUri);
                            intent = new Intent(Intent.ACTION_PICK, null);
                            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                            intent.putExtra("return-data", false);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                            startActivityForResult(intent, REQUEST_PICK_MEDIA);
                            break;
                        default:
                            Log.d("SettingFragment", "item不为0和1，出错");
                    }
                })
                .setOnBackPressListener(dialogPlus -> {
                })
                .setAdapter(new ChoosePhotoAdapter(getActivity()))
                .setCancelable(true)
                .setContentHolder(new GridHolder(1))
                .setGravity(Gravity.CENTER)
                .setPadding(0, 50, 0, 50)
                .create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_PICK_MEDIA:
                if (resultCode == getActivity().RESULT_OK) {
                    //uploadAvatar();
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(data.getData(), "image/*");
                    intent.putExtra("crop", "true");
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("outputX", 400);
                    intent.putExtra("outputY", 400);
                    intent.putExtra("scale", true);
                    intent.putExtra("return-data", false);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                    startActivityForResult(intent, REQUEST_CROP_MEDIA);
                }
                break;
            case REQUEST_TAKE_MEDIA:
                if (resultCode == getActivity().RESULT_OK) {
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(take_photo_file_Uri, "image/*");
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.putExtra("crop", "true");
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("outputX", 400);
                    intent.putExtra("outputY", 400);
                    intent.putExtra("scale", true);
                    intent.putExtra("return-data", false);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(take_photo_file));
                    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                    startActivityForResult(intent, REQUEST_CROP_MEDIA);
                }
                break;
            case REQUEST_CROP_MEDIA:
                LogUtil.d("result", String.valueOf(REQUEST_CROP_MEDIA));
                if (resultCode == getActivity().RESULT_OK) {
                    uploadAvatar();
                } else {
                    Log.d("DATA URI", "FAIL");
                }
                chooseDialog.dismiss();
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadAvatar() {
        Map<String, String> params = new HashMap<>();
        params.put("token", SessionKeeper.getToken(getActivity()));
        params.put("uid", SessionKeeper.getUserId(getActivity()));
        HttpUtil.uploadAvatarInfo(params, imageUri.getPath(), new BaseObserver<UploadAvatarBean>() {
            @Override
            protected void onSuccess(UploadAvatarBean uploadAvatarBean) {
                if (uploadAvatarBean.getResult_code().equals("200")) {
                    SessionKeeper.keepUserAvatar(getActivity(), uploadAvatarBean.getData().getAvatar());
                    mHandler.sendEmptyMessage(WHAT_CHANGE_AVATAR);
                } else {
                    ToastUtil.showMessage(getActivity(), "修改头像失败");
                }
            }

            @Override
            protected void onFailure(Throwable e, boolean isNetWorkError) {
                if (isNetWorkError)
                    ToastUtil.showMessage(getActivity(), "网络错误", ToastUtil.LENGTH_LONG);
                else
                    ToastUtil.showMessage(getActivity(), e.getMessage(), ToastUtil.LENGTH_SHORT);
            }
        });
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_CHANGE_AVATAR:
                    initView();
                    if (chooseDialog.isShowing())
                        chooseDialog.dismiss();
                    break;
            }
        }
    };

    @Override
    public void onResume() {
        initView();
        super.onResume();
    }
}
