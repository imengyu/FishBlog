package com.dreamfish.fishblog.core.entity;

import java.io.Serializable;

public class StatTopPost implements Serializable {

    private static final long serialVersionUID = 2058594760883468322L;

    private Integer id;
    private String title;
    private Integer viewCount;
    private String urlName;

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public String getUrlName() {
        return urlName;
    }
}
