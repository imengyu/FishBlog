package com.dreamfish.fishblog.core.config;


import com.dreamfish.fishblog.core.interceptor.RequestAuthInterceptor;
import org.omg.PortableInterceptor.Interceptor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.MultipartConfigElement;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebAppConfig extends WebMvcConfigurerAdapter {

    //获取配置文件中图片的路径
    @Value("${fishblog.images-save-path}")
    private String mImagesPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (mImagesPath.equals("") || mImagesPath.equals("${fishblog.images-save-path}")) {
            String imagesPath = WebAppConfig.class.getClassLoader().getResource("").getPath();
            if (imagesPath.indexOf(".jar") > 0) {
                imagesPath = imagesPath.substring(0, imagesPath.indexOf(".jar"));
            } else if (imagesPath.indexOf("classes") > 0) {
                imagesPath = "file:" + imagesPath.substring(0, imagesPath.indexOf("classes"));
            }
            imagesPath = imagesPath.substring(0, imagesPath.lastIndexOf("/")) + "/images/";
            mImagesPath = imagesPath;
        }
        LoggerFactory.getLogger(WebAppConfig.class).info("imagesPath=" + mImagesPath);
        registry.addResourceHandler("/static-images/**")
                .addResourceLocations(mImagesPath).setCachePeriod(2592000).resourceChain(true);
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .addResourceLocations("classpath:/templates/")
                .setCacheControl(CacheControl.maxAge(15, TimeUnit.DAYS).cachePublic());
        super.addResourceHandlers(registry);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RequestAuthInterceptor()).addPathPatterns("/**");
        super.addInterceptors(registry);
    }
}
