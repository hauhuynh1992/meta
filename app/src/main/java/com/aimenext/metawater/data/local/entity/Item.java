package com.aimenext.metawater.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "items")
public class Item {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    private String type;
    private String code;
    private String image;
    private String unique;
    private Long date;

    public Item(String type, String code, String image, String unique, Long date) {
        this.type = type;
        this.code = code;
        this.image = image;
        this.unique = unique;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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