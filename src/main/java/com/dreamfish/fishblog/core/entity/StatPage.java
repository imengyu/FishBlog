package com.dreamfish.fishblog.core.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "fish_stat_pages")
public class StatPage implements Serializable {
    private static final long serialVersionUID = -4318773964306798788L;

    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String url;
    private String date;
    private Integer count;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
