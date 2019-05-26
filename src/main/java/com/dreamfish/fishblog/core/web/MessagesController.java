package com.dreamfish.fishblog.core.web;

import com.alibaba.fastjson.JSONObject;
import com.dreamfish.fishblog.core.config.ConstConfig;
import com.dreamfish.fishblog.core.service.MessagesService;
import com.dreamfish.fishblog.core.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;

@Controller
@RequestMapping(ConstConfig.API_PUBLIC)
public class MessagesController {

    @Autowired
    private MessagesService messagesService = null;

    //获取用户消息分页
    @RequestMapping(value = "/user/{userId}/messages/{pageIndex}/{pageSize}", name = "获取用户消息分页", method = RequestMethod.GET)
    @ResponseBody
    public Result getMessagesWithPager(
            @PathVariable("userId") Integer userId,
            @PathVariable("pageIndex")
            @Min(value = 0, message = "页数必须大于等于0")
                    Integer pageIndex,
            @PathVariable("pageSize")
            @Min(value = 1, message = "页大小必须大于等于1")
                    Integer pageSize)
    {
        return messagesService.getMessagesWithPager(userId, pageIndex, pageSize);
    }

    @RequestMapping(value = "/user/{userId}/messages/notread", name = "获取标记用户消息全部已读", method = RequestMethod.GET)
    @ResponseBody
    public Result getMessagesNotRead(@PathVariable("userId") Integer userId, @RequestParam("maxCount") Integer maxCount){
        return messagesService.getMessagesNotRead(userId, maxCount);
    }


    //获取标记用户消息已读
    @RequestMapping(value = "/user/{userId}/messages/read", name = "获取标记用户消息已读", method = RequestMethod.POST)
    @ResponseBody
    public Result setReadMessages(@PathVariable("userId") Integer userId, @RequestBody JSONObject messageIds){
        return messagesService.setReadMessages(userId, messageIds);
    }
    //获取标记用户消息全部已读
    @RequestMapping(value = "/user/{userId}/messages/readall", name = "获取标记用户消息全部已读", method = RequestMethod.GET)
    @ResponseBody
    public Result setReadAllMessages(@PathVariable("userId") Integer userId){
        return messagesService.setReadAllMessages(userId);
    }
    //删除用户消息
    @RequestMapping(value = "/user/{userId}/messages", name = "删除用户消息", method = RequestMethod.DELETE)
    @ResponseBody
    public Result deleteMessages(@PathVariable("userId") Integer userId, @RequestBody JSONObject messageIds){
        return messagesService.deleteMessages(userId, messageIds);
    }
    //删除所有用户消息
    @RequestMapping(value = "/user/{userId}/messages/all", name = "删除所有用户消息", method = RequestMethod.DELETE)
    @ResponseBody
    public Result deleteAllMessages(@PathVariable("userId") Integer userId){
        return messagesService.deleteAllMessages(userId);
    }
}
