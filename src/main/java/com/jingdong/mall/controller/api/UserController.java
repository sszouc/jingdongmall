package com.jingdong.mall.controller.api;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.common.response.Result;
import com.jingdong.mall.common.utils.JwtUtil;
import com.jingdong.mall.model.dto.request.ChangePasswordRequest;
import com.jingdong.mall.model.dto.request.UserUpdateRequest;
import com.jingdong.mall.model.dto.response.UserInfoResponse;
import com.jingdong.mall.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
     */
    @PostMapping("/signout")
    public Result<String> signout(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);

        if (token == null || token.isEmpty()) {
            throw new BusinessException(ErrorCode.TOKEN_ERROR, "缺少认证令牌");
        }

        String userIdStr = jwtUtil.getUserIdFromToken(token);
        long userId = Long.parseLong(userIdStr);

        userService.signout(token, userId);

        log.info("用户退出登录成功: userId={}", userId);
        return Result.success("退出登录成功", null);

    }

    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    public Result<UserInfoResponse> getUserInfo(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);

        if (token == null || token.isEmpty()) {
            throw new BusinessException(ErrorCode.TOKEN_ERROR, "缺少认证令牌");
        }

        String userIdStr = jwtUtil.getUserIdFromToken(token);
        long userId = Long.parseLong(userIdStr);

        UserInfoResponse userInfo = userService.getUserInfo(userId);

        return Result.success(userInfo);
    }

    /**
     * 修改密码
     */
    @PutMapping("/change-password")
    public Result<String> changePassword(
            @RequestBody @Valid ChangePasswordRequest request,
            HttpServletRequest httpRequest) {

        String token = extractTokenFromRequest(httpRequest);

        if (token == null || token.isEmpty()) {
            throw new BusinessException(ErrorCode.TOKEN_ERROR);
        }

        String userIdStr = jwtUtil.getUserIdFromToken(token);
        long userId = Long.parseLong(userIdStr);

        userService.changePassword(userId, request.getOldPassword(), request.getNewPassword());

        return Result.success("密码修改成功", null);

    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public Result<String> updateUserInfo(
            @RequestBody @Valid UserUpdateRequest request,
            HttpServletRequest httpRequest) {
        // 验证token
        String token = extractTokenFromRequest(httpRequest);

        if (token == null || token.isEmpty()) {
            throw new BusinessException(ErrorCode.TOKEN_ERROR, "缺少认证令牌");
        }

        // 获取用户ID
        String userIdStr = jwtUtil.getUserIdFromToken(token);
        long userId = Long.parseLong(userIdStr);

        // 更新用户信息
        userService.updateUserInfo(userId, request);

        log.info("用户信息更新成功: userId={}", userId);
        return Result.success("信息更新成功", null);
    }

    /**
     * 上传用户头像
     * @param file 头像文件
     * @param httpRequest HTTP请求
     * @return 头像URL
     */
    @PostMapping("/avatar")
    public Result<String> uploadAvatar(
            @RequestParam("avatar") MultipartFile file,
            HttpServletRequest httpRequest) {


        // 调试：打印请求参数
        log.warn(String.valueOf(file));
        // 调试：打印文件信息
        log.warn("文件名: {}", file.getOriginalFilename());
        log.warn("文件大小: {} bytes", file.getSize());
        log.warn("文件类型: {}", file.getContentType());
        log.warn("是否为空: {}", file.isEmpty());

        // 验证token
        String token = extractTokenFromRequest(httpRequest);
        if (token == null || token.isEmpty()) {
            throw new BusinessException(ErrorCode.TOKEN_ERROR, "缺少认证令牌");
        }

        // 获取用户ID
        String userIdStr = jwtUtil.getUserIdFromToken(token);
        long userId = Long.parseLong(userIdStr);

        // 验证文件是否为空
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.AVATAR_EMPTY, "请选择要上传的头像文件");
        }

        // 调用服务上传头像
        String avatarUrl = userService.uploadAvatar(userId, file);

        log.info("用户头像上传成功: userId={}, avatarUrl={}", userId, avatarUrl);
        return Result.success("头像上传成功", avatarUrl);
    }

    /**
     * 从请求头中提取token
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        String tokenParam = request.getParameter("token");
        if (tokenParam != null && !tokenParam.isEmpty()) {
            return tokenParam;
        }

        return null;
    }
}