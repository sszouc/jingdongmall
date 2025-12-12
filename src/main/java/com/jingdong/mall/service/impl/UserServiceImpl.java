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
    public boolean signout(String token, long userId) {
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
            blacklistRecord.setUserId(Math.toIntExact(userId));
            blacklistRecord.setTokenHash(tokenHash);
            blacklistRecord.setExpiresAt(LocalDateTime.now().plusDays(30));
            blacklistRecord.setReason(TokenBlacklist.Reason.LOGOUT);
            blacklistRecord.setCreatedTime(LocalDateTime.now());

            // 保存到数据库
            int result = tokenBlacklistMapper.insert(blacklistRecord);
            if (result > 0) {
                log.info("Token added to blacklist: userId={}, reason=LOGOUT", userId);
                return true;
            }

            return false;
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
    public UserInfoResponse updateUserInfo(long userId, UserUpdateRequest request) {
        try {
            // 验证请求参数
            if (request == null || !request.isAnyFieldPresent()) {
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
                user.setAvatar(request.getAvatar());
                hasUpdates = true;
            }

            // 更新手机号
            if (StringUtils.hasText(request.getPhone())) {

                // 检查手机号是否已被其他用户使用
                int phoneCount = userMapper.countByPhone(request.getPhone());
                if (phoneCount > 0) {
                    throw new BusinessException(ErrorCode.PHONE_EXISTED);
                }
                user.setPhone(request.getPhone());
                hasUpdates = true;
            }

            // 更新邮箱
            if (StringUtils.hasText(request.getEmail())) {
                // 检查邮箱是否已被其他用户使用
                int emailCount = userMapper.countByEmail(request.getEmail());
                if (emailCount > 0) {
                    throw new BusinessException(ErrorCode.EMAIL_EXISTED);
                }
                user.setEmail(request.getEmail());
                hasUpdates = true;
            }

            // 如果没有实际更新，直接返回当前用户信息
            if (!hasUpdates) {
                return UserInfoResponse.fromEntity(user);
            }

            // 更新时间戳
            user.setUpdatedTime(LocalDateTime.now());

            // 保存到数据库
            int result = userMapper.updateUserInfo(user);
            if (result <= 0) {
                throw new BusinessException(ErrorCode.INFO_UPDATE_FAIL);
            }

            log.info("User info updated: userId={}", userId);

            // 重新查询获取最新数据
            User updatedUser = userMapper.selectById(userId);
            return UserInfoResponse.fromEntity(updatedUser);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to update user info", e);
            throw new BusinessException("更新用户信息失败");
        }
    }

    @Override
    @Transactional
    public boolean changePassword(long userId, String oldPassword, String newPassword) {
        try {
            log.info("开始修改密码: userId={}", userId);

            // 1. 参数校验
            if (oldPassword == null || oldPassword.trim().isEmpty()) {
                throw new BusinessException("旧密码不能为空");
            }

            if (newPassword == null || newPassword.trim().isEmpty()) {
                throw new BusinessException("新密码不能为空");
            }

            // 2. 新旧密码不能相同
            if (oldPassword.equals(newPassword)) {
                throw new BusinessException("新密码不能与旧密码相同");
            }

            // 3. 获取用户信息
            User user = userMapper.selectById(userId);
            if (user == null) {
                throw new BusinessException(ErrorCode.USER_NOT_EXIST);
            }

            // 4. 检查用户状态
            if (user.getStatus() != User.Status.ENABLED) {
                throw new BusinessException(ErrorCode.USER_DISABLED);
            }

            // 5. 验证旧密码
            log.debug("验证旧密码: userId={}, 输入密码长度={}, 数据库密码长度={}",
                    userId, oldPassword.length(),
                    user.getPassword() != null ? user.getPassword().length() : 0);

            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                log.warn("密码验证失败: userId={}", userId);
                throw new BusinessException(ErrorCode.PASSWORD_ERROR);
            }

            log.info("旧密码验证成功: userId={}", userId);

            // 6. 验证新密码复杂度
            if (!isValidPassword(newPassword)) {
                log.warn("新密码不符合复杂度要求: userId={}, password={}", userId, newPassword);
                throw new BusinessException("新密码必须包含字母和数字，长度6-20位");
            }

            // 7. 加密新密码
            String encodedNewPassword = passwordEncoder.encode(newPassword);
            log.debug("新密码加密完成: userId={}", userId);

            // 8. 更新密码
            LocalDateTime updatedTime = LocalDateTime.now();
            int result = userMapper.updatePasswordById(
                    user.getId(),
                    encodedNewPassword,
                    updatedTime
            );

            if (result > 0) {
                log.info("密码修改成功: userId={}", userId);
                return true;
            } else {
                log.error("密码更新失败，数据库返回0: userId={}", userId);
                throw new BusinessException("密码修改失败，请稍后重试");
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
     * 验证密码复杂度
     */
    private boolean isValidPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }

        // 去除空格
        String pwd = password.trim();

        // 长度检查
        if (pwd.length() < 6 || pwd.length() > 20) {
            return false;
        }

        // 必须包含字母和数字
        boolean hasLetter = false;
        boolean hasDigit = false;

        for (char c : pwd.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }

            // 同时满足字母和数字，提前结束
            if (hasLetter && hasDigit) {
                break;
            }
        }

        return hasLetter && hasDigit;
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