package com.jingdong.mall.service;

import com.jingdong.mall.model.dto.request.AdminChangePasswordRequest;
import com.jingdong.mall.model.dto.request.AdminUpdateRequest;
import com.jingdong.mall.model.dto.response.AdminInfoResponse;

/**
 * 管理员用户服务接口
 */
public interface AdminUserService {

    /**
     * 管理员登出
     * @param token 当前token
     * @param userId 管理员ID
     * @param userRole 管理员角色
     */
    void adminSignout(String token, Long userId, Integer userRole);

    /**
     * 获取管理员信息
     * @param userId 管理员ID
     * @param userRole 管理员角色
     * @return 管理员信息
     */
    AdminInfoResponse getAdminInfo(Long userId, Integer userRole);

    /**
     * 更新管理员信息
     * @param userId 管理员ID
     * @param userRole 管理员角色
     * @param request 更新请求
     */
    void updateAdminInfo(Long userId, Integer userRole, AdminUpdateRequest request);

    /**
     * 修改管理员密码
     * @param token 当前token
     * @param userId 管理员ID
     * @param userRole 管理员角色
     * @param request 修改密码请求
     */
    void changeAdminPassword(String token, Long userId, Integer userRole, AdminChangePasswordRequest request);
}