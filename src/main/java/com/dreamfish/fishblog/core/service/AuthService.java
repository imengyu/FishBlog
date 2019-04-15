package com.dreamfish.fishblog.core.service;

import com.dreamfish.fishblog.core.entity.UserExtened;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public interface AuthService {

    int AUTH_TOKEN_GUEST_EXPIRE_TIME = 60*60*24; //1 Day
    int AUTH_TOKEN_DEFAULT_EXPIRE_TIME = 60*60; //60 Min
    String AUTH_TOKEN_NAME = "COMMON_AUTH_TOKEN";

    UserExtened authGetAuthedUserInfo(HttpServletRequest request);
    int authLogin(String userNaame, String passwd, HttpServletRequest request);
    int authForToken(HttpServletRequest request, Cookie clientToken, Integer requireLevel, Integer requirePrivileges);
    int checkUserAuth(HttpServletRequest request);
    int checkUserAuth(HttpServletRequest request, Integer requireLevel);
    int checkUserAuth(HttpServletRequest request, Integer requireLevel, Integer requirePrivileges);
    String genAuthToken(HttpServletRequest request);
    String genAuthToken(HttpServletRequest request, int expireTime);
    void authClear(HttpServletRequest request);

}
