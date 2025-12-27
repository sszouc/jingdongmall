package com.jingdong.mall.service.impl;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.common.utils.JwtUtil;
import com.jingdong.mall.mapper.UserMapper;
import com.jingdong.mall.model.dto.request.AdminChangePasswordRequest;
import com.jingdong.mall.model.dto.request.AdminUpdateRequest;
import com.jingdong.mall.model.dto.response.AdminInfoResponse;
import com.jingdong.mall.model.entity.User;
import com.jingdong.mall.service.AdminUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
public class AdminUserServiceImpl implements AdminUserService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAdminInfo(Long userId, Integer userRole, AdminUpdateRequest request) {
        // 1. 验证用户角色必须是管理员
        validateAdminRole(userRole, userId);

        // 2. 验证是否有可更新的字段
        if (!request.hasUpdatableFields()) {
            log.warn("管理员更新信息请求中没有可更新的字段，管理员ID：{}", userId);
            throw new BusinessException(ErrorCode.INFO_UPDATE_FAIL, "请至少提供一个可更新的字段");
        }

        try {
            // 3. 验证管理员用户是否存在且状态正常
            User adminUser = validateAdminUser(userId);

            // 4. 验证手机号唯一性（如果提供新手机号）
            if (request.getPhone() != null) {
                validatePhoneUnique(request.getPhone(), userId);
            }

            // 5. 验证邮箱唯一性（如果提供新邮箱）
            if (request.getEmail() != null) {
                validateEmailUnique(request.getEmail(), userId);
            }

            // 6. 执行更新
            int result = userMapper.updateAdminInfo(request, userId, LocalDateTime.now());
            if (result <= 0) {
                log.error("更新管理员信息失败，管理员ID：{}", userId);
                throw new BusinessException(ErrorCode.INFO_UPDATE_FAIL);
            }

            // 7. 记录日志
            log.info("管理员信息更新成功，管理员ID：{}，更新的字段：{}",
                    userId, getUpdatedFieldsDescription(request));

        } catch (BusinessException e) {
            // 业务异常直接抛出
            throw e;
        } catch (Exception e) {
            log.error("更新管理员信息发生系统异常，管理员ID：{}", userId, e);
            throw new BusinessException(ErrorCode.INFO_UPDATE_FAIL, "系统异常，更新管理员信息失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeAdminPassword(String token, Long userId, Integer userRole, AdminChangePasswordRequest request) {
        // 1. 验证用户角色必须是管理员
        validateAdminRole(userRole, userId);

        try {
            // 2. 验证管理员用户信息
            Map<String, Object> adminInfo = validateAdminUserForPasswordChange(userId);

            // 3. 验证旧密码是否正确
            validateOldPassword(request.getOldPassword(), (String) adminInfo.get("password"));

            // 4. 验证新密码不能与旧密码相同
            validateNewPasswordDifferent(request.getOldPassword(), request.getNewPassword());

            // 5. 验证密码强度（可选）
            validatePasswordStrength(request.getNewPassword());

            // 6. 加密新密码
            String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());

            // 7. 更新密码
            int result = userMapper.updateAdminPassword(userId, encodedNewPassword, LocalDateTime.now());
            if (result <= 0) {
                log.error("修改管理员密码失败，管理员ID：{}", userId);
                throw new BusinessException(ErrorCode.PASSWORD_CHANGE_FAIL);
            }

            // 8. 将当前token加入黑名单（密码修改后token应失效）
            try {
                jwtUtil.blackListToken(token, com.jingdong.mall.model.entity.TokenBlacklist.Reason.PWD_CHANGE);
                log.debug("管理员密码修改后，token已加入黑名单，管理员ID：{}", userId);
            } catch (NoSuchAlgorithmException e) {
                log.warn("Token哈希计算失败，管理员ID：{}", userId, e);
                // 密码修改成功，只是黑名单操作失败，不抛出异常
            }

            // 9. 记录日志
            log.info("管理员密码修改成功，管理员ID：{}", userId);

        } catch (BusinessException e) {
            // 业务异常直接抛出
            throw e;
        } catch (Exception e) {
            log.error("修改管理员密码发生系统异常，管理员ID：{}", userId, e);
            throw new BusinessException(ErrorCode.PASSWORD_CHANGE_FAIL, "系统异常，修改密码失败");
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

    /**
     * 验证管理员用户信息（用于密码修改）
     */
    private Map<String, Object> validateAdminUserForPasswordChange(Long userId) {
        Map<String, Object> adminInfo = userMapper.selectAdminPassword(userId);
        if (adminInfo == null || adminInfo.isEmpty()) {
            log.warn("管理员用户不存在，用户ID：{}", userId);
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        // 验证用户状态
        Integer status = (Integer) adminInfo.get("status");
        if (status != null && status == 0) {
            log.warn("管理员用户已被禁用，用户ID：{}", userId);
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }

        // 验证用户角色
        Integer role = (Integer) adminInfo.get("role");
        if (role == null || (role != 1 && role != 2)) {
            log.warn("用户不是管理员，用户ID：{}，角色：{}", userId, role);
            throw new BusinessException(ErrorCode.ADMIN_NOT_PERMISSION);
        }

        return adminInfo;
    }


    /**
     * 验证旧密码是否正确
     */
    private void validateOldPassword(String oldPassword, String storedPassword) {
        if (!passwordEncoder.matches(oldPassword, storedPassword)) {
            log.warn("原密码不正确");
            throw new BusinessException(ErrorCode.PASSWORD_ERROR);
        }
    }

    /**
     * 验证新密码不能与旧密码相同
     */
    private void validateNewPasswordDifferent(String oldPassword, String newPassword) {
        if (oldPassword.equals(newPassword)) {
            log.warn("新密码不能与旧密码相同");
            throw new BusinessException(ErrorCode.NEW_OLD_UNEQUAL);
        }
    }

    /**
     * 验证密码强度（可选，可根据需求扩展）
     */
    private void validatePasswordStrength(String password) {
        // 密码长度已在DTO中验证（6-20位）
        // 可以在这里添加更复杂的密码强度验证

        // 示例：检查是否包含数字和字母
        boolean hasLetter = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }

            if (hasLetter && hasDigit) {
                break;
            }
        }
    }

    /**
     * 验证管理员用户是否存在且状态正常
     */
    private User validateAdminUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            log.warn("管理员用户不存在，用户ID：{}", userId);
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        // 验证用户角色
        if (user.getRole() == null || (user.getRole() != 1 && user.getRole() != 2)) {
            log.warn("用户不是管理员，用户ID：{}，角色：{}", userId, user.getRole());
            throw new BusinessException(ErrorCode.ADMIN_NOT_PERMISSION);
        }

        // 验证用户状态
        if (user.getStatus() != null && user.getStatus() == 0) {
            log.warn("管理员用户已被禁用，用户ID：{}", userId);
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }

        return user;
    }

