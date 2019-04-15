package com.dreamfish.fishblog.core.web;

import com.dreamfish.fishblog.core.annotation.RequestAuth;
import com.dreamfish.fishblog.core.entity.User;
import com.dreamfish.fishblog.core.service.AuthService;
import com.dreamfish.fishblog.core.utils.StringUtils;
import com.dreamfish.fishblog.core.utils.auth.PublicAuth;
import com.dreamfish.fishblog.core.utils.request.CookieUtils;
import com.dreamfish.fishblog.core.utils.response.AuthCode;
import com.dreamfish.fishblog.core.utils.request.RequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 后台页面控制器
 */
@Controller
public class AdminPageController {

    @Autowired
    private AuthService authService;

    //request And response
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    @GetMapping("/sign-in/")
    public String signIn(){
        if(PublicAuth.authCheckIncludeLevel(request, User.LEVEL_WRITER) < AuthCode.SUCCESS) return "admin/auth-sign-in";
        else return "redirect:/admin/";
    }
    @GetMapping("/sign-out/")
    public ModelAndView signOut(@RequestParam(value = "redirect_uri", required = false) String redirect_uri) throws IOException {
        ModelAndView view = new ModelAndView("admin/auth-sign-out");

        int authCode = authService.checkUserAuth(request);
        CookieUtils.setookie(response, AuthService.AUTH_TOKEN_NAME, "", 0);
        authService.authClear(request);
        if(!StringUtils.isBlank(redirect_uri))
            response.sendRedirect(redirect_uri);
        else {
            if (authCode >= AuthCode.SUCCESS) {
                view.addObject("logout_stat", "退出登录成功");
                view.addObject("logout_msg", "您的账号已经安全退出");
            }else{
                view.addObject("logout_stat", "您没有登录");
                view.addObject("logout_msg", "或者登录信息已经过期，请重新登录");
            }
        }
        return view;
    }

    @GetMapping("/user/change-passwd/")
    @RequestAuth(value = User.LEVEL_WRITER, redirectTo = "/sign-in/")
    public String changecPassword()  { return "admin/auth-change-passwd"; }
    @GetMapping("/user/rec-passwd/")
    public String recPassword()  { return "admin/auth-rec-passwd"; }
    @PostMapping("/user/rec-passwd/send/")
    public ModelAndView recPasswordSend()  {
        ModelAndView view = new ModelAndView("admin/auth-sign-out");




        view.addObject("logout_stat", "发送找回密码信息成功");
        view.addObject("logout_msg", "我们已经成功向您提供的地址发送了一封找回密码的信息，它会引导您恢复密码，请注意查收");
        return view;
    }

    @GetMapping("/admin/")
    @RequestAuth(value = User.LEVEL_WRITER, redirectTo = "/sign-in/")
    public String adminIndex(){
        return "admin/dashboard-container";
    }
    @GetMapping("/admin/{page}/")
    @RequestAuth(value = User.LEVEL_WRITER, redirectTo = "/sign-in/")
    public String adminDashboard(@PathVariable String page){
        return "admin/dashboard-container";
    }

}
