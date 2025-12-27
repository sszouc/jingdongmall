package com.jingdong.mall.service;

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
}