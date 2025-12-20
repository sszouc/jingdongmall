package com.jingdong.mall.controller.api;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.common.response.Result;
import com.jingdong.mall.common.utils.JwtUtil;
import com.jingdong.mall.model.dto.request.AdminUserListRequest;
import com.jingdong.mall.model.dto.request.UserStatusUpdateRequest;
import com.jingdong.mall.model.dto.response.AdminUserListResponse;
import com.jingdong.mall.service.AdminService;
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

    @Operation(
            summary = "分页查询用户列表",
            description = "管理员分页查询用户列表，支持关键词搜索（用户名/手机号/邮箱）和状态筛选",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("")
    public Result<AdminUserListResponse> getUserList(
            @Parameter(description = "JWT认证令牌", required = true)
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "搜索关键词（用户名/手机号/邮箱）")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "按照状态筛选：1正常，0禁用")
            @RequestParam(required = false) Integer status,
            @Parameter(description = "页码，默认为1")
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @Parameter(description = "每页数量，默认为10")
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {

        // 提取并验证token
        String token = extractTokenFromHeader(authHeader);
        String currentUserIdStr = jwtUtil.getUserIdFromToken(token);
        Integer currentUserRole = jwtUtil.getUserRoleFromToken(token);
        Long currentUserId = Long.parseLong(currentUserIdStr);

        // 构建请求参数
        AdminUserListRequest request = new AdminUserListRequest();
        request.setKeyword(keyword);
        request.setStatus(status);
        request.setPage(page);
        request.setPageSize(pageSize);

        // 调用服务层查询用户列表
        AdminUserListResponse response = adminService.getUserList(currentUserId, currentUserRole, request);

        return Result.success("查询成功", response);
    }

    @Operation(
            summary = "封禁/解禁用户",
            description = "管理员封禁或解禁用户（只能操作普通用户，role=0）",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PatchMapping("/status/{id}")
    public Result<String> updateUserStatus(
            @Parameter(description = "JWT认证令牌", required = true)
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody UserStatusUpdateRequest request) {

        // 提取并验证token
        String token = extractTokenFromHeader(authHeader);
        String currentUserIdStr = jwtUtil.getUserIdFromToken(token);
        Integer currentUserRole = jwtUtil.getUserRoleFromToken(token);
        Long currentUserId = Long.parseLong(currentUserIdStr);

        // 调用服务层更新用户状态
        adminService.updateUserStatus(currentUserId, currentUserRole, id, request);

        return Result.success("用户状态更新成功", null);
    }
}