package com.example.daoyun.http;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.example.daoyun.HttpBean.CheckListBean;
import com.example.daoyun.HttpBean.CourseInfoBean;
import com.example.daoyun.HttpBean.CoursesListBean;
import com.example.daoyun.HttpBean.DefaultResultBean;
import com.example.daoyun.HttpBean.DictInfoListBean;
import com.example.daoyun.HttpBean.LoginBean;
import com.example.daoyun.HttpBean.RegisterBean;
import com.example.daoyun.HttpBean.SearchListBean;
import com.example.daoyun.HttpBean.StudentsListBean;
import com.example.daoyun.HttpBean.UploadAvatarBean;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class HttpUtil extends HttpBase {
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    private static Retrofit mRetrofit;

    /**
     * 初始化Retrofit
     *
     * @return
     */
    @NonNull
    private static Retrofit init() {
        if (mRetrofit != null) return mRetrofit;
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20L, TimeUnit.SECONDS)
                .readTimeout(15L, TimeUnit.SECONDS)
                .writeTimeout(15L, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(HTTP_URL)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return mRetrofit;
    }

    /**
     * 用户模块 <==================================================================================>
     */

    public static void registerUser(Map<String, String> params, BaseObserver<RegisterBean> callback) {
        Retrofit retrofit = init();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);
        service.httpRegisterInterface(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }

    public static void login(Map<String, String> params, BaseObserver<LoginBean> callback) {
        Retrofit retrofit = init();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);
        service.httpLoginInterface(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }

    public static void forgotPwd(Map<String, String> params, BaseObserver<DefaultResultBean<Object>> callback) {
        Retrofit retrofit = init();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);
        service.httpForgotPwdInterface(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }

    public static void modifyUserInfo(Map<String, String> params, BaseObserver<DefaultResultBean<Object>> callback) {
        Retrofit retrofit = init();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);
        service.httpModifyUserInfoInterface(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }

    /**
     * 上传人脸信息
     *
     * @param params
     * @param fileUrl  图片url
     * @param callback
     */
    public static void uploadFaceInfo(Map<String, String> params, String fileUrl, BaseObserver<DefaultResultBean<Object>> callback) {
        Retrofit retrofit = init();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);

        File file = new File(fileUrl);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("face", file.getName(), requestFile);

        service.httpUploadFaceInterface(params, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }

    public static void uploadFaceInfo(Map<String, String> params, File faceFile, BaseObserver<DefaultResultBean<Object>> callback) {
        Retrofit retrofit = init();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);
        File file = new File(Environment.getExternalStorageDirectory() + "/upload_face_info.jpg");
        BufferedOutputStream baos = null;
        try {
            baos = new BufferedOutputStream(new FileOutputStream(file));
            Bitmap bit = BitmapFactory.decodeFile(faceFile.getPath());
            bit.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            baos.flush();
            baos.close();

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("face", file.getName(), requestFile);

            service.httpUploadFaceInterface(params, body)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(callback);
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(e, false);
        }
    }

    public static void getFaceExist(String token, BaseObserver<DefaultResultBean<Object>> callback) {
        Retrofit retrofit = init();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);
        service.httpGetFaceExistInterface(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }

    public static void sendEmail(String email, BaseObserver<DefaultResultBean<Object>> callback) {
        Retrofit retrofit = init();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);
        service.httpSendEmailInterface(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }

    /**
     * 上传头像
     *
     * @param params
     * @param fileUrl  图片url
     * @param callback
     */
    public static void uploadAvatarInfo(Map<String, String> params, String fileUrl, BaseObserver<UploadAvatarBean> callback) {
        Retrofit retrofit = init();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);

        File file = new File(fileUrl);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("avatar", file.getName(), requestFile);

        service.httpUploadAvatarInterface(params, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }

    /**
     * 课程模块  <=============================================================================>
     */

    public static void addCourse(Map<String, String> params, BaseObserver<DefaultResultBean<Object>> callback) {
        Retrofit retrofit = init();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);
        service.httpAddCourseInterface(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }

    public static void getStudentsList(String token, String course_id, BaseObserver<StudentsListBean> callback) {
        Retrofit retrofit = init();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);
        service.httpGetStudentsListInterface(token, course_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }

    public static void getCheckList(String token, String course_id, BaseObserver<CheckListBean> callback) {
        Retrofit retrofit = init();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);
        service.httpGetCheckListInterface(token, course_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }

    public static void searchCourse(Map<String, String> params, BaseObserver<SearchListBean> callback) {
        Retrofit retrofit = init();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);
        service.httpSearchCourseInterface(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }

    public static void getCourseInfo(String token, String course_id, BaseObserver<CourseInfoBean> callback) {
        Retrofit retrofit = init();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);
        service.httpGetCourseInfoInterface(token, course_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }

    public static void modifyCourseInfo(Map<String, String> params, BaseObserver<DefaultResultBean<Object>> callback) {
        Retrofit retrofit = init();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);
        service.httpModifyCourseInfoInterface(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }

    public static void deleteStudent(Map<String, String> params, BaseObserver<DefaultResultBean<Object>> callback) {
        Retrofit retrofit = init();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);
        service.httpDeleteStudentInterface(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }

    public static void deleteCheck(Map<String, String> params, BaseObserver<DefaultResultBean<Object>> callback) {
        Retrofit retrofit = init();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);
        service.httpDeleteCheckInterface(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }

    public static void modifyStudent(Map<String, String> params, BaseObserver<DefaultResultBean<Object>> callback) {
        Retrofit retrofit = init();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);
        service.httpModifyStudentInterface(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }

    public static void modifyCheck(Map<String, String> params, BaseObserver<DefaultResultBean<Object>> callback) {
        Retrofit retrofit = init();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);
        service.httpModifyCheckInterface(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }

    public static void addStu2Course(Map<String, String> params, BaseObserver<DefaultResultBean<Object>> callback) {
        Retrofit retrofit = init();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);
        service.httpAddStu2CourseInterface(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }

    public static void getCoursesList(Map<String, String> params, BaseObserver<CoursesListBean> callback) {
        Retrofit retrofit = init();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);
        service.httpGetCoursesListInterface(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }

    public static void createCourse(Map<String, String> params, BaseObserver<DefaultResultBean<Object>> callback) {
        Retrofit retrofit = init();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);
        service.httpCreateCourseInterface(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }

    /**
     * 签到模块  <=============================================================================>
     */

    public static void check(Map<String, String> params, String fileUrl, BaseObserver<DefaultResultBean<Boolean>> callback) {
        Retrofit retrofit = init();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);
