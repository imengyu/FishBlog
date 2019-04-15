package com.dreamfish.fishblog.core.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Redis缓存配置类
 * @author szekinwin
 *
 */
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

    //缓存管理器
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();  // 生成一个默认配置，通过config对象即可对缓存进行自定义配置
        config = config.entryTtl(Duration.ofMinutes(1))     // 设置缓存的默认过期时间，也是使用Duration设置
                .disableCachingNullValues();     // 不缓存空值

        // 设置一个初始化的缓存空间set集合
        Set<String> cacheNames =  new HashSet<>();
        cacheNames.add("blog-cache");//总cache，杂
        cacheNames.add("blog-simple-reader-cache");//文章简单查看页缓存
        cacheNames.add("blog-single-reader-cache");//单个文章读取缓存
        cacheNames.add("blog-comment-pages-cache");//文章评论查看页缓存
        cacheNames.add("blog-posts-pages-cache");//文章查看页缓存
        cacheNames.add("blog-classes-cache");//文章分类缓存
        cacheNames.add("blog-tags-cache");//文章标签缓存
        cacheNames.add("blog-dates-cache");//文章归档缓存
        cacheNames.add("blog-images-cache");//图片缓存
        cacheNames.add("blog-user-cache");//用户信息缓存
        cacheNames.add("blog-bot-cache");//针对搜索引擎页面缓存

        // 对每个缓存空间应用不同的配置
        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
        configMap.put("blog-cache", config);
        configMap.put("blog-posts-pages-cache", config.entryTtl(Duration.ofDays(2)));
        configMap.put("blog-comment-pages-cache", config.entryTtl(Duration.ofDays(2)));
        configMap.put("blog-simple-reader-cache", config.entryTtl(Duration.ofDays(1)));
        configMap.put("blog-single-reader-cache", config.entryTtl(Duration.ofDays(1)));
        configMap.put("blog-classes-cache", config.entryTtl(Duration.ofDays(10)));
        configMap.put("blog-tags-cache", config.entryTtl(Duration.ofDays(10)));
        configMap.put("blog-dates-cache", config.entryTtl(Duration.ofDays(10)));
        configMap.put("blog-images-cache", config.entryTtl(Duration.ofDays(15)));
        configMap.put("blog-user-cache", config.entryTtl(Duration.ofDays(10)));
        configMap.put("blog-bot-cache", config.entryTtl(Duration.ofDays(10)));

        RedisCacheManager cacheManager = RedisCacheManager.builder(connectionFactory)     // 使用自定义的缓存配置初始化一个cacheManager
                .initialCacheNames(cacheNames)  // 注意这两句的调用顺序，一定要先调用该方法设置初始化的缓存名，再初始化相关的配置
                .withInitialCacheConfigurations(configMap)
                .build();
        return cacheManager;
    }
}