    /**
     * 验证手机号唯一性
     */
    private void validatePhoneUnique(String phone, Long excludeUserId) {
        int count = userMapper.countByPhoneExcludingUser(phone, excludeUserId);
        if (count > 0) {
            log.warn("手机号已存在，手机号：{}，排除用户ID：{}", phone, excludeUserId);
            throw new BusinessException(ErrorCode.PHONE_EXISTED);
        }
    }

    /**
     * 验证邮箱唯一性
     */
    private void validateEmailUnique(String email, Long excludeUserId) {
        if (email != null && !email.trim().isEmpty()) {
            int count = userMapper.countByEmailExcludingUser(email, excludeUserId);
            if (count > 0) {
                log.warn("邮箱已存在，邮箱：{}，排除用户ID：{}", email, excludeUserId);
                throw new BusinessException(ErrorCode.EMAIL_EXISTED);
            }
        }
    }

    /**
     * 获取更新的字段描述
     */
    private String getUpdatedFieldsDescription(AdminUpdateRequest request) {
        StringBuilder sb = new StringBuilder();
        if (request.getAvatar() != null) sb.append("头像, ");
        if (request.getEmail() != null) sb.append("邮箱, ");
        if (request.getPhone() != null) sb.append("手机号, ");
        if (request.getUsername() != null) sb.append("用户名, ");
        if (request.getGender() != null) sb.append("性别, ");
        if (request.getBirthday() != null) sb.append("生日, ");

        if (sb.length() > 0) {
            sb.setLength(sb.length() - 2); // 移除最后的逗号和空格
        }

        return sb.toString();
    }
}