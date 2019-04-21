package com.dreamfish.fishblog.core.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 文章评论实体
 */
@Entity
@Table(name="fish_comments")
public class PostComment implements Serializable {

    private static final long serialVersionUID = 3045385884753999532L;

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer authorId;
    private String authorName;
    private String authorMail;
    private String authorWebsite;
    @Transient
    private String authorHead;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String authorUa;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String authorIp;

    private Integer postId;
    private Integer parentComment;

    private Date postDate;

    private String commentContent;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorMail() {
        return authorMail;
    }

    public void setAuthorMail(String authorMail) {
        this.authorMail = authorMail;
    }

    public String getAuthorWebsite() {
        return authorWebsite;
    }

    public void setAuthorWebsite(String authorWebsite) {
        this.authorWebsite = authorWebsite;
    }

    public String getAuthorUa() {
        return authorUa;
    }

    public void setAuthorUa(String authorUa) {
        this.authorUa = authorUa;
    }

    public String getAuthorIp() {
        return authorIp;
    }

    public void setAuthorIp(String authorIp) {
        this.authorIp = authorIp;
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public Integer getParentComment() {
        return parentComment;
    }

    public void setParentComment(Integer parentComment) {
        this.parentComment = parentComment;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }
}
