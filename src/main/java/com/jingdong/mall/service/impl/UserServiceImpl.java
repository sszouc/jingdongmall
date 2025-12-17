package com.jingdong.mall.service.impl;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.common.utils.FileStorageUtil;
import com.jingdong.mall.common.utils.JwtUtil;
import com.jingdong.mall.mapper.UserMapper;
import com.jingdong.mall.model.dto.request.UserUpdateRequest;
import com.jingdong.mall.model.dto.response.UserInfoResponse;
import com.jingdong.mall.model.entity.User;
import com.jingdong.mall.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FileStorageUtil fileStorageUtil; // 替换原来的FileStorageService


    @Override
    @Transactional
    public void signout(String token) {
        try {
            jwtUtil.blackListToken(token, "LOGOUT");
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.TOKEN_ADD_BLACKLIST_FAILED);
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
            throw new BusinessException(ErrorCode.INFO_GET_FAIL);
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

//            // 更新头像
//            if (StringUtils.hasText(request.getAvatar())) {
//                user.setAvatar(request.getAvatar());
//                hasUpdates = true;
//            }

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
                return;
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
            throw new BusinessException(ErrorCode.INFO_UPDATE_FAIL);
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
            throw new BusinessException(ErrorCode.PASSWORD_ERROR);
        }
    }

    @Override
    @Transactional
    public String uploadAvatar(long userId, MultipartFile avatarFile) {
        try {
            log.info("开始上传头像: userId={}, fileSize={}", userId, avatarFile.getSize());

            // 1. 查询用户信息
            User user = userMapper.selectById(userId);
            if (user == null) {
                throw new BusinessException(ErrorCode.USER_NOT_EXIST);
            }

            // 2. 检查用户状态
            if (user.getStatus() != User.Status.ENABLED) {
                throw new BusinessException(ErrorCode.USER_DISABLED);
            }

            // 3. 保存头像文件
            String avatarUrl = fileStorageUtil.storeUserAvatar(avatarFile, userId);

            // 4. 保存旧头像URL用于后续删除
            String oldAvatar = user.getAvatar();

            // 5. 更新用户头像URL
            user.setAvatar(avatarUrl);
            user.setUpdatedTime(LocalDateTime.now());

            int result = userMapper.updateAvatar(userId, avatarUrl, LocalDateTime.now());
            if (result <= 0) {
                throw new BusinessException(ErrorCode.AVATAR_UPDATE_FAIL);
            }

            // 6. 删除旧头像文件（如果有的话）
            if (StringUtils.hasText(oldAvatar)) {
                try {
                    boolean deleted = fileStorageUtil.deleteFile(oldAvatar);
                    if (deleted) {
                        log.info("旧头像删除成功: userId={}, oldAvatar={}", userId, oldAvatar);
                    }
                } catch (Exception e) {
                    // 删除失败不影响主流程，只记录日志
                    log.warn("删除旧头像失败，可手动清理: userId={}, oldAvatar={}", userId, oldAvatar, e);
                }
            }

            log.info("头像上传成功: userId={}, avatarUrl={}", userId, avatarUrl);
            return avatarUrl;

        } catch (BusinessException e) {
            log.warn("头像上传业务异常: userId={}", userId, e);
            throw e;
        } catch (Exception e) {
            log.error("头像上传系统异常: userId={}", userId, e);
            throw new BusinessException(ErrorCode.AVATAR_UPDATE_FAIL);
        }
    }
}