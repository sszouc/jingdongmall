package com.jingdong.mall.controller.api;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.common.response.Result;
import com.jingdong.mall.common.utils.JwtUtil;
import com.jingdong.mall.model.dto.request.AdminChangePasswordRequest;
import com.jingdong.mall.model.dto.request.AdminUpdateRequest;
import com.jingdong.mall.model.dto.response.AdminInfoResponse;
import com.jingdong.mall.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@Tag(name = "管理员/管理员个人中心", description = "管理员个人中心相关接口")
public class AdminUserController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AdminUserService adminUserService;

    @Operation(
            summary = "管理员登出",
            description = "管理员登出，将当前token加入黑名单",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/signout")
    public Result<String> adminSignout(
            @Parameter(description = "JWT认证令牌", required = true)
            @RequestHeader("Authorization") String authHeader) throws NoSuchAlgorithmException {

        // 1. 提取token
        String token = extractTokenFromHeader(authHeader);

        // 2. 验证token并获取用户信息
        String userIdStr = jwtUtil.getUserIdFromToken(token);
        Integer userRole = jwtUtil.getUserRoleFromToken(token);
        Long userId = Long.parseLong(userIdStr);

        // 3. 验证用户角色必须是管理员（role=1或2）
        validateAdminRole(userRole, userId);

        try {
            // 4. 将token加入黑名单
            jwtUtil.blackListToken(token, com.jingdong.mall.model.entity.TokenBlacklist.Reason.LOGOUT);

            // 5. 记录日志
            log.info("管理员登出成功，管理员ID：{}，角色：{}", userId, userRole);

            return Result.success("登出成功", null);

        } catch (NoSuchAlgorithmException e) {
            log.error("Token哈希计算失败，管理员ID：{}", userId, e);
            throw new BusinessException(ErrorCode.TOKEN_ADD_BLACKLIST_FAILED);
        } catch (Exception e) {
            log.error("管理员登出失败，管理员ID：{}", userId, e);
            throw new BusinessException(ErrorCode.LOGOUT_FAILED);
        }
    }

    @Operation(
            summary = "获取管理员信息",
            description = "获取当前登录的管理员信息",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/info")
    public Result<AdminInfoResponse> getAdminInfo(
            @Parameter(description = "JWT认证令牌", required = true)
            @RequestHeader("Authorization") String authHeader) {

        // 1. 提取token
        String token = extractTokenFromHeader(authHeader);

        // 2. 验证token并获取用户信息
        String userIdStr = jwtUtil.getUserIdFromToken(token);
        Integer userRole = jwtUtil.getUserRoleFromToken(token);
        Long userId = Long.parseLong(userIdStr);

        // 3. 调用服务层获取管理员信息
        AdminInfoResponse response = adminUserService.getAdminInfo(userId, userRole);

        return Result.success("成功", response);
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

    @Operation(
            summary = "更新管理员信息",
            description = "更新当前登录的管理员信息（只能更新自己的信息）",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/update")
    public Result<String> updateAdminInfo(
            @Parameter(description = "JWT认证令牌", required = true)
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody AdminUpdateRequest request) {

        // 1. 提取token
        String token = extractTokenFromHeader(authHeader);

        // 2. 验证token并获取用户信息
        String userIdStr = jwtUtil.getUserIdFromToken(token);
        Integer userRole = jwtUtil.getUserRoleFromToken(token);
        Long userId = Long.parseLong(userIdStr);

        // 3. 调用服务层更新管理员信息
        adminUserService.updateAdminInfo(userId, userRole, request);

        return Result.success("管理员信息更新成功", null);
    }

    @Operation(
            summary = "修改管理员密码",
            description = "修改当前登录的管理员密码（修改成功后当前token将失效）",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/change-password")
    public Result<String> changeAdminPassword(
            @Parameter(description = "JWT认证令牌", required = true)
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody AdminChangePasswordRequest request) {

        // 1. 提取token
        String token = extractTokenFromHeader(authHeader);

        // 2. 验证token并获取用户信息
        String userIdStr = jwtUtil.getUserIdFromToken(token);
        Integer userRole = jwtUtil.getUserRoleFromToken(token);
        Long userId = Long.parseLong(userIdStr);

        // 3. 调用服务层修改管理员密码
        adminUserService.changeAdminPassword(token, userId, userRole, request);

        return Result.success("密码修改成功", null);
    }

    /**
     * 从请求头中提取token
     */
    private String extractTokenFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID_FORMAT);
        }
        return authHeader.substring(7);
    }
}