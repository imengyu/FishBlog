package com.dreamfish.fishblog.core.enums;

/**
 * 用戶附加权限
 */
public class UserPrivileges {

    /**
     * 许可用户管理所有文章
     */
    public static final int PRIVILEGE_MANAGE_ALL_ARCHIVES = 0x00000001;
    /**
     * 许可用户管理文章分类、标签
     */
    public static final int PRIVILEGE_MANAGE_ALL_CLASSANDTAGS = 0x00000002;
    /**
     * 许可用户管理媒体中心
     */
    public static final int PRIVILEGE_MANAGE_MEDIA_CENTER = 0x00000004;
    /**
     * 许可用户管理用户
     */
    public static final int PRIVILEGE_MANAGE_USERS = 0x00000008;
    /**
     * 许可用户赋予用户权限
     */
    public static final int PRIVILEGE_GAINT_PRIVILEGE = 0x00000010;
    /**
     * 许可用户设置全局设置
     */
    public static final int PRIVILEGE_GLOBAL_SETTINGS = 0x00000020;

}
