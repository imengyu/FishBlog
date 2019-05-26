package com.dreamfish.fishblog.core.entity;

import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity
@DynamicInsert(true)
@Table(name="fish_messages")
public class MessageItem implements Serializable {
    private static final long serialVersionUID = 4608237524892002759L;

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer userId;
    private Integer fromUserId;
    private Date date;
    private String title;
    private String content;
    private Boolean haveRead;

    public Boolean getHaveRead() {
        return haveRead;
    }

    public void setHaveRead(Boolean haveRead) {
        this.haveRead = haveRead;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Integer fromUserId) {
        this.fromUserId = fromUserId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

