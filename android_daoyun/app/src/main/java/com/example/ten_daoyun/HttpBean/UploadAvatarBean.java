package com.example.ten_daoyun.HttpBean;

public class UploadAvatarBean extends DefaultResultBean<UploadAvatarBean> {

    /**
     * avatar : http://124.141.432.41:8080/static/xxx.jpg
     */

    private String avatar;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
