package com.dreamfish.fishblog.core.web;

import com.dreamfish.fishblog.core.annotation.RequestAuth;
import com.dreamfish.fishblog.core.annotation.RequestPrivilegeAuth;
import com.dreamfish.fishblog.core.config.ConstConfig;
import com.dreamfish.fishblog.core.entity.User;
import com.dreamfish.fishblog.core.enums.UserPrivileges;
import com.dreamfish.fishblog.core.service.SettingsService;
import com.dreamfish.fishblog.core.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 设置控制类
 */
@Controller
@RequestMapping(ConstConfig.API_PUBLIC)
public class SettingsController {

    @Autowired
    private SettingsService settingsService = null;

    @Cacheable("blog-settings-cache")
    @ResponseBody
    @GetMapping("/settings.js")
    public String getSettingsJs() { return settingsService.getSettingsJs(); }

    @GetMapping("/version")
    @ResponseBody
    public Result version() {
        return Result.success("1.1.3.0330" );
    }

    @ResponseBody
    @CacheEvict(value = "blog-settings-cache", allEntries = true)
    @PostMapping("/settings/{key}")
    @RequestAuth(User.LEVEL_WRITER)
    @RequestPrivilegeAuth(UserPrivileges.PRIVILEGE_GLOBAL_SETTINGS)
    public Result setSetting(@PathVariable("key") String key, @RequestBody @RequestParam("value") String value){
        return settingsService.setSetting(key, value);
    }

    @ResponseBody
    @GetMapping("/settings/{key}")
    @RequestAuth(User.LEVEL_WRITER)
    @RequestPrivilegeAuth(UserPrivileges.PRIVILEGE_GLOBAL_SETTINGS)
    public Result getSetting(@RequestParam("key") String key){
        return settingsService.getSetting(key);
    }

    @ResponseBody
    @GetMapping("/settings")
    @RequestAuth(User.LEVEL_WRITER)
    @RequestPrivilegeAuth(UserPrivileges.PRIVILEGE_GLOBAL_SETTINGS)
    public Result getSettings(){
        return settingsService.getSettings();
    }


}
