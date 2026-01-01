// utils/AliyunSmsClient.java
package com.jingdong.mall.common.utils;

import com.aliyun.dypnsapi20170525.Client;
import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeRequest;
import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeResponse;
import com.aliyun.teautil.models.RuntimeOptions;
import com.jingdong.mall.common.config.SmsConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import jakarta.annotation.PostConstruct;

@Slf4j
@Component
public class AliyunSmsClient {

    @Autowired
    private SmsConfig smsConfig;

    /**
     * -- GETTER --
     *  获取客户端实例
     */
    @Getter
    private Client client;

    @PostConstruct
    public void init() throws Exception {
        // 使用更安全的凭据管理方式
        com.aliyun.credentials.Client credential = new com.aliyun.credentials.Client();

        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setCredential(credential)
                .setEndpoint(smsConfig.getEndpoint());

        this.client = new Client(config);
    }

    /**
     * 发送短信验证码
     */
    public SendSmsVerifyCodeResponse sendSmsVerifyCode(String phoneNumber, String code) throws Exception {
        SendSmsVerifyCodeRequest request = new SendSmsVerifyCodeRequest()
                .setPhoneNumber(phoneNumber)
                .setSignName(smsConfig.getSignName())
                .setTemplateCode(smsConfig.getTemplateCode())
                .setTemplateParam("{\"code\":\"" + code + "\"}"); // JSON格式的参数

        RuntimeOptions runtime = new RuntimeOptions();

        try {
            SendSmsVerifyCodeResponse response = client.sendSmsVerifyCodeWithOptions(request, runtime);
            log.info("短信发送成功: phone={}, requestId={}", phoneNumber, response.getBody().getRequestId());
            return response;
        } catch (Exception e) {
            log.error("短信发送失败: phone={}, error={}", phoneNumber, e.getMessage());
            throw new RuntimeException("短信发送失败: " + e.getMessage());
        }
    }

}