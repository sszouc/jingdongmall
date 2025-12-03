// src/main/java/com/jingdong/mall/controller/api/UserController.java
package com.jingdong.mall.controller.api;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.common.response.Result;
import com.jingdong.mall.common.utils.JwtUtil;
import com.jingdong.mall.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户个人中心控制器
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 退出登录
     * 将当前token加入黑名单
     */
    @PostMapping("/signout")
    public Result<String> signout(HttpServletRequest request) {
        // 从请求头中获取token
        String token = extractTokenFromRequest(request);

        if (token == null || token.isEmpty()) {
            throw new BusinessException(ErrorCode.TOKEN_ERROR, "缺少认证令牌");
        }

        try {
            // 从token中获取用户ID
            String userIdStr = jwtUtil.getUserIdFromToken(token);
            Integer userId = Integer.parseInt(userIdStr);

            // 调用UserService退出登录
            boolean success = userService.signout(token, userId);

            if (success) {
                log.info("用户退出登录成功: userId={}", userId);
                return Result.success("退出登录成功");
            } else {
                log.warn("用户退出登录失败: userId={}", userId);
                throw new BusinessException("退出登录失败，请重试");
            }
        } catch (BusinessException e) {
            // 如果token无效，仍然尝试将其加入黑名单
            log.warn("无效token尝试退出登录: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("退出登录异常", e);
            throw new BusinessException("退出登录失败，请重试");
        }
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    public Result<Object> getUserInfo(HttpServletRequest request) {
        try {
            // 从请求头中获取token
            String token = extractTokenFromRequest(request);

            if (token == null || token.isEmpty()) {
                throw new BusinessException(ErrorCode.TOKEN_ERROR, "缺少认证令牌");
            }

            // 从token中获取用户ID
            String userIdStr = jwtUtil.getUserIdFromToken(token);
            Integer userId = Integer.parseInt(userIdStr);

            // 获取用户信息
            Object userInfo = userService.getUserInfo(userId);

            return Result.success(userInfo);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取用户信息异常", e);
            throw new BusinessException("获取用户信息失败");
        }
    }

    /**
     * 修改密码请求体
     */
    public static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;

        // Getters and Setters
        public String getOldPassword() {
            return oldPassword;
        }

        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public Result<String> changePassword(
            @RequestBody @Valid ChangePasswordRequest request,
            HttpServletRequest httpRequest) {
        try {
            // 从请求头中获取token
            String token = extractTokenFromRequest(httpRequest);

            if (token == null || token.isEmpty()) {
                throw new BusinessException(ErrorCode.TOKEN_ERROR, "缺少认证令牌");
            }

            // 从token中获取用户ID
            String userIdStr = jwtUtil.getUserIdFromToken(token);
            Integer userId = Integer.parseInt(userIdStr);

            // 调用UserService修改密码
            boolean success = userService.changePassword(
                    userId,
                    request.getOldPassword(),
                    request.getNewPassword()
            );

            if (success) {
                log.info("密码修改成功: userId={}", userId);
                return Result.success("密码修改成功");
            } else {
                throw new BusinessException("密码修改失败");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("修改密码异常", e);
            throw new BusinessException("修改密码失败");
        }
    }

    /**
     * 从请求头中提取token
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        // 从Authorization头中获取token
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        // 也可以从其他位置获取token，比如query parameter
        String tokenParam = request.getParameter("token");
        if (tokenParam != null && !tokenParam.isEmpty()) {
            return tokenParam;
        }

        return null;
    }
}