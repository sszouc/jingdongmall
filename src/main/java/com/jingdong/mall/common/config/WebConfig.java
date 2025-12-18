// src/main/java/com/jingdong/mall/config/WebConfig.java
package com.jingdong.mall.common.config;


import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class WebConfig {

    @Value("${file.upload-dir:/app/uploads/avatar}")
    private String uploadDir;

    @Value("${file.base-url:http://localhost:8080/uploads/avatar}")
    private String baseUrl;

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addResourceHandlers(@NotNull ResourceHandlerRegistry registry) {
                // Docker环境静态资源映射
                String resourcePath = "file:" + uploadDir + "/";

                registry.addResourceHandler("/uploads/avatar/**")
                        .addResourceLocations(resourcePath)
                        .setCachePeriod(3600)
                        .resourceChain(true)
                        .addResolver(new PathResourceResolver());

                log.info("Docker静态资源映射: /uploads/avatar/** -> {}", resourcePath);
            }

            @Override
            public void addCorsMappings(@NotNull CorsRegistry registry) {
                // Docker环境CORS配置
                registry.addMapping("/uploads/avatar/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "HEAD")
                        .maxAge(3600);
            }
        };
    }
}