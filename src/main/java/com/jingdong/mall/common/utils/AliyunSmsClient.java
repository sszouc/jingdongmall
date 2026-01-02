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

        com.aliyun.credentials.Client credentialClient = new com.aliyun.credentials.Client();

        com.aliyun.credentials.models.CredentialModel credential  = credentialClient.getCredential();
        String accessKeyId = credential.getAccessKeyId();
        String accessKeySecret = credential.getAccessKeySecret();
        String securityToken = credential.getSecurityToken();

        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setCredential(credentialClient)
                .setEndpoint(smsConfig.getEndpoint())
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret)
                .setSecurityToken(securityToken);

        this.client = new Client(config);

        log.info("阿里云客户端初始化完毕\naccessKeyId:{}, accessKeySecret:{}", accessKeyId, accessKeySecret);
    }

    /**
     * 发送短信验证码
     */
    public SendSmsVerifyCodeResponse sendSmsVerifyCode(String phoneNumber, String code) throws Exception {

        String templateParam = String.format("{\"code\":\"%s\",\"min\":\"5\"}", code);

        SendSmsVerifyCodeRequest request = new SendSmsVerifyCodeRequest()
                .setSignName(smsConfig.getSignName())
                .setTemplateCode(smsConfig.getTemplateCode())
                .setPhoneNumber(phoneNumber)
                .setTemplateParam(templateParam); // JSON格式的参数

        RuntimeOptions runtime = new RuntimeOptions();


            SendSmsVerifyCodeResponse response = client.sendSmsVerifyCodeWithOptions(request, runtime);
            System.out.println(new com.google.gson.Gson().toJson(response));
            log.info("短信发送成功: phone={}, requestId={}", phoneNumber, response.getBody().getRequestId());
            return response;
    }

}