package com.dreamfish.fishblog.core.listener;

import com.dreamfish.fishblog.core.service.SettingsService;
import com.dreamfish.fishblog.core.service.StatService;
import com.dreamfish.fishblog.core.utils.ApplicationContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


/**
 * 系统就绪事件监听器
 */
@Component
public class ApplicationReadyEventListener implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private SettingsService settingsService = null;
    @Autowired
    private StatService statService = null;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationStartedEvent) {
        //须通知设置进行加载
        settingsService = ApplicationContextProvider.getBean(SettingsService.class);
        settingsService.reloadSettingsToCache();
        statService = ApplicationContextProvider.getBean(StatService.class);
        statService.setStartUpDate();
    }
}