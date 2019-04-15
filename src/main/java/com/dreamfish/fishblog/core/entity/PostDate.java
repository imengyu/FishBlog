package com.dreamfish.fishblog.core.entity;

import java.io.Serializable;

/**
 * 文章归档时间实体
 */
public class PostDate implements Serializable {

    private static final long serialVersionUID = 2253982095479944143L;

    private Integer id;
    private String date;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer count;


}
