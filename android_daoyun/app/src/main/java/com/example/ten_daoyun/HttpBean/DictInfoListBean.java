package com.example.ten_daoyun.HttpBean;

import java.util.List;

public class DictInfoListBean extends DefaultResultBean<List<DictInfoListBean>> {

    /**
     * id : 1
     * typeid : 1
     * type_level : 1
     * type_belong : 1
     * info :
     */

    private int id;
    private int typeid;
    private int type_level;
    private int type_belong;
    private String info;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTypeid() {
        return typeid;
    }

    public void setTypeid(int typeid) {
        this.typeid = typeid;
    }

    public int getType_level() {
        return type_level;
    }

    public void setType_level(int type_level) {
        this.type_level = type_level;
    }

    public int getType_belong() {
        return type_belong;
    }

    public void setType_belong(int type_belong) {
        this.type_belong = type_belong;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
