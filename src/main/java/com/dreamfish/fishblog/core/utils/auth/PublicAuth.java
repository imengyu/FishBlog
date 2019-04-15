package com.dreamfish.fishblog.core.utils.auth;

import com.dreamfish.fishblog.core.entity.User;
import com.dreamfish.fishblog.core.exception.InvalidArgumentException;
import com.dreamfish.fishblog.core.service.AuthService;
import com.dreamfish.fishblog.core.utils.StringUtils;
import com.dreamfish.fishblog.core.utils.request.CookieUtils;
import com.dreamfish.fishblog.core.utils.request.IpUtil;
import com.dreamfish.fishblog.core.utils.response.AuthCode;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class PublicAuth {

    /**
     * 认证Token
     * @param clientToken 颁发给用户的名为 AUTH_TOKEN_NAME 的 认证Token Cookie
     * @return 返回 AuthCode 作为认证状态
     */
    public static int authForToken(HttpServletRequest request, Cookie clientToken, Integer requireLevel, Integer requirePrivileges) {
        if(clientToken==null)
            return AuthCode.FAIL_BAD_TOKEN;
        String token = clientToken.getValue();
        Integer tokenCheckResult;
        String[] tokenOrgData;

        try {
            tokenCheckResult = TokenAuthUtils.checkToken(token, TokenAuthUtils.TOKEN_DEFAULT_KEY);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
            return AuthCode.UNKNOW;
        }

        if(tokenCheckResult.intValue() == TokenAuthUtils.TOKEN_CHECK_BAD_TOKEN) {
            return AuthCode.FAIL_BAD_TOKEN;
        }
        else if(tokenCheckResult.intValue() == TokenAuthUtils.TOKEN_CHECK_EXPIRED){
            return AuthCode.FAIL_EXPIRED;
        }


        try {
            tokenOrgData = TokenAuthUtils.decodeToken(token, TokenAuthUtils.TOKEN_DEFAULT_KEY);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
            return AuthCode.UNKNOW;
        }

        if(tokenOrgData.length >= 3) {
            String tokenData = tokenOrgData[2];
            String[] tokenOrgCustomData = tokenData.split("#");
            if (tokenOrgCustomData.length >= 4) {
                if (!tokenOrgCustomData[0].equals(IpUtil.getIpAddr(request))) return AuthCode.FAIL_BAD_IP;
                if (requireLevel!= User.LEVEL_NOT_REQUIRED && Integer.parseInt(tokenOrgCustomData[2]) > requireLevel)
                    return AuthCode.FAIL_NO_PRIVILEGE;
                if(requirePrivileges != 0 && Integer.parseInt(tokenOrgCustomData[2]) != User.LEVEL_ADMIN && ((Integer.parseInt(tokenOrgCustomData[3]) & requirePrivileges) != requirePrivileges))
                    return AuthCode.FAIL_NO_PRIVILEGE;
                return AuthCode.SUCCESS;
            }
        }

        return AuthCode.FAIL_BAD_TOKEN;
    }

    /**
     * 认证当前请求用户是否登录
     * @param request 当前请求
     * @return 返回 AuthCode 作为认证状态
     */
    public static int authCheck(HttpServletRequest request){
        return authCheckIncludeLevel(request, User.LEVEL_NOT_REQUIRED);
    }

    /**
     * 认证当前请求用户是否登录并认证用户组
     * @param request 当前请求
     * @param requireLevel 需要的用户组
     * @return 返回 AuthCode 作为认证状态
     */
    public static int authCheckIncludeLevel(HttpServletRequest request, Integer requireLevel){
        Cookie cookie = CookieUtils.findCookieByName(request.getCookies(), AuthService.AUTH_TOKEN_NAME);
        if(cookie!=null) return authForToken(request, cookie, requireLevel, 0);
        else return AuthCode.FAIL_NOT_LOGIN;
    }

    /**
     * 认证当前请求用户是否登录并认证用户组以及权限
     * @param request 当前请求
     * @param requireLevel 需要的用户组
     * @param requirePrivileges 需要的用户附加权限，查看 @see UserPrivileges
     * @return 返回 AuthCode 作为认证状态
     */
    public static int authCheckIncludeLevelAndPrivileges(HttpServletRequest request, Integer requireLevel, Integer requirePrivileges){
        Cookie cookie = CookieUtils.findCookieByName(request.getCookies(), AuthService.AUTH_TOKEN_NAME);
        if(cookie!=null) return authForToken(request, cookie, requireLevel, requirePrivileges);
        else return AuthCode.FAIL_NOT_LOGIN;
    }


    /**
     * 获取当前已认证请求的用户ID
     * @param request 当前请求
     * @return 返回 AuthCode 作为认证状态
     */
    public static Integer authGetUseId(HttpServletRequest request){

        Cookie clientToken = CookieUtils.findCookieByName(request.getCookies(), AuthService.AUTH_TOKEN_NAME);
        if(clientToken==null)
            return 0;

        String token = clientToken.getValue();
        Integer tokenCheckResult;
        String[] tokenOrgData;

        try {
            tokenCheckResult = TokenAuthUtils.checkToken(token, TokenAuthUtils.TOKEN_DEFAULT_KEY);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
            return AuthCode.UNKNOW;
        }

        if(tokenCheckResult.intValue() == TokenAuthUtils.TOKEN_CHECK_BAD_TOKEN) {
            return AuthCode.FAIL_BAD_TOKEN;
        }
        else if(tokenCheckResult.intValue() == TokenAuthUtils.TOKEN_CHECK_EXPIRED){
            return AuthCode.FAIL_EXPIRED;
        }


        try {
            tokenOrgData = TokenAuthUtils.decodeToken(token, TokenAuthUtils.TOKEN_DEFAULT_KEY);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
            return AuthCode.UNKNOW;
        }

        if(tokenOrgData.length >= 3) {
            String tokenData = tokenOrgData[2];
            String[] tokenOrgCustomData = tokenData.split("#");
            if (tokenOrgCustomData.length >= 2) {
                if (StringUtils.isInteger(tokenOrgCustomData[1]))
                    return Integer.parseInt(tokenOrgCustomData[1]);
                else return AuthCode.UNKNOW;
            }
        }

        return AuthCode.UNKNOW;
    }
}
