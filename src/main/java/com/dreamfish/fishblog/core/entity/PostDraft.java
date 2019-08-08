package com.dreamfish.fishblog.core.entity;

import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 文章实体类
 */
@Entity
@DynamicInsert(true)
@Table(name="fish_post_draft")
public class PostDraft implements Serializable {

    private static final long serialVersionUID = -9182510244279825054L;

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer belongPost;
    private String objectJson;
    private Date updateTime;

    public Date getUpdateTime() {
        return updateTime;
    }
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getBelongPost() {
        return belongPost;
    }
    public void setBelongPost(Integer belongPost) {
        this.belongPost = belongPost;
    }
    public String getObjectJson() {
        return objectJson;
    }
    public void setObjectJson(String objectJson) {
        this.objectJson = objectJson;
    }
}
