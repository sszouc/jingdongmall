package com.jingdong.mall.service.impl;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.common.utils.JwtUtil;
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