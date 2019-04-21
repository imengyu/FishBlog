package com.dreamfish.fishblog.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 用户实体
 */
@Entity
@Table(name = "fish_users")
public class User implements Serializable {

    /**
     * 不需要许可权限
     */
    public static final int LEVEL_NOT_REQUIRED = -1;
    /**
     * 用户被封禁，无任何权限，无法登录
     */
    public static final int LEVEL_LOCKED = 0;
    /**
     * 网站管理员组，全部权限
     */
    public static final int LEVEL_ADMIN = 1;
    /**
     * 作者组，部分权限，可附加其他权限
     */
    public static final int LEVEL_WRITER = 2;
    /**
     * 访客组，只能评论，无法登录
     */
    public static final int LEVEL_GUEST = 3;


    private static final long serialVersionUID = -3711977388670023741L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String passwd;
    private Integer level;
    private Integer privilege;
    private String friendlyName;
    private String headimg;

    public Integer getPrivilege() {
        return privilege;
    }

    public void setPrivilege(Integer privilege) {
        this.privilege = privilege;
    }

    public String getHeadimg() {
        return headimg;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPasswd() {
        return passwd;
    }

    public Integer getLevel() {
        return level;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public void setHeadimg(String headimg) {
        this.headimg = headimg;
    }

    public User() {
    }
}
