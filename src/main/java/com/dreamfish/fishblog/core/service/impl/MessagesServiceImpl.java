package com.dreamfish.fishblog.core.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dreamfish.fishblog.core.entity.MessageItem;
import com.dreamfish.fishblog.core.exception.InvalidArgumentException;
import com.dreamfish.fishblog.core.mapper.MessageMapper;
import com.dreamfish.fishblog.core.mapper.UserMapper;
import com.dreamfish.fishblog.core.repository.MessageRepository;
import com.dreamfish.fishblog.core.service.MessagesService;
import com.dreamfish.fishblog.core.utils.Result;
import com.dreamfish.fishblog.core.utils.ResultCodeEnum;
import com.dreamfish.fishblog.core.utils.StringUtils;
import com.dreamfish.fishblog.core.utils.auth.PublicAuth;
import com.dreamfish.fishblog.core.utils.encryption.Base64Utils;
import com.dreamfish.fishblog.core.utils.request.ContextHolderUtils;
import com.dreamfish.fishblog.core.utils.response.AuthCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MessagesServiceImpl implements MessagesService {

    @Autowired
    private MessageRepository messageRepository = null;
    @Autowired
    private MessageMapper messageMapper = null;
    @Autowired
    private UserMapper userMapper = null;


    @Override
    public Result getMessagesWithPager(Integer userId, Integer pageIndex, Integer pageSize) {
        return Result.success(messageRepository.findAllByUserIdOrderByDateDesc(userId, PageRequest.of(pageIndex, pageSize)));
    }

    @Override
    public Result getMessagesNotRead(Integer userId, Integer maxCount) {
        return Result.success(messageMapper.findAllByUserIdAndReadOrderByDateDescLimit(userId, false, maxCount));
    }

    @Override
    public Result setReadMessages(Integer userId, JSONObject messageIds) {
        //验证权限
        if(!messagesAuthTest(userId)) return Result.failure(ResultCodeEnum.UNAUTHORIZED);

        List<Integer> ids;
        try{
            ids = messagesJsonToIdArray(messageIds);
        }catch (InvalidArgumentException e){
            return Result.failure(ResultCodeEnum.BAD_REQUEST.getCode(), e.getMessage());
        }

        messageMapper.updateReadByIdIn(true, StringUtils.intListToInStr(ids));
        userMapper.updateUserMessageCount(userId, messageMapper.getNotReadMessageCountByUserId(userId));
        return Result.success();
    }

    @Override
    public Result setReadAllMessages(Integer userId) {
        //验证权限
        if(!messagesAuthTest(userId)) return Result.failure(ResultCodeEnum.UNAUTHORIZED);

        messageMapper.updateReadByUserId(true, userId);
        userMapper.updateUserMessageCount(userId, 0);
        return Result.success();
    }

    @Override
    public Result deleteMessages(Integer userId, JSONObject messageIds) {
        //验证权限
        if(!messagesAuthTest(userId)) return Result.failure(ResultCodeEnum.UNAUTHORIZED);

        List<Integer> ids = new ArrayList<>();
        try{
            ids = messagesJsonToIdArray(messageIds);
        }catch (InvalidArgumentException e){
            return Result.failure(ResultCodeEnum.BAD_REQUEST.getCode(), e.getMessage());
        }
        messageRepository.deleteByIdIn(ids);
        userMapper.updateUserMessageCount(userId, messageMapper.getNotReadMessageCountByUserId(userId));
        return Result.success();
    }

    @Override
    public Result deleteAllMessages(Integer userId) {
        //验证权限
        if(!messagesAuthTest(userId)) return Result.failure(ResultCodeEnum.UNAUTHORIZED);

        messageRepository.deleteByUserId(userId);
        userMapper.updateUserMessageCount(userId, 0);
        return Result.success();
    }

    @Override
    public Result sendMessage(Integer userId, Integer fromUser, String title, String content) {
        MessageItem msg = new MessageItem();

        msg.setUserId(userId);
        msg.setFromUserId(fromUser);
        msg.setTitle(title);
        msg.setHaveRead(false);
        msg.setContent(Base64Utils.encode(content));
        msg.setDate(new Date());

        //增加消息数
        userMapper.updateUserMessageCountIncrease(userId,1);

        msg = messageRepository.saveAndFlush(msg);
        return Result.success(msg);
    }


    private boolean messagesAuthTest(Integer userId){
        Integer authUserId = PublicAuth.authGetUseId(ContextHolderUtils.getRequest());
        return !(authUserId < AuthCode.SUCCESS || userId != authUserId);
    }
    private List<Integer> messagesJsonToIdArray(JSONObject messageIds) throws InvalidArgumentException {
        JSONArray idStrs = messageIds.getJSONArray("messages");
        if(idStrs == null || idStrs.size() == 0)
            throw new InvalidArgumentException("空数组");
        List<Integer> ids = new ArrayList<>();
        for(int i =0, size = idStrs.size(); i<size; i++){
            if(idStrs.getString(i) != null && StringUtils.isInteger(idStrs.getString(i))) ids.add(Integer.parseInt(idStrs.getString(i)));
            else throw new InvalidArgumentException("参数 messages[" + i + "] 类型有误");
        }
        return ids;
    }
}
