// src/main/java/com/jingdong/mall/service/impl/UserServiceImpl.java
package com.jingdong.mall.service.impl;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.mapper.TokenBlacklistMapper;
import com.jingdong.mall.mapper.UserMapper;
import com.jingdong.mall.model.entity.TokenBlacklist;
import com.jingdong.mall.model.entity.User;
import com.jingdong.mall.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

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
    public boolean signout(String token, Integer userId) {
        try {
            // 计算token的SHA256哈希值
            String tokenHash = calculateTokenHash(token);

            // 检查token是否已经在黑名单中
            if (tokenBlacklistMapper.existsByTokenHash(tokenHash) > 0) {
                log.warn("Token already in blacklist: hash={}", tokenHash);
                return false;
            }

            // 创建黑名单记录
            TokenBlacklist blacklistRecord = new TokenBlacklist();
            blacklistRecord.setUserId(userId);
            blacklistRecord.setTokenHash(tokenHash);
            blacklistRecord.setExpiresAt(LocalDateTime.now().plusDays(30)); // 设置30天后过期
            blacklistRecord.setReason(TokenBlacklist.Reason.LOGOUT);
            blacklistRecord.setCreatedTime(LocalDateTime.now());

            // 保存到数据库
            int result = tokenBlacklistMapper.insert(blacklistRecord);
            if (result > 0) {
                log.info("Token added to blacklist: userId={}, reason={}", userId, TokenBlacklist.Reason.LOGOUT);
                return true;
            }

            return false;
        } catch (Exception e) {
            log.error("Failed to add token to blacklist", e);
            throw new BusinessException("退出登录失败，请重试");
        }
    }

    @Override
    public Object getUserInfo(Integer userId) {
        try {
            // TODO: 实现获取用户信息的逻辑
            // 这里可以先返回简单信息，后面再完善
            return userMapper.selectById(userId);
        } catch (Exception e) {
            log.error("Failed to get user info", e);
            throw new BusinessException("获取用户信息失败");
        }
    }

    @Override
    @Transactional
    public boolean updateUserInfo(Integer userId, Object userInfo) {
        try {
            // TODO: 实现更新用户信息的逻辑
            // 这里需要根据传入的用户信息更新数据库
            log.info("Updating user info for userId: {}", userId);
            return true;
        } catch (Exception e) {
            log.error("Failed to update user info", e);
            throw new BusinessException("更新用户信息失败");
        }
    }

    @Override
    @Transactional
    public boolean changePassword(Integer userId, String oldPassword, String newPassword) {
        try {
            // 1. 获取用户信息
            User user = userMapper.selectById(userId);
            if (user == null) {
                throw new BusinessException(ErrorCode.USER_NOT_EXIST);
            }

            // 2. 验证旧密码
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                throw new BusinessException(ErrorCode.PASSWORD_ERROR);
            }

            // 3. 更新密码
            String encodedNewPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodedNewPassword);
            user.setUpdatedTime(LocalDateTime.now());

            // 4. 保存到数据库
            // 需要先创建updateUser方法，这里先用现有的updatePasswordById方法
            int result = userMapper.updatePasswordById(
                    user.getId(),
                    encodedNewPassword,
                    user.getUpdatedTime()
            );

            if (result > 0) {
                log.info("Password changed successfully for userId: {}", userId);
                return true;
            }

            throw new BusinessException("修改密码失败，请稍后重试");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to change password", e);
            throw new BusinessException("修改密码失败");
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