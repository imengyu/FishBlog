package com.dreamfish.fishblog.core.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 文章简要信息实体类
 */
@Entity
@Table(name="fish_posts")
public class PostSimple implements Serializable {

    private static final long serialVersionUID = -4267297373765083850L;

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String urlName;
    private String postDate;
    private String lastmodifyDate;
    private String tags;
    private String postClass;
    private String title;
    private String previewText;
    private String previewImage;
    private String keywords;
    private Integer status;
    private Integer postPrefix;
    private Integer viewCount;
    private Integer commentCount;
    private String author;
    private Integer authorId;
    private Boolean showInList;
    private Boolean topMost;
    private Integer likeCount;
    @Transient
    private String link;

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Boolean getTopMost() {
        return topMost;
    }

    public void setTopMost(Boolean topMost) {
        this.topMost = topMost;
    }

    public Boolean getShowInList() {
        return showInList;
    }

    public void setShowInList(Boolean showInList) {
        this.showInList = showInList;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    public Integer getPostPrefix() {
        return postPrefix;
    }

    public void setPostPrefix(Integer postPrefix) {
        this.postPrefix = postPrefix;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrlName() {
        return urlName;
    }

    public void setUrlName(String urlName) {
        this.urlName = urlName;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getLastmodifyDate() {
        return lastmodifyDate;
    }

    public void setLastmodifyDate(String lastmodifyDate) {
        this.lastmodifyDate = lastmodifyDate;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getPostClass() {
        return postClass;
    }

    public void setPostClass(String postClass) {
        this.postClass = postClass;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPreviewText() {
        return previewText;
    }

    public void setPreviewText(String previewText) {
        this.previewText = previewText;
    }

    public String getPreviewImage() {
        return previewImage;
    }

    public void setPreviewImage(String previewImage) {
        this.previewImage = previewImage;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
