package com.jingdong.mall.controller.api;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.common.response.Result;
import com.jingdong.mall.common.utils.JwtUtil;
import com.jingdong.mall.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin/users")
@Tag(name = "管理员/用户管理", description = "管理员用户管理相关接口")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(
            summary = "删除用户",
            description = "管理员删除用户（只能删除普通用户，role=0）",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteUser(
            @Parameter(description = "JWT认证令牌", required = true)
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "用户ID", required = true, example = "12")
            @PathVariable Long id) {

        // 提取并验证token
        String token = extractTokenFromHeader(authHeader);
        String currentUserIdStr = jwtUtil.getUserIdFromToken(token);
        Integer currentUserRole = jwtUtil.getUserRoleFromToken(token);
        Long currentUserId = Long.parseLong(currentUserIdStr);

        // 调用服务层删除用户
        adminService.deleteUser(currentUserId, currentUserRole, id);

        return Result.success("用户删除成功", null);
    }

    private String extractTokenFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID_FORMAT);
        }
        return authHeader.substring(7);
    }
}