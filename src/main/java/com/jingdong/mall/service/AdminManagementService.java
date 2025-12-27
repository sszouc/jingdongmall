package com.jingdong.mall.service;

import com.jingdong.mall.model.dto.request.AdminCreateRequest;
import com.jingdong.mall.model.dto.response.AdminCreateResponse;
import com.jingdong.mall.model.dto.response.AdminListResponse;

/**
 * 管理员管理服务接口
 */
public interface AdminManagementService {

    /**
     * 创建管理员
     * @param currentUserId 当前操作用户ID
     * @param currentUserRole 当前操作用户角色
     * @param request 创建管理员请求
     * @return 创建的管理员信息
     */
    AdminCreateResponse createAdmin(Long currentUserId, Integer currentUserRole, AdminCreateRequest request);

    /**
     * 删除管理员
     * @param currentUserId 当前操作用户ID
     * @param currentUserRole 当前操作用户角色
     * @param targetAdminId 要删除的管理员ID
     */
    void deleteAdmin(Long currentUserId, Integer currentUserRole, Long targetAdminId);

    /**
     * 获取管理员列表
     * @param currentUserId 当前操作用户ID
     * @param currentUserRole 当前操作用户角色
     * @return 管理员列表响应
     */
    AdminListResponse getAdminList(Long currentUserId, Integer currentUserRole);
}