package com.jingdong.mall.service;

import com.jingdong.mall.model.dto.request.UserUpdateRequest;
import com.jingdong.mall.model.dto.response.UserInfoResponse;
import org.springframework.web.multipart.MultipartFile;

import java.security.NoSuchAlgorithmException;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 退出登录
     * @param token JWT令牌
     */
    void signout(String token);

    /**
     * 获取用户信息
     * @param userId 用户ID
     * @return 用户信息响应
     */
    UserInfoResponse getUserInfo(long userId);

    /**
     * 更新用户信息
     * @param userId 用户ID
     * @param request 更新请求
     */
    void updateUserInfo(long userId, UserUpdateRequest request);

    /**
     * 修改密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void changePassword(long userId, String oldPassword, String newPassword);

    /**
     * 上传用户头像
     * @param userId 用户ID
     * @param avatarFile 头像文件
     * @return 头像URL
     */
    String uploadAvatar(long userId, MultipartFile avatarFile);


}