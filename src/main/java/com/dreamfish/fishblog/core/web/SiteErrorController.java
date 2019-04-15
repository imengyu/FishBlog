package com.dreamfish.fishblog.core.web;

import com.dreamfish.fishblog.core.exception.ErrorResponseEntity;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
class SiteErrorController implements ErrorController {

    @RequestMapping(value = "/error")
    @ResponseBody
    public ErrorResponseEntity handleError(HttpServletRequest request) {
        //获取statusCode:401,404,500
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == 404)
            return new ErrorResponseEntity(statusCode, "未找到指定接口");
        else if (statusCode == 403)
            return new ErrorResponseEntity(statusCode, "拒绝访问接口");
        else if (statusCode == 401)
            return new ErrorResponseEntity(statusCode, "访问需要认证");
        else if (statusCode >= 500)
            return new ErrorResponseEntity(statusCode, "服务暂时不可用，请稍后再试");
        else return new ErrorResponseEntity(statusCode, "服务暂时出现未知错误，请稍后再试");
    }
    @RequestMapping(value = "/error", produces = "text/html")
    public String handleErrorHtml(HttpServletRequest request) {
        //获取statusCode:401,404,500
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == 404) {
            return "blog-404";
        } else if (statusCode == 403) {
            return "blog-403";
        } else if (statusCode >= 500) {
            return "blog-5xx";
        }
        return "";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}