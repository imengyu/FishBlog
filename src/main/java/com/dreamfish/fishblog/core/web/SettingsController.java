package com.dreamfish.fishblog.core.web;

import com.alibaba.fastjson.JSONObject;
import com.dreamfish.fishblog.core.annotation.RequestAuth;
import com.dreamfish.fishblog.core.annotation.RequestPrivilegeAuth;
import com.dreamfish.fishblog.core.config.ConstConfig;
import com.dreamfish.fishblog.core.entity.User;
import com.dreamfish.fishblog.core.enums.UserPrivileges;
import com.dreamfish.fishblog.core.service.SettingsService;
import com.dreamfish.fishblog.core.utils.Result;
import com.dreamfish.fishblog.core.utils.json.JsonUtils;
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
    @RequestMapping(value = "/settings.json", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public String getSettingsJson() { return JSONObject.parseObject(settingsService.getSettingsJSON()).toJSONString(); }

    @CacheEvict(value = "blog-settings-cache", allEntries = true)
    @PutMapping("/settings.json")
    @RequestAuth(User.LEVEL_WRITER)
    @RequestPrivilegeAuth(UserPrivileges.PRIVILEGE_GLOBAL_SETTINGS)
    @ResponseBody
    public Result setSettingsJson(@RequestBody JSONObject settings) {
        return Result.success(settingsService.setSettingsJSON(settings));
    }

    @ResponseBody
    @CacheEvict(value = "blog-settings-cache", allEntries = true)
    @PostMapping("/settings")
    @RequestAuth(User.LEVEL_WRITER)
    @RequestPrivilegeAuth(UserPrivileges.PRIVILEGE_GLOBAL_SETTINGS)
    public Result setSetting(@RequestBody JSONObject value){
        return settingsService.setSetting(value.getString("key"), JsonUtils.JsonValueToString(value, "value"));
    }

    @ResponseBody
    @GetMapping("/settings")
    @RequestAuth(User.LEVEL_WRITER)
    @RequestPrivilegeAuth(UserPrivileges.PRIVILEGE_GLOBAL_SETTINGS)
    public Result getSetting(@RequestParam("key") String key){
        return settingsService.getSetting(key);
    }
    }
