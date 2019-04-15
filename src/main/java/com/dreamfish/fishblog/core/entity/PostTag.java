package com.dreamfish.fishblog.core.entity;


import java.io.Serializable;

/**
 * 文章分类标签
 */
public class PostTag implements Serializable {

    private static final long serialVersionUID = 1929315475947612600L;

    private Integer id;
    private String name;
    private String color;

    public Integer getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getColor() {
        return color;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setColor(String color) {
        this.color = color;
    }
}
