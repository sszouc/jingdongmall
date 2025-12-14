package com.jingdong.mall.service.impl;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.mapper.TokenBlacklistMapper;
import com.jingdong.mall.mapper.UserMapper;
import com.jingdong.mall.model.dto.request.UserUpdateRequest;
import com.jingdong.mall.model.dto.response.UserInfoResponse;
import com.jingdong.mall.model.entity.TokenBlacklist;
import com.jingdong.mall.model.entity.User;
import com.jingdong.mall.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private TokenBlacklistMapper tokenBlacklistMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void signout(String token, long userId) {
        try {
            // 计算token的SHA256哈希值
            String tokenHash = calculateTokenHash(token);

            // 检查token是否已经在黑名单中
            if (tokenBlacklistMapper.existsByTokenHash(tokenHash) > 0) {
                log.warn("Token already in blacklist: hash={}", tokenHash);
                throw new BusinessException(ErrorCode.TOKEN_EXPIRED);
            }

            // 创建黑名单记录
            TokenBlacklist blacklistRecord = new TokenBlacklist();
            blacklistRecord.setUserId(Math.toIntExact(userId));
            blacklistRecord.setTokenHash(tokenHash);
            blacklistRecord.setExpiresAt(LocalDateTime.now().plusDays(30));
            blacklistRecord.setReason(TokenBlacklist.Reason.LOGOUT);
            blacklistRecord.setCreatedTime(LocalDateTime.now());

            // 保存到数据库
            int result = tokenBlacklistMapper.insert(blacklistRecord);
            if (result > 0) {
                log.info("Token added to blacklist: userId={}, reason=LOGOUT", userId);
            }

        } catch (Exception e) {
            log.error("Failed to add token to blacklist", e);
            throw new BusinessException("退出登录失败，请重试");
        }
    }

    @Override
    public UserInfoResponse getUserInfo(long userId) {
        try {
            // 查询用户信息
            User user = userMapper.selectById(userId);
            if (user == null) {
                throw new BusinessException(ErrorCode.USER_NOT_EXIST);
            }

            // 转换为响应对象
            return UserInfoResponse.fromEntity(user);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to get user info", e);
            throw new BusinessException("获取用户信息失败");
        }
    }

    @Override
    @Transactional
    public void updateUserInfo(long userId, UserUpdateRequest request) {
        try {
            // 验证请求参数
            if (request == null) {
                throw new BusinessException("请提供要更新的信息");
            }

            // 查询用户信息
            User user = userMapper.selectById(userId);
            if (user == null) {
                throw new BusinessException(ErrorCode.USER_NOT_EXIST);
            }

            // 检查用户状态
            if (user.getStatus() != User.Status.ENABLED) {
                throw new BusinessException(ErrorCode.USER_DISABLED);
            }

            // 更新用户信息
            boolean hasUpdates = false;

            // 更新头像
            if (StringUtils.hasText(request.getAvatar())) {
                user.setAvatar(request.getAvatar());
                hasUpdates = true;
            }

            // 更新生日
            if (request.getBirthday() != null) {
                // 验证生日是否合理（不能是未来日期，已经在DTO中验证）
                user.setBirthday(request.getBirthday());
                hasUpdates = true;
            }

            // 更新性别
            if (StringUtils.hasText(request.getGender())) {
                user.setGender(request.getGenderAsInt());
                hasUpdates = true;
            }

            // 更新用户名
            if (StringUtils.hasText(request.getUsername())) {
                user.setUsername(request.getUsername());
                hasUpdates = true;
            }

            if (StringUtils.hasText(request.getPhone())) {
                // 检查手机号是否已被其他用户使用
                int phoneCount = userMapper.countByPhoneExcludingUser(request.getPhone(), user.getId());
                if (phoneCount > 0) {
                    throw new BusinessException(ErrorCode.PHONE_EXISTED);
                }
                user.setPhone(request.getPhone());
                hasUpdates = true;
            }

            // 更新邮箱
            if (StringUtils.hasText(request.getEmail())) {
                // 检查邮箱是否已被其他用户使用
                int emailCount = userMapper.countByEmailExcludingUser(request.getEmail(), user.getId());
                if (emailCount > 0) {
                    throw new BusinessException(ErrorCode.EMAIL_EXISTED);
                }
                user.setEmail(request.getEmail());
                hasUpdates = true;
            }

            // 如果没有实际更新，直接返回
            if (!hasUpdates) {
                return ;
            }

            // 更新时间戳
            user.setUpdatedTime(LocalDateTime.now());

            // 保存到数据库
            int result = userMapper.updateUserInfo(user);
            if (result <= 0) {
                throw new BusinessException(ErrorCode.INFO_UPDATE_FAIL);
            }

            log.info("User info updated: userId={}", userId);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to update user info", e);
            throw new BusinessException("更新用户信息失败");
        }
    }

    @Override
    @Transactional
    public void changePassword(long userId, String oldPassword, String newPassword) {
        try {
            log.info("开始修改密码: userId={}", userId);

            // 1. 新旧密码不能相同
            if (oldPassword.equals(newPassword)) {
                throw new BusinessException(ErrorCode.NEW_OLD_UNEQUAL);
            }

            // 2. 获取用户信息
            User user = userMapper.selectById(userId);
            if (user == null) {
                throw new BusinessException(ErrorCode.USER_NOT_EXIST);
            }

            // 3. 检查用户状态
            if (user.getStatus() != User.Status.ENABLED) {
                throw new BusinessException(ErrorCode.USER_DISABLED);
            }

            // 4. 验证旧密码
            log.debug("验证旧密码: userId={}, 输入密码长度={}, 数据库密码长度={}",
                    userId, oldPassword.length(),
                    user.getPassword() != null ? user.getPassword().length() : 0);

            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                log.warn("密码验证失败: userId={}", userId);
                throw new BusinessException(ErrorCode.PASSWORD_ERROR);
            }

            log.info("旧密码验证成功: userId={}", userId);

            // 6. 加密新密码
            String encodedNewPassword = passwordEncoder.encode(newPassword);
            log.debug("新密码加密完成: userId={}", userId);

            // 7. 更新密码
            LocalDateTime updatedTime = LocalDateTime.now();
            int result = userMapper.updatePasswordById(
                    user.getId(),
                    encodedNewPassword,
                    updatedTime
            );

            if (result > 0) {
                log.info("密码修改成功: userId={}", userId);
            } else {
                log.error("密码更新失败，数据库返回0: userId={}", userId);
                throw new BusinessException(ErrorCode.PASSWORD_ERROR);
            }
        } catch (BusinessException e) {
            log.warn("修改密码业务异常: userId={}, message={}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("修改密码系统异常", e);
            throw new BusinessException("密码修改失败");
        }
    }

    /**
     * 计算token的SHA256哈希值
     */
    private String calculateTokenHash(String token) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(token.getBytes(StandardCharsets.UTF_8));

        // 将字节数组转换为十六进制字符串
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }
}