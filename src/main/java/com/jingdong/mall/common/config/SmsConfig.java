//// config/SmsConfig.java
//package com.jingdong.mall.common.config;
//
//import lombok.Data;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.stereotype.Component;
//
//@Data
//@Component
//@ConfigurationProperties(prefix = "aliyun.sms")
//public class SmsConfig {
//    private String accessKeyId;
//    private String accessKeySecret;
//    private String endpoint = "dypnsapi.aliyuncs.com";
//    private String signName; // 短信签名
//    private String templateCode; // 短信模板
//    private Integer expireMinutes = 5; // 验证码过期时间（分钟）
//    private Integer maxSendPerDay = 10; // 每天最大发送次数
//}