package com.dreamfish.fishblog.core.service.impl;

import com.alibaba.fastjson.JSON;

import com.dreamfish.fishblog.core.service.RedisService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author yadong.zhang (yadong.zhang0415(a)gmail.com)
 * @version 1.0
 * @website https://www.zhyd.me
 * @date 2018/4/16 16:26
 * @since 1.0
 */
@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisTemplate redisTemplate = null;

    @Override
    public <T> void set(String key, T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public <T> void set(String key, T value, long expire, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, expire, timeUnit);
    }

    @Override
    public <T> T get(String key) {
        return (T) redisTemplate.opsForValue().get(key);
    }

    @Override
    public boolean expire(String key, long expire) {
        Boolean b = redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        return b!=null && b;
    }
    @Override
    public boolean expire(String key, long expire, TimeUnit timeUnit) {
        Boolean b = redisTemplate.expire(key, expire, timeUnit);
        return b!=null && b;
    }

    @Override
    public void del(String key) {
        redisTemplate.opsForValue().getOperations().delete(key);
    }

    @Override
    public void delBatch(Set<String> keys) {
        redisTemplate.delete(keys);
    }

    @Override
    public void delBatch(String keyPrefix) {
        Set<String> keys = this.keySet(keyPrefix + "*");
        if (!CollectionUtils.isEmpty(keys)) {
            delBatch(keys);
        }
    }

    @Override
    public <T> void setList(String key, List<T> list) {
        String value = JSON.toJSONString(list);
        set(key, value);
    }

    @Override
    public <T> void setList(String key, List<T> list, long expire, TimeUnit timeUnit) {
        String value = JSON.toJSONString(list);
        set(key, value, expire, timeUnit);
    }

    @Override
    public <T> List<T> getList(String key, Class<T> clz) {
        String json = get(key);
        if (json != null) {
            return JSON.parseArray(json, clz);
        }
        return null;
    }

    @Override
    public boolean hasKey(String key) {
        Boolean b = redisTemplate.hasKey(key);
        return b!=null && b;
    }

    @Override
    public long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }

    @Override
    public Set<String> keySet(String keyPrefix) {
        return redisTemplate.keys(keyPrefix + "*");
    }

    @Override
    public boolean persist(String key){
        return redisTemplate.persist(key);
    }
}