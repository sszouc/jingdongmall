package com.jingdong.mall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JingdongmallApplication {

    public static void main(String[] args) {
        SpringApplication.run(JingdongmallApplication.class, args);
    }

}
