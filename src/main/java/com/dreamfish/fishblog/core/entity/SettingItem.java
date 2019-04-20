package com.dreamfish.fishblog.core.entity;

import java.io.Serializable;

public class SettingItem implements Serializable {

    private static final long serialVersionUID = 8987994721546093856L;

    private String name;
    private String data;
    private String explain;

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

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }
}
