package com.dreamfish.fishblog.core.service;

import com.alibaba.fastjson.JSONObject;
import com.dreamfish.fishblog.core.utils.Result;


public interface MessagesService {

    Result getMessagesWithPager(Integer userId, Integer pageIndex, Integer pageSize);
    Result getMessagesNotRead(Integer userId, Integer maxCount);
    Result setReadMessages(Integer userId, JSONObject messageIds);
    Result setReadAllMessages(Integer userIds);
    Result deleteMessages(Integer userId, JSONObject messageIds);
    Result deleteAllMessages(Integer userId);
    Result sendMessage(Integer userId, Integer fromUser, String title, String content);
}
