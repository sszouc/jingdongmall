// service/SmsService.java
package com.jingdong.mall.service;

import com.jingdong.mall.model.dto.request.SmsCodeRequest;
import com.jingdong.mall.model.dto.request.SmsVerifyRequest;

public interface SmsService {

    /**
     * 发送验证码
     */
    void sendVerificationCode(SmsCodeRequest request);

    /**
     * 验证验证码
     */
    boolean verifyCode(String phone, String code, String type);
}