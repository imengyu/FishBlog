package com.dreamfish.fishblog.core.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 文章实体类
 */
@Entity
@Table(name="fish_posts")
public class Post implements Serializable {

    public static final Integer POST_STATUS_PUBLISH = 1;
    public static final Integer POST_STATUS_PRIVATE = 0;
    public static final Integer POST_STATUS_AUTOSAVE = 2;

    public static final Integer POST_PREFIX_ORIGINAL = 1;//原创
    public static final Integer POST_PREFIX_DEFAULT = 0;//默认无
    public static final Integer POST_PREFIX_REPRINT = 2;//转载
    public static final Integer POST_PREFIX_VIDEO = 3;//视频
    public static final Integer POST_PREFIX_ALBUM = 4;//相册

    private static final long serialVersionUID = -3035781360400015965L;

    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String urlName;

    private Date postDate;
    private Date lastmodifyDate;
    private String tags;
    private String postClass;
    private String title;
    private String previewText;
    private String previewImage;
    private String type;
    private String author;
    private Integer authorId;
    private String keywords;
    private Integer status;
    private String content;
    private Boolean headimgMask;
    private String headimg;
    private Integer viewCount;
    private Integer commentCount;
    private Integer postNextId;
    private Integer postPrvId;
    private Boolean enableComment;
    private Integer postPrefix;

    @Transient
    private String postPrvTitle;
    @Transient
    private String postNextTitle;
    @Transient
    private List<PostTag> postTagNames;

    public List<PostTag> getPostTagNames() {
        return postTagNames;
    }

    public void setPostTagNames(List<PostTag> postTagNames) {
        this.postTagNames = postTagNames;
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

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public Date getLastmodifyDate() {
        return lastmodifyDate;
    }

    public void setLastmodifyDate(Date lastmodifyDate) {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getHeadimgMask() {
        return headimgMask;
    }

    public void setHeadimgMask(Boolean headimgMask) {
        this.headimgMask = headimgMask;
    }

    public String getHeadimg() {
        return headimg;
    }

    public void setHeadimg(String headimg) {
        this.headimg = headimg;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer view_count) {
        this.viewCount = view_count;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Integer getPostNextId() {
        return postNextId;
    }

    public void setPostNextId(Integer postNextId) {
        this.postNextId = postNextId;
    }

    public Integer getPostPrvId() {
        return postPrvId;
    }

    public void setPostPrvId(Integer postPrvId) {
        this.postPrvId = postPrvId;
    }

    public Boolean getEnableComment() {
        return enableComment;
    }

    public void setEnableComment(Boolean enableComment) {
        this.enableComment = enableComment;
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

    public String getPostPrvTitle() {
        return postPrvTitle;
    }

    public void setPostPrvTitle(String postPrvTitle) {
        this.postPrvTitle = postPrvTitle;
    }

    public String getPostNextTitle() {
        return postNextTitle;
    }

    public void setPostNextTitle(String postNextTitle) {
        this.postNextTitle = postNextTitle;
    }
}
