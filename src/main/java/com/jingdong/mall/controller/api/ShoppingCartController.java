// src/main/java/com/jingdong/mall/controller/api/ShoppingCartController.java
package com.jingdong.mall.controller.api;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.common.response.Result;
import com.jingdong.mall.common.utils.JwtUtil;
import com.jingdong.mall.model.dto.request.CartAddRequest;
import com.jingdong.mall.model.dto.request.CartUpdateRequest;
import com.jingdong.mall.model.dto.response.CartItemResponse;
import com.jingdong.mall.model.dto.response.CartListResponse;
import com.jingdong.mall.service.ShoppingCartService;
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
@RequestMapping("/api/cart")
@Tag(name = "购物车", description = "购物车相关接口")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(
            summary = "获取购物车列表",
            description = "获取当前登录用户的所有购物车条目",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/list")
    public Result<CartListResponse> getCartList(
            @Parameter(description = "JWT认证令牌", required = true, example = "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String authHeader) {
        // 提取并验证Token
        String token = extractTokenFromHeader(authHeader);
        String userIdStr = jwtUtil.getUserIdFromToken(token);
        Long userId = Long.parseLong(userIdStr);

        // 调用Service获取购物车列表
        CartListResponse cartList = shoppingCartService.getUserCartList(userId);
        return Result.success("购物车列表获取成功", cartList);
    }

    /**
     * 从请求头提取Token（复用项目现有逻辑）
     */
    private String extractTokenFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID_FORMAT);
        }
        return authHeader.substring(7);
    }

    /**
     * 新增：添加商品到购物车
     */
    @Operation(
            summary = "添加商品到购物车",
            description = "添加商品到购物车，若该SKU已存在则自动累加数量",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/add")
    public Result<CartItemResponse> addCart(
            @Parameter(description = "JWT认证令牌", required = true, example = "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CartAddRequest request) {
        // 提取并验证Token（复用现有逻辑）
        String token = extractTokenFromHeader(authHeader);
        String userIdStr = jwtUtil.getUserIdFromToken(token);
        Long userId = Long.parseLong(userIdStr);

        // 调用服务添加购物车
        CartItemResponse response = shoppingCartService.addCart(userId, request);
        return Result.success("商品添加到购物车成功", response);
    }

    /**
     * 更新购物车商品
     * 支持更新数量或选中状态
     */
    @Operation(
            summary = "更新购物车商品",
            description = "更新购物车商品数量或选中状态（选中状态固定返回true，按业务要求）",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/update")
    public Result<CartItemResponse> updateCart(
            @Parameter(description = "JWT认证令牌", required = true, example = "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CartUpdateRequest request) {
        // 1. 提取并验证Token（复用现有逻辑）
        String token = extractTokenFromHeader(authHeader);
        String userIdStr = jwtUtil.getUserIdFromToken(token);
        Long userId = Long.parseLong(userIdStr);

        // 2. 调用服务更新购物车
        CartItemResponse response = shoppingCartService.updateCart(userId, request);
        return Result.success("购物车更新成功", response);
    }
}