package com.dreamfish.fishblog.core.utils.request;

import com.dreamfish.fishblog.core.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class IpUtil {
    /**
     * 获取用户IP地址
     * @param request 请求
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = null;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()
                // = 15
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
            //修复获取 0:0:0:0:0:0:0:1 的错误, 说明服务端和客户端在一台机器
            if (!StringUtils.isEmpty(ipAddress) && "0:0:0:0:0:0:0:1".equals(ipAddress)) { // "***.***.***.***".length()
                ipAddress = "127.0.0.1";
            }
        } catch (Exception e) {
            ipAddress = "";
        }
        return ipAddress;
    }
}
