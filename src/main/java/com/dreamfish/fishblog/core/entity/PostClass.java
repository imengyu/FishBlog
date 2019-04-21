package com.dreamfish.fishblog.core.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 文章分类
 */
@Entity
@Table(name="fish_post_classes")
public class PostClass implements Serializable {

    private static final long serialVersionUID = -3037304079571291010L;

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String url_name;
    private String preview_text;
    private String preview_image;
    private String title;
    private Integer status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getUrlName() {
        return url_name;
    }

    public void setUrlName(String urlName) {
        this.url_name = urlName;
    }

    public String getPreviewText() {
        return preview_text;
    }

    public void setPreviewText(String previewText) {
        this.preview_text = previewText;
    }

    public String getPreviewImage() {
        return preview_image;
    }

    public void setPreviewImage(String previewImage) {
        this.preview_image = previewImage;
    }
}
