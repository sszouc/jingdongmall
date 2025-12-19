package com.jingdong.mall.service;

import com.jingdong.mall.model.dto.request.AdminUserListRequest;
import com.jingdong.mall.model.dto.response.AdminUserListResponse;

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

    /**
     * 分页查询用户列表
     * @param currentUserId 当前管理员用户ID
     * @param currentUserRole 当前管理员角色
     * @param request 查询条件
     * @return 用户列表响应
     */
    AdminUserListResponse getUserList(Long currentUserId, Integer currentUserRole, AdminUserListRequest request);
}