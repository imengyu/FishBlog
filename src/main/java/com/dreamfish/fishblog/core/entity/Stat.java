package com.dreamfish.fishblog.core.entity;

import java.io.Serializable;

public class Stat implements Serializable {
    private static final long serialVersionUID = 1337581141872784424L;

    private Integer id;
    private String name;
    private String data;
    private Integer intData;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Integer getIntData() {
        return intData;
    }

    public void setIntData(Integer intData) {
        this.intData = intData;
    }
}
