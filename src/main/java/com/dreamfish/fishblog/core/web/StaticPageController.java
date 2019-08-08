package com.dreamfish.fishblog.core.web;

import com.dreamfish.fishblog.core.config.ConstConfig;
import com.dreamfish.fishblog.core.utils.Result;
import com.dreamfish.fishblog.core.utils.request.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class StaticPageController {

    @GetMapping("/")
    public String getIndex(){
        return "index";
    }

    @Autowired
    private HttpServletRequest httpServletRequest;

    @GetMapping(ConstConfig.API_PUBLIC)
    @ResponseBody
    public Result geV1Index(){
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("version", ConstConfig.API_VERSION);

        Map<String, Object> userData = new HashMap<>();
        userData.put("ip", IpUtil.getIpAddr(httpServletRequest));
        userData.put("ua", httpServletRequest.getHeader("user-agent"));

        Map<String, Object> systemData = new HashMap<>();
        systemData.put("home", System.getProperty("user.home"));
        systemData.put("user", System.getProperty("user.name"));
        systemData.put("osInfo", System.getProperty("os.name") + "-" + System.getProperty("os.arch") + "-" + System.getProperty("os.version"));
        systemData.put("javaVersion", System.getProperty("java.version"));

        resultData.put("systemInfo", systemData);
        resultData.put("userInfo", userData);

        return Result.success(resultData);
    }

}
