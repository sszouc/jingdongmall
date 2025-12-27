package com.jingdong.mall.service;

import com.jingdong.mall.model.dto.request.AdminLoginRequest;
import com.jingdong.mall.model.dto.request.AdminResetPasswordRequest;
import com.jingdong.mall.model.dto.response.AdminLoginResponse;

/**
 * 管理员认证服务接口
 */
public interface AdminAuthService {

    /**
     * 管理员登录
     * @param request 登录请求
     * @return 登录响应
     */
    AdminLoginResponse login(AdminLoginRequest request);

    /**
     * 管理员找回密码
     * @param request 找回密码请求
     */
    void resetPassword(AdminResetPasswordRequest request);
}