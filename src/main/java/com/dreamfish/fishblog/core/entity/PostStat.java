package com.dreamfish.fishblog.core.entity;

import javax.persistence.Transient;
import java.io.Serializable;

public class PostStat implements Serializable {

    private static final long serialVersionUID = 1175987822118795504L;

    private Integer viewCount;
    private Integer commentCount;
    private Integer likeCount;

    @Transient
    private Boolean currentUserLiked;


    public Boolean getCurrentUserLiked() {
        return currentUserLiked;
    }

    public void setCurrentUserLiked(Boolean currentUserLiked) {
        this.currentUserLiked = currentUserLiked;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
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
}
