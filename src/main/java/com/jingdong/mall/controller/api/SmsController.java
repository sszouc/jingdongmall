//// controller/api/SmsController.java
//package com.jingdong.mall.controller.api;
//
//import com.jingdong.mall.common.response.Result;
//import com.jingdong.mall.model.dto.request.SmsCodeRequest;
//import com.jingdong.mall.model.dto.request.SmsVerifyRequest;
//import com.jingdong.mall.service.SmsService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Slf4j
//@Validated
//@RestController
//@RequestMapping("/api/sms")
//@Tag(name = "短信服务", description = "短信验证码相关接口")
//public class SmsController {
//
//    @Autowired
//    private SmsService smsService;
//
//    @Operation(summary = "发送验证码", description = "发送短信验证码到指定手机号")
//    @PostMapping("/send-code")
//    public Result<String> sendVerificationCode(
//            @RequestBody @Valid SmsCodeRequest request) {
//
//        smsService.sendVerificationCode(request);
//        return Result.success("验证码发送成功");
//    }
//
//    @Operation(summary = "验证验证码", description = "验证短信验证码是否正确")
//    @PostMapping("/verify-code")
//    public Result<Map<String, Boolean>> verifyCode(
//            @RequestBody @Valid SmsVerifyRequest request) {
//
//        boolean isValid = smsService.verifyCode(request);
//
//        Map<String, Boolean> result = new HashMap<>();
//        result.put("valid", isValid);
//
//        return Result.success("验证码正确", result);
//    }
//}