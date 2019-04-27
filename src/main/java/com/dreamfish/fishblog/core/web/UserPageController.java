package com.dreamfish.fishblog.core.web;

import com.dreamfish.fishblog.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserPageController {


    @Autowired
    private UserService userService = null;

    @GetMapping("/user/")
    public String userPageSelf() { return "blog-user"; }
    @GetMapping("/user/{userId}/")
    public String userPage() { return "blog-user"; }

    @GetMapping("/user/center/change-passwd/")
    public String changecPassword()  { return "admin/auth-change-passwd"; }
    @GetMapping("/user/center/rec-passwd/")
    public String recPassword()  { return "admin/auth-rec-passwd"; }
    @PostMapping("/user/center/rec-passwd/send/")
    public ModelAndView recPasswordSend(@RequestParam("rec_email") @NonNull String email)  {
        ModelAndView view = new ModelAndView("admin/auth-sign-out");

        userService.sendRepasswordMessage(email);

        view.addObject("logout_stat", "发送找回密码信息成功");
        view.addObject("logout_msg", "我们已经成功向您提供的地址发送了一封找回密码的信息，它会引导您找回密码，请注意查收");
        return view;
    }
    @GetMapping("/user/center/rec-passwd2/")
    public String recPasswordReal(@RequestParam("token") @NonNull String token)  {
        return "admin/auth-change-passwd";
    }
    @GetMapping("/user/center/active/")
    public ModelAndView activeUser(@RequestParam("token") @NonNull String token)  {
        ModelAndView view = new ModelAndView("admin/auth-active-user-result");
        view.addObject("active_success", userService.activeUser(token));
        return view;
    }
    @GetMapping("/user/center/baned/")
    public ModelAndView userBaned() {
        ModelAndView view = new ModelAndView("admin/auth-sign-out");
        view.addObject("logout_stat", "由于管理员或管理者进行的设置，拒绝此账号登录本站");
        view.addObject("logout_msg", "请联系管理员获得更详细的信息，或换用账号登录");
        return view;
    }
}
