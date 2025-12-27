package com.jingdong.mall.controller.api;

import com.jingdong.mall.common.response.Result;
import com.jingdong.mall.model.dto.request.AdminLoginRequest;
import com.jingdong.mall.model.dto.request.AdminResetPasswordRequest;
import com.jingdong.mall.model.dto.response.AdminLoginResponse;
import com.jingdong.mall.service.AdminAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin/auth")
@Tag(name = "管理员/管理员认证", description = "管理员认证相关接口")
public class AdminAuthController {

    @Autowired
    private AdminAuthService adminAuthService;

    @Operation(
            summary = "管理员登录",
            description = "管理员登录接口，支持手机号或邮箱登录"
    )
    @PostMapping("/login")
    public Result<AdminLoginResponse> login(@Valid @RequestBody AdminLoginRequest request) {
        AdminLoginResponse response = adminAuthService.login(request);
        return Result.success("登录成功", response);
    }

    @Operation(
            summary = "管理员找回密码",
            description = "管理员通过手机号和验证码找回密码"
    )
    @PostMapping("/reset-password")
    public Result<String> resetPassword(@Valid @RequestBody AdminResetPasswordRequest request) {
        adminAuthService.resetPassword(request);
        return Result.success("密码重置成功", null);
    }
}