// src/main/java/com/jingdong/mall/config/WebConfig.java
package com.jingdong.mall.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:/root/uploads/avatar}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将 /uploads/avatar/** 映射到Linux文件系统的实际路径
        String resourcePath = "file:" + uploadDir + "/";

        registry.addResourceHandler("/root/uploads/avatar/**")
                .addResourceLocations(resourcePath)
                .setCachePeriod(3600); // 缓存1小时

    }
}