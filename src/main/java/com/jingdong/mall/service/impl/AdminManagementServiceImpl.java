package com.jingdong.mall.service.impl;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.common.utils.JwtUtil;
import com.jingdong.mall.mapper.UserMapper;
import com.jingdong.mall.model.dto.request.AdminCreateRequest;
import com.jingdong.mall.model.dto.response.AdminCreateResponse;
import com.jingdong.mall.model.entity.User;
import com.jingdong.mall.service.AdminManagementService;
import com.jingdong.mall.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class AdminManagementServiceImpl implements AdminManagementService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    // 数字字符集
    private static final String NUMBERS = "0123456789";
    // 字母数字字符集
    private static final String ALPHANUMERIC = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdminCreateResponse createAdmin(Long currentUserId, Integer currentUserRole, AdminCreateRequest request) {
        // 1. 权限验证：只有超级管理员(role=2)可以创建管理员
        validatePermission(currentUserRole);

        // 2. 验证手机号是否已存在
        validatePhoneExists(request.getPhone());

        try {
            // 3. 生成用户名（手机号后6位，如果重复则追加随机数）
            String username = generateUsername(request.getPhone());

            // 4. 创建用户实体
            User newAdmin = buildAdminUser(request, username);

            // 5. 保存到数据库
            int result = userMapper.insert(newAdmin);
            if (result <= 0) {
                log.error("创建管理员失败，手机号：{}", request.getPhone());
                throw new BusinessException(ErrorCode.ADMIN_OPERATION_FAILED, "创建管理员失败");
            }

            // 6. 构建响应
            return buildAdminCreateResponse(newAdmin);

        } catch (BusinessException e) {
            // 业务异常直接抛出
            throw e;
        } catch (Exception e) {
            log.error("创建管理员发生系统异常，手机号：{}", request.getPhone(), e);
            throw new BusinessException(ErrorCode.ADMIN_OPERATION_FAILED, "系统异常，创建管理员失败");
        }
    }

    /**
     * 验证权限：只有超级管理员可以创建管理员
     */
    private void validatePermission(Integer currentUserRole) {
        if (currentUserRole == null || currentUserRole != 2) {
            log.warn("非超级管理员尝试创建管理员，当前角色：{}", currentUserRole);
            throw new BusinessException(ErrorCode.ADMIN_NOT_PERMISSION);
        }
    }

    /**
     * 验证手机号是否已存在
     */
    private void validatePhoneExists(String phone) {
        // 检查普通用户是否已存在
        int userCount = userMapper.countByPhone(phone);
        if (userCount > 0) {
            // 检查是否已经是管理员
            int adminCount = userMapper.checkAdminExists(phone);
            if (adminCount > 0) {
                log.warn("手机号已经是管理员，手机号：{}", phone);
                throw new BusinessException(ErrorCode.ADMIN_CANNOT_DELETE_ADMIN, "该用户已经是管理员");
            }
        }
    }

    /**
     * 生成用户名：手机号后6位，如果重复则追加随机数
     */
    private String generateUsername(String phone) {
        String baseUsername;
        if (phone.length() >= 6) {
            baseUsername = phone.substring(phone.length() - 6);
        } else {
            baseUsername = phone;
        }

        String username = baseUsername;
        int attempt = 0;
        final int MAX_ATTEMPTS = 5;

        while (attempt < MAX_ATTEMPTS) {
            int count = userMapper.countByUsername(username);
            if (count == 0) {
                return username;
            }

            // 用户名重复，追加4位随机数字
            String randomDigits = generateRandomDigits(4);
            username = baseUsername + randomDigits;
            attempt++;
        }

        // 如果多次尝试仍然冲突，使用8位随机字母数字
        log.warn("用户名生成多次冲突，使用随机字符串生成，手机号：{}", phone);
        return "admin_" + generateRandomAlphanumeric(8);
    }

    /**
     * 生成指定长度的随机数字字符串
     * @param length 字符串长度
     * @return 随机数字字符串
     */
    private String generateRandomDigits(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * NUMBERS.length());
            sb.append(NUMBERS.charAt(index));
        }
        return sb.toString();
    }

    /**
     * 生成指定长度的随机字母数字字符串
     * @param length 字符串长度
     * @return 随机字母数字字符串
     */
    private String generateRandomAlphanumeric(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * ALPHANUMERIC.length());
            sb.append(ALPHANUMERIC.charAt(index));
        }
        return sb.toString();
    }

    /**
     * 构建管理员用户实体
     */
    private User buildAdminUser(AdminCreateRequest request, String username) {
        User user = new User();
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUsername(username);
        user.setRole(1); // 管理员角色
        user.setStatus(1); // 正常状态
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());

        // 设置默认头像
        user.setAvatar(generateDefaultAvatarUrl());

        return user;
    }

    /**
     * 生成默认头像URL
     */
    private String generateDefaultAvatarUrl() {
        // 这里可以使用一个默认头像URL，或者根据用户ID生成
        return "/uploads/avatar/default_avatar.png"; // 修改为你的默认头像路径
    }

    /**
     * 构建创建管理员响应
     */
    private AdminCreateResponse buildAdminCreateResponse(User user) {
        return new AdminCreateResponse(
                user.getId(),
                user.getUsername(),
                user.getAvatar()
        );
    }
}