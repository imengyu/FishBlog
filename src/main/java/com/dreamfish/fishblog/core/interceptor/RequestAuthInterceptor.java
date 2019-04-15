package com.dreamfish.fishblog.core.interceptor;

import com.dreamfish.fishblog.core.annotation.RequestAuth;
import com.dreamfish.fishblog.core.annotation.RequestPrivilegeAuth;
import com.dreamfish.fishblog.core.entity.User;
import com.dreamfish.fishblog.core.utils.Result;
import com.dreamfish.fishblog.core.utils.StringUtils;
import com.dreamfish.fishblog.core.utils.auth.PublicAuth;
import com.dreamfish.fishblog.core.utils.response.AuthCode;
import com.dreamfish.fishblog.core.utils.response.ResponseUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 自动验证注解拦截器
 */
public class RequestAuthInterceptor extends HandlerInterceptorAdapter {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if(!(handler instanceof HandlerMethod))
            return true;

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        //First auth user and user level
        RequestAuth requestAuth = method.getAnnotation(RequestAuth.class);
        if (requestAuth != null && requestAuth.required() && requestAuth.value() != 0) {
            int authCode = PublicAuth.authCheckIncludeLevel(request, requestAuth.value());
            if(authCode < AuthCode.SUCCESS){
                if(StringUtils.isBlank(requestAuth.redirectTo())) {
                    Result result = Result.failure(requestAuth.unauthCode(), requestAuth.unauthMsg(), String.valueOf(authCode));
                    response.setStatus(Integer.parseInt(requestAuth.unauthCode()));
                    ResponseUtils.responseOutWithJson(response, result);
                }else{
                    response.sendRedirect(requestAuth.redirectTo() + "?error=" +
                            (authCode == AuthCode.FAIL_EXPIRED ? "SessionOut" : "RequestLogin") + "&redirect_url=" + request.getRequestURI());
                }
                return false;
            }
        }

        //Second auth user privilege
        //First auth user and user level
        RequestPrivilegeAuth requestPrivilegeAuth = method.getAnnotation(RequestPrivilegeAuth.class);
        if (requestPrivilegeAuth != null && requestPrivilegeAuth.required() && requestPrivilegeAuth.value() != 0) {
            int authCode = PublicAuth.authCheckIncludeLevelAndPrivileges(request, requestAuth == null ? User.LEVEL_NOT_REQUIRED : requestAuth.value(), requestPrivilegeAuth.value());
            if(authCode < AuthCode.SUCCESS){
                Result result = Result.failure(requestPrivilegeAuth.unauthCode(), requestPrivilegeAuth.unauthMsg(), String.valueOf(authCode));
                response.setStatus(Integer.parseInt(requestPrivilegeAuth.unauthCode()));
                ResponseUtils.responseOutWithJson(response, result);
                return false;
            }
        }

        return true;
    }

}
