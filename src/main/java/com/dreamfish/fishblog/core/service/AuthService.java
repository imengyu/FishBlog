package com.dreamfish.fishblog.core.service;

import com.dreamfish.fishblog.core.entity.User;
import com.dreamfish.fishblog.core.entity.UserExtened;
import com.dreamfish.fishblog.core.utils.Result;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface AuthService {

    int AUTH_TOKEN_GUEST_EXPIRE_TIME = 60*60*24*2; //2 Day
    int AUTH_TOKEN_DEFAULT_EXPIRE_TIME = 60*60; //60 Min
    String AUTH_TOKEN_NAME = "COMMON_AUTH_TOKEN";
    String AUTH_PASSWORD_KEY = "dreamfish_blog_sz_password";

    UserExtened authGetUserInfo(String name);
    UserExtened authGetAuthedUserInfo(HttpServletRequest request);

    Result authDoLogin(User user, HttpServletRequest request, HttpServletResponse response);
    Result authDoTest(HttpServletRequest request);
    Result authDoLogout(HttpServletRequest request, HttpServletResponse response, String redirect_uri) throws IOException;

    int authLogin(String userNaame, String passwd, HttpServletRequest request);
    int authForToken(HttpServletRequest request, Cookie clientToken, Integer requireLevel, Integer requirePrivileges);
    int checkUserAuth(HttpServletRequest request);
    int checkUserAuth(HttpServletRequest request, Integer requireLevel);
    int checkUserAuth(HttpServletRequest request, Integer requireLevel, Integer requirePrivileges);
    String genAuthToken(HttpServletRequest request);
    String genAuthToken(HttpServletRequest request, int expireTime);
    void authClear(HttpServletRequest request);

}
