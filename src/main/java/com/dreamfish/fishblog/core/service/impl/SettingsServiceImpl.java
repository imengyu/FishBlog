package com.dreamfish.fishblog.core.service.impl;

import com.dreamfish.fishblog.core.entity.SettingItem;
import com.dreamfish.fishblog.core.mapper.SettingsMapper;
import com.dreamfish.fishblog.core.mapper.UserMapper;
import com.dreamfish.fishblog.core.service.MessagesService;
import com.dreamfish.fishblog.core.service.SettingsService;
import com.dreamfish.fishblog.core.utils.Result;
import com.dreamfish.fishblog.core.utils.auth.PublicAuth;
import com.dreamfish.fishblog.core.utils.log.ActionLog;
import com.dreamfish.fishblog.core.utils.request.ContextHolderUtils;
import com.dreamfish.fishblog.core.utils.request.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class SettingsServiceImpl implements SettingsService {

    @Autowired
    private UserMapper userMapper = null;
    @Autowired
    private SettingsMapper settingsMapper = null;
    @Autowired
    private MessagesService messagesService = null;

    @Override
    public String getSettingsJs() {

        String js = "";

        js += "var sendStats = " + settingsMapper.getSetting("sendStats") + ";";
        js += "var anonymousComment = " + settingsMapper.getSetting("anonymousComment") + ";";
        js += "var enableSearch = " + settingsMapper.getSetting("enableSearch") + ";";
        js += "var enableRegister = " + settingsMapper.getSetting("enableRegister") + ";";
        js += "var address_image_center = '" + settingsMapper.getSetting("imageCenter") + "';";
        js += "var sideCustomArea = '" + settingsMapper.getSetting("sideCustomArea") + "';";

        return js;
    }

    @Override
    public Result setSetting(String key, String data) {

        HttpServletRequest request = ContextHolderUtils.getRequest();
        Integer uid = PublicAuth.authGetUseId(request);
        String name = userMapper.getUserNameById(uid);

        //通知管理员
        String oldSetting = settingsMapper.getSetting(key);
        String message = "已将系统设置 <b>" + key + "</b> 从 <b>" + oldSetting + "</b> 改为 <b>" + data + "</b> 详情请查看系统日志";
        if(uid == 1) messagesService.sendMessage(1, 0, "您更改了系统设置", message);
        else messagesService.sendMessage(1, 0, "用户 " + name + " 更改了系统设置", message);

        //日志
        ActionLog.logUserAction("更新设置：" + key + "旧值：" + oldSetting + " 新值：" + data, uid, name, request);

        settingsMapper.setSetting(key, data);
        return Result.success();
    }

    @Override
    public Result getSetting(String key) {
        return Result.success(settingsMapper.getSetting(key));
    }

    @Override
    public Result getSettings(){
        return Result.success(settingsMapper.getSettings());
    }

    @Override
    public String getSettingString(String key) {
        return settingsMapper.getSetting(key);
    }
}
