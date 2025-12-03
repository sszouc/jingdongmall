// src/main/java/com/jingdong/mall/service/UserService.java
package com.jingdong.mall.service;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 退出登录
     * @param token JWT令牌
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean signout(String token, Integer userId);

    /**
     * 获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    Object getUserInfo(Integer userId);

    /**
     * 更新用户信息
     * @param userId 用户ID
     * @param userInfo 用户信息
     * @return 是否成功
     */
    boolean updateUserInfo(Integer userId, Object userInfo);

    /**
     * 修改密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean changePassword(Integer userId, String oldPassword, String newPassword);
}