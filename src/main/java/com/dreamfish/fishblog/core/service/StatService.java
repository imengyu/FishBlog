package com.dreamfish.fishblog.core.service;

import com.alibaba.fastjson.JSONObject;
import com.dreamfish.fishblog.core.utils.Result;


public interface StatService {

    Result updateStat(JSONObject data);
    Result getStatSimple();
    Result getStatIpPv(String startDate, Integer maxCount);
    Result getStatIpPv(Integer page, Integer pageSize);
    Result getStatIpPv();
    Result getStatTopPage(Integer maxCount);
    Result getStatTopPost(Integer maxCount);
}
