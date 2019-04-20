package com.dreamfish.fishblog.core.service;

import com.dreamfish.fishblog.core.utils.Result;

public interface LogService {


    Result getLogsWithPageable(Integer pageIndex, Integer pageSize);

    void writeLog(String action, Integer user, String userName, String ip);
}

