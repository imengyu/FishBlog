package com.dreamfish.fishblog.core.entity;


import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 文章媒体中心条目
 */
@Entity
@Table(name="fish_media_center")
public class PostMedia implements Serializable {

    private static final long serialVersionUID = 2653829053946397782L;

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer postId;
    private String hash;
    private String type;
    private String title;
    private String contentType;
    private String resourceType;
    private String resourcePath;
    private boolean uploadFinish;
    private Integer uploadBlob;
    private Integer uploadCurrent;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String uploadTempPath;
    private Date uploadDate;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String uploadPath;

    public String getUploadPath() {
        return uploadPath;
    }
    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }
    public Date getUploadDate() {
        return uploadDate;
    }
    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }
    public String getUploadTempPath() {
        return uploadTempPath;
    }
    public void setUploadTempPath(String uploadTempPath) {
        this.uploadTempPath = uploadTempPath;
    }
    public Integer getUploadBlob() {
        return uploadBlob;
    }
    public void setUploadBlob(Integer uploadBlob) {
        this.uploadBlob = uploadBlob;
    }
    public Integer getUploadCurrent() {
        return uploadCurrent;
    }
    public void setUploadCurrent(Integer uploadIndex) {
        this.uploadCurrent = uploadIndex;
    }
    public String getResourcePath() {
        return resourcePath;
    }
    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }
    public boolean isUploadFinish() {
        return uploadFinish;
    }
    public void setUploadFinish(boolean uploadFinish) {
        this.uploadFinish = uploadFinish;
    }
    public String getResourceType() {
        return resourceType;
    }
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }
    public String getContentType() {
        return contentType;
    }
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
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
