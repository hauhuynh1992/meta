package com.aimenext.metawater.activity.unsend.adapter;

public class UnSendItem {
    private Long id;
    private int numImages;
    private String canCode;
    private String type;
    private String unique;
    private Long date;

    public UnSendItem(Long id, int numImages, String canCode, String type, String unique, Long date) {
        this.id = id;
        this.numImages = numImages;
        this.canCode = canCode;
        this.type = type;
        this.unique = unique;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getImageNum() {
        return numImages;
    }

    public void setImageUri(int numImages) {
        this.numImages = numImages;
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
}
