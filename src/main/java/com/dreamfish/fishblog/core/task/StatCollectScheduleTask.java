package com.dreamfish.fishblog.core.task;

import com.dreamfish.fishblog.core.mapper.SettingsMapper;
import com.dreamfish.fishblog.core.mapper.StatMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 自动访问数据收集
 */
@Component
@Configuration
@EnableScheduling
public class StatCollectScheduleTask {


    @Autowired
    private StatMapper statMapper = null;

    @Autowired
    private SettingsMapper settingsMapper = null;

    /**
     * 每天 23:55:00 刷新PV/IP数据，以及其他数据
     */
    @Scheduled(cron = "0 55 23 * * ?")
    private void updateStatData() {

        boolean collectStat = Boolean.parseBoolean(settingsMapper.getSetting("sendStats"));

        if(collectStat) {

            //添加今日新的记录

            Integer pv = statMapper.getStatIntData("pvToday");
            Integer ip = statMapper.getStatIntData("ipToday");
            Integer comment = statMapper.getStatIntData("commentToday");

            statMapper.addDayLog(pv, ip, comment);

            //转移今日数据到昨日去

            statMapper.updateStatInt("pvYesterday", pv);
            statMapper.updateStatInt("ipYesterday", ip);
            statMapper.updateStatInt("commentYesterday", comment);

            statMapper.updateStatInt("pvToday", 0);
            statMapper.updateStatInt("ipToday", 0);
            statMapper.updateStatInt("commentToday", 0);

            // 删除 时间比较长的数据

            Integer maxSaveStatDays = Integer.parseInt(settingsMapper.getSetting("maxStatSaveDays"));

            statMapper.deleteDayLog(maxSaveStatDays);
            statMapper.deleteStatPage(maxSaveStatDays);
            statMapper.deleteActionLogs(maxSaveStatDays);
            statMapper.clearIpTable();

        }
    }
}

