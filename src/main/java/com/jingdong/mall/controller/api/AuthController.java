// controller/api/AuthController.java
package com.jingdong.mall.controller.api;

import com.jingdong.mall.common.response.Result;
import com.jingdong.mall.model.dto.request.ResetPasswordRequest;
import com.jingdong.mall.model.dto.request.UserRegisterRequest;
import com.jingdong.mall.model.dto.response.UserRegisterResponse;
import com.jingdong.mall.model.dto.request.UserLoginRequest;
import com.jingdong.mall.model.dto.response.UserLoginResponse;
import com.jingdong.mall.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;


/*
 * controller，没什么好介绍的，在这里定义url路径，注意代码的复用
 * */
@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public Result<UserRegisterResponse> register(@RequestBody @Valid UserRegisterRequest request) {
        UserRegisterResponse response = authService.register(request);
        return Result.success(response);
    }

    @PostMapping("/login")
    public Result<UserLoginResponse> login(@RequestBody @Valid UserLoginRequest request) {
        UserLoginResponse response = authService.login(request);
        return Result.success(response);
    }

    @PostMapping("/reset-password")
    public Result<String> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        authService.resetPassword(request);
        return Result.success();
    }
}

