package com.dreamfish.fishblog.core.utils.request;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Cookie 工具类
 */
public class CookieUtils {

    /**
     * 获取 Cookie
     * @param request
     * @param cookieName Cookie 名字
     * @return
     */
    public static String getCookie(HttpServletRequest request, String cookieName) {

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Cookie cookie = findCookieByName(cookies, cookieName);
            if (cookie != null)
                return cookie.getValue();
        }

        return null;
    }

    /**
     * 在 Cookie[] 数组中查找 Cookie
     *
     * @param cookies Cookie[] 数组
     * @param cookieName 要查找 Cookie 的名字
     * @return 返回查找到的 Cookie ， 如果没有找到，返回 null
     */
    public static Cookie findCookieByName(Cookie[] cookies, String cookieName){
        if(cookies != null){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals(cookieName))
                    return cookie;
            }
        }
        return null;
    }

    /**
     * 设置 Cookie
     * @param response 当前请求
     * @param cookieName Cookie 名字
     * @param value Cookie 值
     */
    public static void setookie(HttpServletResponse response, String cookieName, String value) {
        Cookie cookie = new Cookie(cookieName, value);
        cookie.setPath("/");
        cookie.setMaxAge(3600);
        response.addCookie(cookie);
    }

    /**
     * 设置 Cookie
     * @param response 当前请求
     * @param cookieName Cookie 名字
     * @param value Cookie 值
     * @param maxAge Cookie 过期秒数，0为删除
     */
    public static void setookie(HttpServletResponse response, String cookieName, String value, Integer maxAge) {
        Cookie cookie = new Cookie(cookieName, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }
}
