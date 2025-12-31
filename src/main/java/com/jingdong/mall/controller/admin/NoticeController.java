// controller/api/marketing/NoticeController.java
package com.jingdong.mall.controller.admin;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.common.response.Result;
import com.jingdong.mall.common.utils.JwtUtil;
import com.jingdong.mall.model.dto.request.NoticeCreateRequest;
import com.jingdong.mall.model.dto.request.NoticeQueryRequest;
import com.jingdong.mall.model.dto.request.NoticeUpdateRequest;
import com.jingdong.mall.model.dto.response.NoticeDetailResponse;
import com.jingdong.mall.model.dto.response.NoticeListResponse;
import com.jingdong.mall.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/admin/marketing/notice")
@Tag(name = "公告管理", description = "商城公告的增删改查操作")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "获取公告列表", description = "分页查询公告列表")
    @GetMapping
    public Result<NoticeListResponse> getNoticeList(
            @Parameter(description = "页码", required = false, example = "1")
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @Parameter(description = "每页数量", required = false, example = "10")
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @Parameter(description = "标题", required = false)
            @RequestParam(required = false) String title,
            @Parameter(description = "公告类型", required = false)
            @RequestParam(required = false) Integer type,
            @Parameter(description = "公告状态", required = false)
            @RequestParam(required = false) Integer isActive,
            @Parameter(description = "JWT认证令牌", required = true, example = "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String authHeader) {

        // 1. 提取并验证token
        String token = extractTokenFromHeader(authHeader);
        Integer currentUserRole = jwtUtil.getUserRoleFromToken(token);

        NoticeQueryRequest noticeQueryRequest = new NoticeQueryRequest();
        noticeQueryRequest.setTitle(title);
        noticeQueryRequest.setType(type);
        noticeQueryRequest.setPage(page);
        noticeQueryRequest.setPageSize(pageSize);
        noticeQueryRequest.setIsActive(isActive);

        NoticeListResponse response = noticeService.getNoticeList(currentUserRole,noticeQueryRequest);
        return Result.success(response);
    }

    @Operation(summary = "获取公告详情", description = "根据ID获取公告详细信息")
    @GetMapping("/{id}")
    public Result<NoticeDetailResponse> getNoticeDetail(
            @Parameter(description = "公告ID", required = true)
            @PathVariable Long id) {
        NoticeDetailResponse response = noticeService.getNoticeDetail(id);
        return Result.success(response);
    }

    @Operation(summary = "新增公告", description = "创建新的商城公告")
    @PostMapping
    public Result<Map<String, Object>> createNotice(
            @Parameter(description = "公告信息")
            @RequestBody @Valid NoticeCreateRequest request,
            @Parameter(description = "JWT认证令牌", required = true, example = "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String authHeader) {

        // 1. 提取并验证token
        String token = extractTokenFromHeader(authHeader);
        Integer currentUserRole = jwtUtil.getUserRoleFromToken(token);


        Long noticeId = noticeService.createNotice(currentUserRole,request);

        Map<String, Object> data = new HashMap<>();
        data.put("id", noticeId);
        return Result.success(data);
    }

    @Operation(summary = "更新公告", description = "更新指定ID的公告信息")
    @PutMapping("/{id}")
    public Result<Void> updateNotice(
            @Parameter(description = "公告ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "公告信息")
            @RequestBody @Valid NoticeUpdateRequest request,
            @Parameter(description = "JWT认证令牌", required = true, example = "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String authHeader) {

        // 1. 提取并验证token
        String token = extractTokenFromHeader(authHeader);
        Integer currentUserRole = jwtUtil.getUserRoleFromToken(token);

        noticeService.updateNotice(currentUserRole,id, request);
        return Result.success();
    }

    @Operation(summary = "删除公告", description = "删除指定ID的公告")
    @DeleteMapping("/{id}")
    public Result<Void> deleteNotice(
            @Parameter(description = "公告ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "JWT认证令牌", required = true, example = "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String authHeader
    ) {
        // 1. 提取并验证token
        String token = extractTokenFromHeader(authHeader);
        Integer currentUserRole = jwtUtil.getUserRoleFromToken(token);

        noticeService.deleteNotice(currentUserRole,id);
        return Result.success();
    }

//    @Operation(summary = "更新公告状态", description = "启用或禁用公告")
//    @PatchMapping("/{id}/status")
//    public Result<Void> updateNoticeStatus(
//            @Parameter(description = "公告ID", required = true)
//            @PathVariable Long id,
//
//            @Parameter(description = "状态：1-启用，0-禁用", required = true)
//            @RequestParam Integer isActive) {
//        noticeService.updateNoticeStatus(id, isActive);
//        return Result.success();
//    }

    /**
     * 从请求头提取Token
     */
    private String extractTokenFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID_FORMAT);
        }
        return authHeader.substring(7);
    }
}