package com.dreamfish.fishblog.core.utils.log;

import com.dreamfish.fishblog.core.service.impl.LogServiceImpl;
import com.dreamfish.fishblog.core.utils.auth.PublicAuth;
import com.dreamfish.fishblog.core.utils.request.IpUtil;
import com.dreamfish.fishblog.core.utils.response.AuthCode;

import javax.servlet.http.HttpServletRequest;

/**
 * 动作日志写入帮助类
 */
public class ActionLog {

    public static final String ACTION_LOGIN = "登录";
    public static final String ACTION_LOGOUT = "登出";


    /**
     * 动作日志写入
     * @param action 动作名称
     * @param user 用户ID
     * @param userName 用户名
     * @param ip 操作IP
     */
    public static void logUserAction(String action, Integer user, String userName, String ip){
        LogServiceImpl.logService.writeLog(action, user, userName, ip);
    }
    public static void logUserAction(String action, Integer user, String ip){
        LogServiceImpl.logService.writeLog(action, user, ip);
    }
    public static void logUserAction(String action, HttpServletRequest request){

        Integer id = PublicAuth.authGetUseId(request);
        if(id >= AuthCode.SUCCESS) LogServiceImpl.logService.writeLog(action, id, IpUtil.getIpAddr(request));
        else LogServiceImpl.logService.writeLog(action, 0, IpUtil.getIpAddr(request));
    }



}
