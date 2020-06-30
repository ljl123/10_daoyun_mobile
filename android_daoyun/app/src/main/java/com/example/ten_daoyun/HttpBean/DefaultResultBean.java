package com.example.ten_daoyun.httpBean;

public class DefaultResultBean<T> {
    /**
     * data : null
     * request_id : 1481729816
     * result_code : 200
     * result_desc : request successful
     * timestamp : 2016-12-14 23:36:56
     */

    private T data;
    private long request_id;
    private String result_code;
    private String result_desc;
    private String timestamp;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getRequest_id() {
        return request_id;
    }

    public void setRequest_id(long request_id) {
        this.request_id = request_id;
    }

    public String getResult_code() {
        return result_code;
    }

    public void setResult_code(String result_code) {
        this.result_code = result_code;
    }

    public String getResult_desc() {
        return result_desc;
    }

    public void setResult_desc(String result_desc) {
        this.result_desc = result_desc;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
