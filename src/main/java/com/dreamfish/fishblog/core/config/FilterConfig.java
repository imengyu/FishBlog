package com.dreamfish.fishblog.core.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 拦截器配置
 */
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean modifyParametersFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new FixHeaderFilter());
        registration.addUrlPatterns("/*");              // 拦截路径
        registration.setName("fixHeaderFilter");        // 拦截器名称
        registration.setOrder(1);                       // 顺序
        return registration;
    }

    /**
     * 自定义拦截器
     */
    class FixHeaderFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            response.setHeader("X-Powered-By","FishBlog/1.1.3");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS");
            //跨域设置
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
            response.setHeader("Access-Control-Allow-Headers","content-type");
            filterChain.doFilter(request, response);
        }
    }
}

