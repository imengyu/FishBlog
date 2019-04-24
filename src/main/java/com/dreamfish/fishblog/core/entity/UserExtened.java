package com.dreamfish.fishblog.core.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 用户扩展信息实体
 */
@Entity
@Table(name="fish_users")
public class UserExtened implements Serializable {

    private static final long serialVersionUID = 6177322910812990483L;

    private String home;
    private String email;
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String thirdId;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String passwd;
    private Integer level;
    private String friendlyName;
    private String headimg;
    private String userFrom;
    private Integer privilege;
    private Integer oldLevel;
    private String gender;
    private String introduction;
    private String cardBackground;
    private Integer bindUser;
    private Integer messageCount;

    public String getHeadimg() { return headimg; }
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
    public String getEmail() { return email; }
    public String getHome() { return home; }
    public String getUserFrom() {
        return userFrom;
    }
    public Integer getPrivilege() {
        return privilege;
    }
    public Integer getOldLevel() {
        return oldLevel;
    }
    public String getGender() {
        return gender;
    }
    public String getIntroduction() {
        return introduction;
    }
    public String getCardBackground() {
        return cardBackground;
    }
    public String getThirdId() {
        return thirdId;
    }
    public Integer getBindUser() {
        return bindUser;
    }
    public Integer getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(Integer messageCount) {
        this.messageCount = messageCount;
    }
    public void setBindUser(Integer bindUser) {
        this.bindUser = bindUser;
    }
    public void setThirdId(String thirdId) {
        this.thirdId = thirdId;
    }
    public void setCardBackground(String cardBackground) {
        this.cardBackground = cardBackground;
    }
    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public void setPrivilege(Integer privilege) {
        this.privilege = privilege;
    }
    public void setUserFrom(String user_from) {
        this.userFrom = user_from;
    }
    public void setOldLevel(Integer oldLevel) {
        this.oldLevel = oldLevel;
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
    public void setHeadimg(String headimg) { this.headimg = headimg; }
    public void setEmail(String email) { this.email = email; }
    public void setHome(String home) { this.home = home; }

    public static UserExtened DefaultValue = new UserExtened();
}
