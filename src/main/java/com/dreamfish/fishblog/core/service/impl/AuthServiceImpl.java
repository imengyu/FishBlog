package com.dreamfish.fishblog.core.service.impl;

import com.dreamfish.fishblog.core.entity.User;
import com.dreamfish.fishblog.core.entity.UserExtened;
import com.dreamfish.fishblog.core.exception.InvalidArgumentException;
import com.dreamfish.fishblog.core.mapper.UserMapper;
import com.dreamfish.fishblog.core.service.AuthService;
import com.dreamfish.fishblog.core.utils.StringUtils;
import com.dreamfish.fishblog.core.utils.encryption.AESUtils;
import com.dreamfish.fishblog.core.utils.log.ActionLog;
import com.dreamfish.fishblog.core.utils.response.AuthCode;

import com.dreamfish.fishblog.core.utils.auth.PublicAuth;
import com.dreamfish.fishblog.core.utils.request.CookieUtils;
import com.dreamfish.fishblog.core.utils.auth.TokenAuthUtils;
import com.dreamfish.fishblog.core.utils.request.IpUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;


/**
 * 登录认证服务
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper = null;

    /**
     * 登录认证
     * @param userName 用户名
     * @param passwd 密码
     * @param request 当前请求
     * @return 返回 AuthCode 作为认证状态
     */
    @Override
    public int authLogin(String userName, String passwd, HttpServletRequest request) {
        if(checkUserAuth(request) == AuthCode.SUCCESS)
            return AuthCode.SUCCESS_ALREDAY_LOGGED;
        if(StringUtils.isBlank(userName) || StringUtils.isBlank(passwd))
            return AuthCode.FAIL_BAD_PARARM;

        List<User> users =  userMapper.findByUserName(userName);
        if(users.size() == 0) return AuthCode.FAIL_NOUSER_FOUND;
        User user = users.get(0);
        if(!user.getActived())  return AuthCode.FAIL_NOT_ACTIVE;

        String passwordRs = AESUtils.encrypt(passwd + "$" + userName, AUTH_PASSWORD_KEY);
        //System.out.println("passwordRs: " + passwordRs);

        if(passwordRs.equals(user.getPasswd())) {

            if(user.getLevel() == User.LEVEL_LOCKED)
                return AuthCode.FAIL_USER_LOCKED;

            //日志
            ActionLog.logUserAction(ActionLog.ACTION_LOGIN, user.getId(), user.getFriendlyName(), IpUtil.getIpAddr(request));

            HttpSession session = request.getSession();
            session.setMaxInactiveInterval(AUTH_TOKEN_DEFAULT_EXPIRE_TIME);
            session.setAttribute("currentLoggedUserId", user.getId());
            session.setAttribute("currentLoggedUserName", user.getName());
            session.setAttribute("currentLoggedUserLevel", user.getLevel());
            session.setAttribute("currentLoggedUserPrivileges", user.getPrivilege());
            return user.getLevel() == User.LEVEL_GUEST ? AuthCode.SUCCESS_GUEST : AuthCode.SUCCESS;
        }
        else {
            return AuthCode.FAIL_BAD_PASSWD;
        }
    }
    /**
     * 认证Token
     * @param clientToken 颁发给用户的名为 AUTH_TOKEN_NAME 的 认证Token Cookie
     * @return 返回 AuthCode 作为认证状态
     */

    @Override
    public int authForToken(HttpServletRequest request, Cookie clientToken, Integer requireLevel, Integer requirePrivileges) {
        return PublicAuth.authForToken(request, clientToken, requireLevel, requirePrivileges);
    }

    @Override
    public UserExtened authGetUserInfo(String name) {
        return userMapper.findFullByUserName(name);
    }

    /**
     * 获取已认证用户信息
     * @param request 当前请求
     * @return 返回当前已认证用户信息
     */
    @Override
    public UserExtened authGetAuthedUserInfo(HttpServletRequest request){
        Integer currentLoggedUserId = PublicAuth.authGetUseId(request);
        if(currentLoggedUserId!=null && currentLoggedUserId!=0 && userMapper.isUserIdExists(currentLoggedUserId)!= null)
            return userMapper.findFullById(currentLoggedUserId);
        return null;
    }

    /**
     * 认证当前请求用户是否登录
     * @param request 当前请求
     * @return 返回 AuthCode 作为认证状态
     */
    @Override
    public int checkUserAuth(HttpServletRequest request){ return checkUserAuth(request, User.LEVEL_NOT_REQUIRED); }
    /**
     * 认证当前请求用户是否登录（限制权限）
     * @param request 当前请求
     * @return 返回 AuthCode 作为认证状态
     */
    @Override
    public int checkUserAuth(HttpServletRequest request, Integer requireLevel){
        Cookie cookie = CookieUtils.findCookieByName(request.getCookies(), AUTH_TOKEN_NAME);
        if(cookie!=null) return authForToken(request, cookie, requireLevel, 0);
        else return AuthCode.FAIL_NOT_LOGIN;
    }
    /**
     * 认证当前请求用户是否登录（限制权限）以及是否有某个附加权限
     * @param request 当前请求
     * @return 返回 AuthCode 作为认证状态
     */
    @Override
    public int checkUserAuth(HttpServletRequest request, Integer requireLevel, Integer requirePrivileges){
        Cookie cookie = CookieUtils.findCookieByName(request.getCookies(), AUTH_TOKEN_NAME);
        if(cookie!=null) return authForToken(request, cookie, requireLevel, requirePrivileges);
        else return AuthCode.FAIL_NOT_LOGIN;
    }

    /**
     * 为当前请求用户生成 认证TOKEN 使用默认过期时间
     * @return 认证TOKEN
     */
    @Override
    public String genAuthToken(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String currentLoggedUserName = (String)session.getAttribute("currentLoggedUserName");
        Integer currentLoggedUserId = (Integer)session.getAttribute("currentLoggedUserId");
        Integer currentLoggedUserLevel = (Integer)session.getAttribute("currentLoggedUserLevel");
        Integer currentLoggedUserPrivileges = (Integer)session.getAttribute("currentLoggedUserPrivileges");
        if(currentLoggedUserName == null) return null;
        try {
            String ip = IpUtil.getIpAddr(request);
            return TokenAuthUtils.genToken(AUTH_TOKEN_DEFAULT_EXPIRE_TIME, ip + "#" + currentLoggedUserId + "#" + currentLoggedUserLevel + "#" + currentLoggedUserPrivileges);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
            return "";
        }
    }
    /**
     * 为当前请求用户生成 认证TOKEN
     * @param expireTime 设置过期时间
     * @return 认证TOKEN
     */
    @Override
    public String genAuthToken(HttpServletRequest request, int expireTime) {
        HttpSession session = request.getSession();
        String currentLoggedUserName = (String)session.getAttribute("currentLoggedUserName");
        Integer currentLoggedUserId = (Integer)session.getAttribute("currentLoggedUserId");
        Integer currentLoggedUserLevel = (Integer)session.getAttribute("currentLoggedUserLevel");
        Integer currentLoggedUserPrivileges = (Integer)session.getAttribute("currentLoggedUserPrivileges");
        if(currentLoggedUserName == null) return null;
        try {
            String ip = IpUtil.getIpAddr(request);
            return TokenAuthUtils.genToken(expireTime, ip + "#" + currentLoggedUserId + "#" + currentLoggedUserLevel + "#" + currentLoggedUserPrivileges);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 清除登录状态
     */
    @Override
    public void authClear(HttpServletRequest request) {

        //日志
        ActionLog.logUserAction(ActionLog.ACTION_LOGOUT, request);

        HttpSession session = request.getSession();
        session.removeAttribute("currentLoggedUserId");
        session.removeAttribute("currentLoggedUserName");
        session.removeAttribute("currentLoggedUserLevel");
        session.removeAttribute("currentLoggedUserPrivileges");
    }

}
