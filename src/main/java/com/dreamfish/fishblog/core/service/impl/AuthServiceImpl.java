package com.dreamfish.fishblog.core.service.impl;

import com.dreamfish.fishblog.core.entity.User;
import com.dreamfish.fishblog.core.entity.UserExtened;
import com.dreamfish.fishblog.core.exception.InvalidArgumentException;
import com.dreamfish.fishblog.core.mapper.UserMapper;
import com.dreamfish.fishblog.core.service.AuthService;
import com.dreamfish.fishblog.core.service.RedisService;
import com.dreamfish.fishblog.core.service.UserService;
import com.dreamfish.fishblog.core.utils.Result;
import com.dreamfish.fishblog.core.utils.ResultCodeEnum;
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
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * 登录认证服务
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper = null;
    @Autowired
    private UserService userService = null;
    //Redis
    @Autowired
    private RedisService redisService = null;


    /**
     * 进行登录操作
     * @param user 用户登录参数
     * @param request 当前请求
     * @param response 当前返回
     * @return 返回登录结果
     */
    @Override
    public Result authDoLogin(User user, HttpServletRequest request, HttpServletResponse response) {

        String ip = IpUtil.getIpAddr(request);
        HttpSession httpSession = request.getSession();
        String passwordErrCountKey = "password_error_count_" + ip + "_" + user.getName();

        //检查密码错误次数
        Integer passwordErrCountInSession = (Integer)httpSession.getAttribute("PasswordIncorrectCount");
        Integer passwordErrCount = redisService.get(passwordErrCountKey);
        if (passwordErrCountInSession != null && passwordErrCountInSession > 3)
            return Result.failure(ResultCodeEnum.FAILED_AUTH.getCode(), "您的密码错误次数过多，请 15 分钟后再试！");
        if ((passwordErrCount != null && passwordErrCount > 3)) {
            if(passwordErrCountInSession == null || passwordErrCountInSession > 3)
                return Result.failure(ResultCodeEnum.FAILED_AUTH.getCode(), "您的密码错误次数过多，请 15 分钟后再试！");
        }

        int authCode = authLogin(user.getName(), user.getPasswd(), request);
        if(authCode >= AuthCode.SUCCESS){

            //设置错误计数为0
            httpSession.setAttribute("PasswordIncorrectCount", 0);
            redisService.set(passwordErrCountKey, 0, 900, TimeUnit.SECONDS);
            String authToken = "";
            if(authCode==AuthCode.SUCCESS_GUEST){//游客
                authToken = genAuthToken(request, AuthService.AUTH_TOKEN_GUEST_EXPIRE_TIME);
                if (!StringUtils.isEmpty(authToken))
                    CookieUtils.setookie(response, AuthService.AUTH_TOKEN_NAME, authToken, AuthService.AUTH_TOKEN_GUEST_EXPIRE_TIME);
            }else {//普通用户
                authToken = genAuthToken(request);
                if (!StringUtils.isEmpty(authToken))
                    CookieUtils.setookie(response, AuthService.AUTH_TOKEN_NAME, authToken, -1);
            }
            Map<String, Object> resultData = new HashMap<>();
            resultData.put("userData", authGetUserInfo(user.getName()));
            resultData.put("authToken", authToken);
            resultData.put("authTokenName", AuthService.AUTH_TOKEN_NAME);
            return Result.success(resultData);
        }
        else {

            if (authCode != AuthCode.FAIL_USER_LOCKED && authCode != AuthCode.FAIL_NOT_ACTIVE) {
                //设置或增加密码错误次数
                if(passwordErrCountInSession==null) {
                    passwordErrCountInSession = 1;
                    httpSession.setAttribute("PasswordIncorrectCount", passwordErrCountInSession);
                }else{
                    passwordErrCountInSession++;
                    httpSession.setAttribute("PasswordIncorrectCount", passwordErrCountInSession);
                }
                if (passwordErrCount == null) {
                    passwordErrCount = 1;
                    redisService.set(passwordErrCountKey, passwordErrCount, 900, TimeUnit.SECONDS);
                } else {
                    passwordErrCount++;
                    redisService.set(passwordErrCountKey, passwordErrCount, 900, TimeUnit.SECONDS);
                }

                if (passwordErrCount >= 4)
                    return Result.failure(ResultCodeEnum.FAILED_AUTH.getCode(), "您的密码错误次数过多，请 15 分钟后再试！", String.valueOf(authCode));
                else return Result.failure(ResultCodeEnum.FAILED_AUTH.getCode(), "您还可以尝试 " + (4 - passwordErrCount) + " 次", String.valueOf(authCode));

            }else return Result.failure(ResultCodeEnum.FAILED_AUTH, String.valueOf(authCode));
        }
    }

    /**
     * 登录状态检测
     * @param request 当前请求
     * @return 返回登录状态
     */
    @Override
    public Result authDoTest(HttpServletRequest request) {
        Integer authCode = checkUserAuth(request);
        if(authCode >= AuthCode.SUCCESS) return Result.success(authGetAuthedUserInfo(request));
        else {
            Map<String, Object> resultData = new HashMap<>();
            resultData.put("authCode", authCode);
            resultData.put("authTokenName", AuthService.AUTH_TOKEN_NAME);
            resultData.put("authTokenSubmited", CookieUtils.findCookieByName(request.getCookies(), AuthService.AUTH_TOKEN_NAME));
            return Result.failure(resultData, ResultCodeEnum.FAILED_AUTH.getCode(), ResultCodeEnum.FAILED_AUTH.getMsg());
        }
    }

    /**
     * 进行登出操作
     * @param request 当前请求
     * @param response 当前返回
     * @param redirect_uri 跳转URL
     * @return 返回登出操作结果
     * @throws IOException sendRedirect 错误
     */
    @Override
    public Result authDoLogout(HttpServletRequest request, HttpServletResponse response, String redirect_uri) throws IOException {
        Integer authCode = checkUserAuth(request);
        CookieUtils.setookie(response, AuthService.AUTH_TOKEN_NAME, "", 0);
        authClear(request);
        if(!StringUtils.isBlank(redirect_uri)){
            response.sendRedirect(redirect_uri);
            return Result.success();
        }else {
            if (authCode >= AuthCode.SUCCESS) {
                Map<String, Object> resultData = new HashMap<>();
                resultData.put("authTokenName", AuthService.AUTH_TOKEN_NAME);
                return Result.success(resultData);
            }
            else return Result.failure(ResultCodeEnum.FAILED_AUTH, String.valueOf(authCode));
        }
    }


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

        if(passwordRs.equals(user.getPasswd())) {

            if(user.getLevel() == User.LEVEL_LOCKED)
                return AuthCode.FAIL_USER_LOCKED;

            //日志
            ActionLog.logUserAction(ActionLog.ACTION_LOGIN, user.getId(), userService.getUserNameAutoById(user.getId()), IpUtil.getIpAddr(request));

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

    /**
     * 获取用户信息
     * @param name 用户名
     * @return 用户信息
     */
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
        if(currentLoggedUserId!=null && currentLoggedUserId!=0)
            return userMapper.findFullById(currentLoggedUserId);
        return null;
    }



    /**
     * 认证当前请求用户是否登录
     * @param request 当前请求
     * @return 返回 AuthCode 作为认证状态
     */
    @Override
    public int checkUserAuth(HttpServletRequest request){
        return PublicAuth.authCheck(request);
    }
    /**
     * 认证当前请求用户是否登录（限制权限）
     * @param request 当前请求
     * @return 返回 AuthCode 作为认证状态
     */
    @Override
    public int checkUserAuth(HttpServletRequest request, Integer requireLevel){
        return PublicAuth.authCheckIncludeLevel(request, requireLevel);
    }
    /**
     * 认证当前请求用户是否登录（限制权限）以及是否有某个附加权限
     * @param request 当前请求
     * @return 返回 AuthCode 作为认证状态
     */
    @Override
    public int checkUserAuth(HttpServletRequest request, Integer requireLevel, Integer requirePrivileges){
        return PublicAuth.authCheckIncludeLevelAndPrivileges(request, requireLevel, requirePrivileges);
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
