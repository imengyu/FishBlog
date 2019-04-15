package com.dreamfish.fishblog.core.utils.request;

import org.springframework.cache.annotation.Cacheable;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

/**
 * 工具类 - 请求
 */
public class RequestUtils {

    /**
     * 字符串 URL 编码
     * @param str
     * @return
     */
    public static String encoderURLString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 字符串 URL 解码
     * @param str
     * @return
     */
    public static String decoderURLString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 检查请求UA是否是搜索引擎
     * @param request
     * @return
     */
    @Cacheable("blog-bot-cache")
    public static boolean checkIsSearchEngines(HttpServletRequest request) {
        String ua = request.getHeader("user-agent");
        return (ua.contains("Baiduspider") || ua.contains("Googlebot")
                || ua.contains("+http://www.baidu.com/search/spider.html")
                || ua.contains("+http://www.google.com/bot.html")
                || ua.contains("360Spider") || ua.contains("bingbot")
                || ua.contains("Sogou web spider") || ua.contains("Yidospider"));
    }
}
