package com.dreamfish.fishblog.core.entity;


import javax.persistence.*;
import java.io.Serializable;

/**
 * 文章媒体中心条目
 */
@Entity
@Table(name="fish_media_center")
public class PostMedia implements Serializable {

    private static final long serialVersionUID = 2653829053946397782L;

    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Integer postId;
    private String hash;
    private String type;
    private String title;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
