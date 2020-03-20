package com.example.daoyun.HttpBean;

import java.util.List;

public class SearchListBean extends DefaultResultBean<List<SearchListBean>> {


    /**
     * course_id : 1241
     * course_name : XXX课程
     * teacher : xxx
     * time : 12414134
     * added : 1
     */

    private int course_id;
    private String course_name;
    private String teacher;
    private String time;
    private int added;

    public int getCourse_id() {
        return course_id;
    }

    public void setCourse_id(int course_id) {
        this.course_id = course_id;
    }

    public String getCourse_name() {
        return course_name;
    }

    public void setCourse_name(String course_name) {
        this.course_name = course_name;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getAdded() {
        return added;
    }

    public void setAdded(int added) {
        this.added = added;
    }
}
