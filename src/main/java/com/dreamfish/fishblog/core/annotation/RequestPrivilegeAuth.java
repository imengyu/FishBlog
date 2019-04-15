package com.dreamfish.fishblog.core.annotation;

import java.lang.annotation.*;

/**
 * 自动权限验证注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestPrivilegeAuth {

    /**
     * 指定需要的用户权限
     */
    int value();

    /**
     * 是否需要
     */
    boolean required() default true;



    /**
     * 认证失败返回值
     */
    String unauthCode() default "403";

    /**
     * 认证失败返回信息
     */
    String unauthMsg() default "无权限";
}
