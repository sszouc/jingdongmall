package com.jingdong.mall.service.impl;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.common.utils.JwtUtil;
import com.jingdong.mall.mapper.UserMapper;
import com.jingdong.mall.model.dto.request.AdminLoginRequest;
import com.jingdong.mall.model.dto.request.AdminResetPasswordRequest;
import com.jingdong.mall.model.dto.response.AdminLoginResponse;
import com.jingdong.mall.model.entity.User;
import com.jingdong.mall.service.AdminAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class AdminAuthServiceImpl implements AdminAuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdminLoginResponse login(AdminLoginRequest request) {
        try {
            // 1. 查询用户
            User user = userMapper.selectAdminByAccount(request.getAccount());
            if (user == null) {
                log.warn("管理员登录失败，账号不存在：{}", request.getAccount());
                throw new BusinessException(ErrorCode.USER_NOT_EXIST);
            }

            // 2. 验证用户角色必须是管理员
            validateAdminRole(user);

            // 3. 验证用户状态
            validateUserStatus(user);

            // 4. 验证密码
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                log.warn("管理员登录失败，密码错误，账号：{}", request.getAccount());
                throw new BusinessException(ErrorCode.PASSWORD_ERROR);
            }

            // 5. 生成JWT令牌
            String token = jwtUtil.generateToken(user);

            // 6. 构建用户信息
            AdminLoginResponse.UserInfo userInfo = buildUserInfo(user);

            // 7. 记录登录日志
            log.info("管理员登录成功，管理员ID：{}，账号：{}", user.getId(), request.getAccount());

            return new AdminLoginResponse(token, userInfo);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("管理员登录发生系统异常，账号：{}", request.getAccount(), e);
            throw new BusinessException(ErrorCode.ADMIN_OPERATION_FAILED, "登录失败，系统异常");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(AdminResetPasswordRequest request) {
        try {
            // 1. 查询用户
            User user = userMapper.selectAdminByPhone(request.getPhone());
            if (user == null) {
                log.warn("管理员找回密码失败，手机号不存在：{}", request.getPhone());
                throw new BusinessException(ErrorCode.USER_NOT_EXIST);
            }

            // 2. 验证用户角色必须是管理员
            validateAdminRole(user);

            // 3. 验证用户状态
            validateUserStatus(user);

            //TODO 4. 验证验证码（这里简化处理，实际应该对接短信验证码服务）
            //validateVerificationCode(request.getPhone(), request.getCode());

            // 5. 验证新密码不能与原密码相同
            if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
                log.warn("新密码不能与原密码相同，手机号：{}", request.getPhone());
                throw new BusinessException(ErrorCode.NEW_OLD_UNEQUAL);
            }

            // 6. 更新密码
            String encodedPassword = passwordEncoder.encode(request.getNewPassword());
            int result = userMapper.updatePasswordById(
                    user.getId(),
                    encodedPassword,
                    LocalDateTime.now()
            );

            if (result <= 0) {
                log.error("管理员密码重置失败，手机号：{}", request.getPhone());
                throw new BusinessException(ErrorCode.PASSWORD_CHANGE_FAIL);
            }

            // 7. 记录日志
            log.info("管理员密码重置成功，管理员ID：{}，手机号：{}", user.getId(), request.getPhone());

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("管理员找回密码发生系统异常，手机号：{}", request.getPhone(), e);
            throw new BusinessException(ErrorCode.ADMIN_OPERATION_FAILED, "密码重置失败，系统异常");
        }
    }

    /**
     * 验证用户角色必须是管理员
     */
    private void validateAdminRole(User user) {
        if (user.getRole() == null || (user.getRole() != 1 && user.getRole() != 2)) {
            log.warn("非管理员尝试登录，用户ID：{}，角色：{}", user.getId(), user.getRole());
            throw new BusinessException(ErrorCode.ADMIN_NOT_PERMISSION);
        }
    }

    /**
     * 验证用户状态
     */
    private void validateUserStatus(User user) {
        if (user.getStatus() != null && user.getStatus() == 0) {
            log.warn("管理员账号已被禁用，用户ID：{}", user.getId());
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }

        // 还需要检查是否在封禁期内，由所有接口的前置校验统一处理
    }

    /**
     * 构建用户信息
     */
    private AdminLoginResponse.UserInfo buildUserInfo(User user) {
        AdminLoginResponse.UserInfo userInfo = new AdminLoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setAvatar(user.getAvatar());
        return userInfo;
    }

    /**
     * 验证验证码（示例方法，需要根据实际验证码服务实现）
     */
    private void validateVerificationCode(String phone, String code) {
        // 这里应该调用验证码服务进行验证
        // 例如：verificationCodeService.validate(phone, code);

        // 简化处理：假设验证码为"123456"
        if (!"123456".equals(code)) {
            log.warn("验证码错误，手机号：{}，验证码：{}", phone, code);
            throw new BusinessException(ErrorCode.VERIFY_CODE_ERROR);
        }
    }
}