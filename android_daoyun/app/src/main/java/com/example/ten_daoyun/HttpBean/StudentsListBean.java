package com.example.ten_daoyun.httpBean;

import java.util.List;

public class StudentsListBean extends DefaultResultBean<List<StudentsListBean>>{

    /**
     * id : 1
     * uid : 1
     * stu_code : 190327046
     * name : 李杰铃
     * gender : 男
     * school : 福州大学
     * department : 数计学院
     * profession : 计算机技术
     * phone :1875XX
     * lack_count :
     * check_count：
     */

    private String id;
    private String uid;
    private String stu_code;
    private String name;
    private String gender;
    private String school;
    private String department;
    private String profession;
    private String phone;
    private String lack_count;
    private String check_count;

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

    public String getStu_code() {
        return stu_code;
    }

    public void setStu_code(String stu_code) {
        this.stu_code = stu_code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLack_count() {
        return lack_count;
    }

    public String getCheck_count() {
        return check_count;
    }

    public void setCheck_count(String check_count) {
        this.check_count = check_count;
    }

    public void setLack_count(String lack_count) {
        this.lack_count = lack_count;
    }
}
