package com.jingdong.mall.controller.api;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.common.response.Result;
import com.jingdong.mall.common.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@Tag(name = "管理员/管理员个人中心", description = "管理员个人中心相关接口")
public class AdminUserController {

    @Autowired
    private JwtUtil jwtUtil;

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

    /**
     * 验证用户角色必须是管理员
     */
    private void validateAdminRole(Integer userRole, Long userId) {
        if (userRole == null || (userRole != 1 && userRole != 2)) {
            log.warn("非管理员尝试使用管理员登出接口，用户ID：{}，角色：{}", userId, userRole);
            throw new BusinessException(ErrorCode.ADMIN_NOT_PERMISSION);
        }
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