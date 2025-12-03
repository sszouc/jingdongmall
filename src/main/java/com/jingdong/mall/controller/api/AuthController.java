// controller/api/AuthController.java
package com.jingdong.mall.controller.api;

import com.jingdong.mall.common.response.Result;
import com.jingdong.mall.common.utils.JwtUtil;
import com.jingdong.mall.model.dto.request.DeleteAccountRequest;
import com.jingdong.mall.model.dto.request.ResetPasswordRequest;
import com.jingdong.mall.model.dto.request.UserRegisterRequest;
import com.jingdong.mall.model.dto.response.UserRegisterResponse;
import com.jingdong.mall.model.dto.request.UserLoginRequest;
import com.jingdong.mall.model.dto.response.UserLoginResponse;
import com.jingdong.mall.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;

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

    @DeleteMapping("/delete-account")
    public Result<String> deleteAccount(HttpServletRequest request,
                                        @RequestBody @Valid DeleteAccountRequest deleteRequest) {
        // 1. 从请求头获取JWT Token
        String token = request.getHeader("Authorization");
        if (!StringUtils.hasText(token) || !token.startsWith("Bearer ")) {
            return Result.unauthorized("Token不存在或格式错误");
        }
        token = token.substring(7); // 去除Bearer前缀

        // 2. 解析Token获取当前登录用户ID
        JwtUtil jwtUtil = new JwtUtil();
        String userId = jwtUtil.getUserIdFromToken(token);

        // 3. 调用Service执行注销逻辑
        authService.deleteAccount(userId, deleteRequest);

        // 4. 返回成功响应
        return Result.success("账号注销成功");
    }
}

