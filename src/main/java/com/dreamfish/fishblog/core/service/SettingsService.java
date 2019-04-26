package com.dreamfish.fishblog.core.service;

import com.dreamfish.fishblog.core.utils.Result;
import org.springframework.web.bind.annotation.RequestParam;

public interface SettingsService {

    String getSettingsJs();
    Result setSetting(String key, String data);
    Result getSetting(String key);
    Result getSettings();
    String getSettingString(String key);
}
