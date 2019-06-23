package com.dreamfish.fishblog.core.service;

import com.alibaba.fastjson.JSONObject;
import com.dreamfish.fishblog.core.utils.Result;
import org.springframework.web.bind.annotation.RequestParam;

public interface SettingsService {

    String getSettingsJSON();
    JSONObject setSettingsJSON(JSONObject settings);
    void reloadSettingsToCache();

    Result setSetting(String key, String data);
    Result getSetting(String key);

    String getSettingCache(String key);
}
