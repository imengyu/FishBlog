package com.dreamfish.fishblog.core.service.impl;

import com.dreamfish.fishblog.core.mapper.SettingsMapper;
import com.dreamfish.fishblog.core.service.SettingsService;
import com.dreamfish.fishblog.core.utils.Result;
import com.dreamfish.fishblog.core.utils.log.ActionLog;
import com.dreamfish.fishblog.core.utils.request.ContextHolderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingsServiceImpl implements SettingsService {

    @Autowired
    private SettingsMapper settingsMapper = null;

    @Override
    public String getSettingsJs() {

        String js = "";

        js += "var sendStats = " + settingsMapper.getSetting("sendStats") + ";";
        js += "var anonymousComment = " + settingsMapper.getSetting("anonymousComment") + ";";
        js += "var enableSearch = " + settingsMapper.getSetting("enableSearch") + ";";
        js += "var address_image_center = '" + settingsMapper.getSetting("imageCenter") + "';";

        return js;
    }

    @Override
    public Result setSetting(String key, String data) {

        ActionLog.logUserAction("更新设置：" + key + " 新值：" + data, ContextHolderUtils.getRequest());

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
}
