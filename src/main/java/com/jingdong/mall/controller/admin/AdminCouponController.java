package com.jingdong.mall.controller.admin;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.common.response.Result;
import com.jingdong.mall.common.utils.JwtUtil;
import com.jingdong.mall.model.dto.request.CouponCreateRequest;
import com.jingdong.mall.model.dto.request.CouponListRequest;
import com.jingdong.mall.model.dto.response.CouponCreateResponse;
import com.jingdong.mall.model.dto.response.CouponListResponse;
import com.jingdong.mall.service.CouponService;
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
@RequestMapping("/api/admin/marketing/coupon")
@Tag(name = "管理员/优惠券管理", description = "优惠券管理相关接口")
public class AdminCouponController {

    @Autowired
    private CouponService couponService;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "创建优惠券", description = "创建新的优惠券", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public Result<CouponCreateResponse> createCoupon(
            @Parameter(description = "JWT认证令牌", required = true)
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CouponCreateRequest request) {

        // 提取并验证JWT令牌
        String token = extractTokenFromHeader(authHeader);
        String currentUserIdStr = jwtUtil.getUserIdFromToken(token);
        Integer currentUserRole = jwtUtil.getUserRoleFromToken(token);
        Long currentUserId = Long.parseLong(currentUserIdStr);

        // 调用服务层创建优惠券
        CouponCreateResponse response = couponService.createCoupon(currentUserId, currentUserRole, request);

        log.info("管理员 {} 创建优惠券: {}", currentUserId, request.getName());

        return Result.success("创建成功", response);
    }

    // 从Authorization头中提取Bearer令牌
    private String extractTokenFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID_FORMAT);
        }
        return authHeader.substring(7);
    }

    @Operation(summary = "获取优惠券列表", description = "获取优惠券的分页列表", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    public Result<CouponListResponse> listCoupons(
            @Parameter(description = "JWT认证令牌", required = true)
            @RequestHeader("Authorization") String authHeader,
            @Valid CouponListRequest request) {

        // 提取并验证JWT令牌
        String token = extractTokenFromHeader(authHeader);
        String currentUserIdStr = jwtUtil.getUserIdFromToken(token);
        Integer currentUserRole = jwtUtil.getUserRoleFromToken(token);
        Long currentUserId = Long.parseLong(currentUserIdStr);

        // 调用服务层获取优惠券列表
        CouponListResponse response = couponService.getCouponList(currentUserId, currentUserRole, request);

        log.info("管理员 {} 获取优惠券列表", currentUserId);

        return Result.success("获取成功", response);
    }
}
