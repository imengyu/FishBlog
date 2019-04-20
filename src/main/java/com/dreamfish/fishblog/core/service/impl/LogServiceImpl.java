package com.dreamfish.fishblog.core.service.impl;

import com.dreamfish.fishblog.core.entity.LogItem;
import com.dreamfish.fishblog.core.mapper.UserMapper;
import com.dreamfish.fishblog.core.repository.LogRepository;
import com.dreamfish.fishblog.core.service.LogService;
import com.dreamfish.fishblog.core.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LogServiceImpl implements LogService {

    public static LogServiceImpl logService = null;

    public LogServiceImpl(){
        logService = this;
    }

    @Autowired
    private LogRepository logRepository = null;
    @Autowired
    private UserMapper userMapper = null;

    @Override
    public Result getLogsWithPageable(Integer pageIndex, Integer pageSize) {
        return Result.success(logRepository.findAllByOrderByDatetimeDesc(PageRequest.of(pageIndex, pageSize)));
    }

    @Override
    public void writeLog(String action, Integer user, String userName, String ip) {
        LogItem newLog = new LogItem();
        newLog.setAction(action);
        newLog.setDatetime(new Date());
        newLog.setIp(ip);
        newLog.setUserId(user);
        newLog.setUserName(userName);
        logRepository.saveAndFlush(newLog);
    }

    public void writeLog(String action, Integer user, String ip) {
        LogItem newLog = new LogItem();
        newLog.setAction(action);
        newLog.setDatetime(new Date());
        newLog.setIp(ip);
        newLog.setUserId(user);
        newLog.setUserName(user==0?"未认证用户":userMapper.getUserFriendlyNameById(user));
        logRepository.saveAndFlush(newLog);
    }
}
