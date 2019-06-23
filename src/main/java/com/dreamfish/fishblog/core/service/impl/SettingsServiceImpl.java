package com.dreamfish.fishblog.core.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dreamfish.fishblog.core.entity.SettingItem;
import com.dreamfish.fishblog.core.mapper.SettingsMapper;
import com.dreamfish.fishblog.core.mapper.UserMapper;
import com.dreamfish.fishblog.core.service.MessagesService;
import com.dreamfish.fishblog.core.service.RedisService;
import com.dreamfish.fishblog.core.service.SettingsService;
import com.dreamfish.fishblog.core.utils.Result;
import com.dreamfish.fishblog.core.utils.auth.PublicAuth;
import com.dreamfish.fishblog.core.utils.encryption.Base64Utils;
import com.dreamfish.fishblog.core.utils.json.JsonUtils;
import com.dreamfish.fishblog.core.utils.log.ActionLog;
import com.dreamfish.fishblog.core.utils.request.ContextHolderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

@Service
public class SettingsServiceImpl implements SettingsService {

    @Autowired
    private UserMapper userMapper = null;
    @Autowired
    private SettingsMapper settingsMapper = null;
    @Autowired
    private MessagesService messagesService = null;
    @Autowired
    private RedisService redisService = null;

    @Override
    public String getSettingsJSON() {
        //生成前端使用的json
        StringBuilder js = new StringBuilder("{ ");
        List<SettingItem> sets = settingsMapper.getSettings();
        for(SettingItem s : sets) {
            js.append(" ");
            js.append(s.getName());
            js.append(": ");
            js.append(Base64Utils.decode(s.getData()));
            js.append(",");
        }
        js.append(" }");
        return js.toString();
    }

    @Override
    public JSONObject setSettingsJSON(JSONObject settings) {
        Set<String> it = settings.keySet();
        for(String key : it){
            String data = JsonUtils.JsonValueToString(settings, key);
            //更新设置缓存
            redisService.set("FishSetting_" + key, data);
            settingsMapper.setSetting(key, Base64Utils.encode(data));
        }

        //日志
        HttpServletRequest request = ContextHolderUtils.getRequest();
        Integer uid = PublicAuth.authGetUseId(request);
        String name = userMapper.getUserNameById(uid);
        if(uid == 1) messagesService.sendMessage(1, 0, "您更改了系统设置", "更新设置 JSON");
        else messagesService.sendMessage(1, 0, "用户 " + name + " 更改了系统设置", "更新设置 JSON");
        //日志
        ActionLog.logUserAction("更新设置 JSON", uid, name, request);

        return settings;
    }

    @Override
    public Result setSetting(String key, String data) {

        HttpServletRequest request = ContextHolderUtils.getRequest();
        Integer uid = PublicAuth.authGetUseId(request);
        String name = userMapper.getUserNameById(uid);

        //通知管理员
        String message = "已更改系统设置 <b>" + key + "</b> 详情请查看系统日志";
        if(uid == 1) messagesService.sendMessage(1, 0, "您更改了系统设置", message);
        else messagesService.sendMessage(1, 0, "用户 " + name + " 更改了系统设置", message);

        //日志
        ActionLog.logUserAction("更新设置：" + key, uid, name, request);

        //更新设置缓存
        redisService.set("FishSetting_" + key, data);
        settingsMapper.setSetting(key, Base64Utils.encode(data));
        return Result.success();
    }

    @Override
    public Result getSetting(String key) {
        return Result.success(Base64Utils.decode(settingsMapper.getSetting(key)));
    }


    @Override
    public String getSettingCache(String key) {
        return redisService.get("FishSetting_" + key);
    }
    @Override
    public void reloadSettingsToCache() {
        List<SettingItem> sets = settingsMapper.getSettings();
        for(SettingItem s : sets) {
            String value = Base64Utils.decode(s.getData());
            redisService.set("FishSetting_" + s.getName(), value);
            redisService.persist("FishSetting_" +  s.getName());
        }
    }
}
