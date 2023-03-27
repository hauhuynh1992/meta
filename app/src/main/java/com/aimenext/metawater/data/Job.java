package com.aimenext.metawater.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Job {
    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("image_uri")
    @Expose
    private String imageUri;
    @SerializedName("can_code")
    @Expose
    private String canCode;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("unique")
    @Expose
    private String unique;
    @SerializedName("date")
    @Expose
    private Long date;

    public Job(Long id, String imageUri, String canCode, String type, String unique, Long date) {
        this.id = id;
        this.imageUri = imageUri;
        this.canCode = canCode;
        this.type = type;
        this.unique = unique;
        this.date = date;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getCanCode() {
        return canCode;
    }

    public void setCanCode(String canCode) {
        this.canCode = canCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUnique() {
        return unique;
    }

    public void setUnique(String unique) {
        this.unique = unique;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}