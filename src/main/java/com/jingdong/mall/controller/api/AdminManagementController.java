package com.jingdong.mall.controller.api;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.common.response.Result;
import com.jingdong.mall.common.utils.JwtUtil;
import com.jingdong.mall.model.dto.request.AdminCreateRequest;
import com.jingdong.mall.model.dto.response.AdminCreateResponse;
import com.jingdong.mall.service.AdminManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin/admins")
@Tag(name = "管理员/管理员管理", description = "管理员管理相关接口")
public class AdminManagementController {

    @Autowired
    private AdminManagementService adminManagementService;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(
            summary = "创建管理员",
            description = "超级管理员创建新管理员（默认role=1）",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/create")
    public Result<AdminCreateResponse> createAdmin(
            @Parameter(description = "JWT认证令牌", required = true)
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody AdminCreateRequest request) {

        // 1. 提取并验证token
        String token = extractTokenFromHeader(authHeader);
        String currentUserIdStr = jwtUtil.getUserIdFromToken(token);
        Integer currentUserRole = jwtUtil.getUserRoleFromToken(token);
        Long currentUserId = Long.parseLong(currentUserIdStr);

        // 2. 调用服务层创建管理员
        AdminCreateResponse response = adminManagementService.createAdmin(
                currentUserId, currentUserRole, request);

        // 3. 记录日志
        log.info("管理员创建成功，操作人：{}，创建的管理员手机号：{}",
                currentUserId, request.getPhone());

        return Result.success("管理员创建成功", response);
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