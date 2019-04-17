package com.dreamfish.fishblog.core.web;

import com.dreamfish.fishblog.core.annotation.RequestAuth;
import com.dreamfish.fishblog.core.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserPageController {

    @GetMapping("/user/")
    public String userPageSelf() { return "blog-user"; }
    @GetMapping("/user/{userId}/")
    public String userPage() { return "blog-user"; }

    @GetMapping("/user/change-passwd/")
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
}
