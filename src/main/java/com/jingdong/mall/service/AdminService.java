package com.jingdong.mall.service;

/**
 * 管理员服务接口
 */
public interface AdminService {

    /**
     * 删除用户
     * @param currentUserId 当前管理员用户ID
     * @param currentUserRole 当前管理员角色（整型）
     * @param targetUserId 要删除的目标用户ID
     */
    void deleteUser(Long currentUserId, Integer currentUserRole, Long targetUserId);
}