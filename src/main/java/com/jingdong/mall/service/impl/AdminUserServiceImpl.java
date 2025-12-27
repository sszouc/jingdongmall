package com.jingdong.mall.service.impl;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.common.utils.JwtUtil;
import com.jingdong.mall.mapper.UserMapper;
import com.jingdong.mall.model.dto.response.AdminInfoResponse;
import com.jingdong.mall.model.entity.User;
import com.jingdong.mall.service.AdminUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

@Slf4j
@Service
public class AdminUserServiceImpl implements AdminUserService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void adminSignout(String token, Long userId, Integer userRole) {
        // 1. 验证用户角色必须是管理员
        validateAdminRole(userRole, userId);

        try {
            // 2. 将token加入黑名单
            jwtUtil.blackListToken(token, com.jingdong.mall.model.entity.TokenBlacklist.Reason.LOGOUT);

            // 3. 记录日志
            log.info("管理员登出成功，管理员ID：{}，角色：{}", userId, userRole);

        } catch (NoSuchAlgorithmException e) {
            log.error("Token哈希计算失败，管理员ID：{}", userId, e);
            throw new BusinessException(ErrorCode.TOKEN_ADD_BLACKLIST_FAILED);
        } catch (Exception e) {
            log.error("管理员登出失败，管理员ID：{}", userId, e);
            throw new BusinessException(ErrorCode.LOGOUT_FAILED);
        }
    }

    @Override
    public AdminInfoResponse getAdminInfo(Long userId, Integer userRole) {
        // 1. 验证用户角色必须是管理员
        validateAdminRole(userRole, userId);

        try {
            // 2. 查询用户信息
            User user = userMapper.selectById(userId);
            if (user == null) {
                log.warn("管理员用户不存在，用户ID：{}", userId);
                throw new BusinessException(ErrorCode.USER_NOT_EXIST);
            }

            // 3. 再次验证用户角色
            if (user.getRole() == null || (user.getRole() != 1 && user.getRole() != 2)) {
                log.warn("用户不是管理员，用户ID：{}，角色：{}", userId, user.getRole());
                throw new BusinessException(ErrorCode.ADMIN_NOT_PERMISSION);
            }

            // 4. 验证用户状态
            if (user.getStatus() != null && user.getStatus() == 0) {
                log.warn("管理员用户已被禁用，用户ID：{}", userId);
                throw new BusinessException(ErrorCode.USER_DISABLED);
            }

            // 5. 转换为AdminInfoResponse
            AdminInfoResponse response = AdminInfoResponse.fromEntity(user);

            // 6. 记录日志（可选，生产环境可改为debug级别）
            log.debug("获取管理员信息成功，管理员ID：{}，用户名：{}", userId, user.getUsername());

            return response;

        } catch (BusinessException e) {
            // 业务异常直接抛出
            throw e;
        } catch (Exception e) {
            log.error("获取管理员信息发生系统异常，管理员ID：{}", userId, e);
            throw new BusinessException(ErrorCode.ADMIN_OPERATION_FAILED, "获取管理员信息失败");
        }
    }

    /**
     * 验证用户角色必须是管理员
     */
    private void validateAdminRole(Integer userRole, Long userId) {
        if (userRole == null || (userRole != 1 && userRole != 2)) {
            log.warn("非管理员尝试使用管理员登出接口，用户ID：{}，角色：{}", userId, userRole);
            throw new BusinessException(ErrorCode.ADMIN_NOT_PERMISSION);
        }
    }
}