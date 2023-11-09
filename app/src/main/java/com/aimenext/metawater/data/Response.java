package com.aimenext.metawater.data;

import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Expose;

public class Response {
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMess() {
        return mess;
    }

    public void setMess(String mess) {
        this.mess = mess;
    }

    @SerializedName("mess")
    @Expose
    String mess;
    @SerializedName("token")
    @Expose
    String token;
    Long jobId;

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }
}
