// common/config/SecurityConfig.java

/*
*   这是鉴权组件，绝大多数api都需要配置，这里不用修改，代码写完后我统一修改就行
* */


package com.jingdong.mall.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    //这个地方注意，目前的生产环境先把认证关掉，后面再打开
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .anyRequest().permitAll()  // 允许所有请求，无需认证
                ).csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configure(http)); // 启用 CORS
        return http.build();
    }
}