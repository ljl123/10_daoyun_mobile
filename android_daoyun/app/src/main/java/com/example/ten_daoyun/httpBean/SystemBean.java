package com.example.ten_daoyun.httpBean;


import java.util.List;

/**
 * lijieling 系统参数
 */
public class SystemBean extends DefaultResultBean<List<SystemBean>>{
    private String id;
    private String uid;
    private String experience;
    private String distance;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