//
//        File file = new File(Environment.getExternalStorageDirectory() + "/upload_face_info.jpg");
//        String origin_length = String.valueOf(file.length());
//        BufferedOutputStream baos = null;
//        try {
//            if(HttpBase.USE_FACE_INFO) {
//                baos = new BufferedOutputStream(new FileOutputStream(file));
//                Bitmap bit = BitmapFactory.decodeFile(fileUrl);
//                bit.compress(Bitmap.CompressFormat.JPEG, 50, baos);
//                baos.flush();
//                baos.close();
//                Log.i("wechat", "压缩前图片的大小:" + origin_length + "byte \n" + "压缩后图片的大小:" + String.valueOf(file.length()) + "byte");
//            }else{
//                file = new File(fileUrl);
//            }
//            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), file);
//            MultipartBody.Part body = MultipartBody.Part.createFormData("face", file.getName(), requestFile);

//            service.httpCheckInterface(params, body)
            service.httpCheckInterface(params)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(callback);
//        } catch (Exception e) {
//            e.printStackTrace();
//            callback.onFailure(e, false);
//        }
    }

    public static void startCheck(Map<String, String> params, BaseObserver<DefaultResultBean<Object>> callback) {
        Retrofit retrofit = init();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);
        service.httpStartCheckInterface(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }

    public static void stopCheck(Map<String, String> params, BaseObserver<DefaultResultBean<Object>> callback) {
        Retrofit retrofit = init();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);
        service.httpStopCheckInterface(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }

    public static void canCheck(Map<String, String> params, BaseObserver<DefaultResultBean<Boolean>> callback) {
        Retrofit retrofit = init();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);
        service.httpCanCheckInterface(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }

    /**
     * 数据字典模块  <=============================================================================>
     */
    public static void getDictInfo(String token, String typename, BaseObserver<DictInfoListBean> callback) {
        Retrofit retrofit = init();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);
        service.httpGetDictInfoInterface(token, typename)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }
}
