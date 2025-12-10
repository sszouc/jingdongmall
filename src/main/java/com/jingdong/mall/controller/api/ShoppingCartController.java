// src/main/java/com/jingdong/mall/controller/api/ShoppingCartController.java
package com.jingdong.mall.controller.api;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.common.response.Result;
import com.jingdong.mall.common.utils.JwtUtil;
import com.jingdong.mall.model.dto.request.*;
import com.jingdong.mall.model.dto.response.*;
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

    // 新增：批量删除购物车商品接口
    @Operation(
            summary = "批量删除购物车商品",
            description = "删除当前登录用户的指定购物车条目（支持批量删除）",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/delete")
    public Result<CartDeleteResponse> deleteCartItems(
            @Parameter(description = "JWT认证令牌", required = true, example = "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CartDeleteRequest request) {
        // 1. 提取并验证Token（复用项目现有逻辑）
        String token = extractTokenFromHeader(authHeader);
        String userIdStr = jwtUtil.getUserIdFromToken(token);
        Long userId = Long.parseLong(userIdStr);

        // 2. 调用Service执行删除
        CartDeleteResponse response = shoppingCartService.deleteCartItems(userId, request);

        // 3. 返回成功响应
        return Result.success("购物车商品删除成功", response);
    }

    /**
     * 新增：获取购物车商品总数量
     * 用于前端顶部角标显示
     */
    @Operation(
            summary = "获取购物车商品总数量",
            description = "统计当前登录用户购物车中所有商品的总数量（累加各条目数量）",
            security = @SecurityRequirement(name = "bearerAuth") // 需登录授权
    )
    @GetMapping("/count") // 接口路径：/api/cart/count，符合项目API规范
    public Result<CartCountResponse> getCartTotalCount(
            @Parameter(description = "JWT认证令牌，格式：Bearer {token}", required = true, example = "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String authHeader) {
        // 1. 提取并验证Token（复用现有工具方法，避免重复代码）
        String token = extractTokenFromHeader(authHeader);
        String userIdStr = jwtUtil.getUserIdFromToken(token);
        Long userId = Long.parseLong(userIdStr);

        // 2. 调用Service获取总数量
        CartCountResponse response = shoppingCartService.getCartTotalCount(userId);

        // 3. 封装响应结果（使用项目统一Result工具类）
        return Result.success("购物车商品总数量获取成功", response);
    }

    /**
     * 批量更新购物车选中状态（全选/取消全选）
     */
    @Operation(
            summary = "批量更新购物车选中状态",
            description = "批量更新购物车条目的选中状态（支持全选/取消全选）",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/batch-select")
    public Result<CartBatchSelectResponse> batchUpdateSelectedStatus(
            @Parameter(description = "JWT认证令牌，格式：Bearer {token}", required = true, example = "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CartBatchSelectRequest request) {
        // 1. 提取并验证Token（复用现有工具方法，避免重复代码）
        String token = extractTokenFromHeader(authHeader);
        String userIdStr = jwtUtil.getUserIdFromToken(token);
        Long userId = Long.parseLong(userIdStr);

        // 2. 调用Service执行批量更新
        CartBatchSelectResponse response = shoppingCartService.batchUpdateSelectedStatus(userId, request);

        // 3. 封装响应结果（使用项目统一Result工具类）
        return Result.success("批量更新选中状态成功", response);
    }

    /**
     * 按SKU ID删除购物车商品
     */
    @Operation(
            summary = "按SKU ID删除购物车商品",
            description = "删除当前登录用户购物车中指定SKU的所有商品（支持自动清理重复记录）",
            security = @SecurityRequirement(name = "bearerAuth") // 需登录授权，与其他购物车接口一致
    )
    @DeleteMapping("/delete-by-sku")
    public Result<CartDeleteBySkuResponse> deleteCartBySkuId(
            @Parameter(description = "JWT认证令牌，格式：Bearer {token}", required = true,
                    example = "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CartDeleteBySkuRequest request) {
        // 1. 提取并验证Token（复用现有工具方法，避免重复代码）
        String token = extractTokenFromHeader(authHeader);
        String userIdStr = jwtUtil.getUserIdFromToken(token);
        Long userId = Long.parseLong(userIdStr);

        // 2. 调用Service执行删除逻辑
        CartDeleteBySkuResponse response = shoppingCartService.deleteCartBySkuId(userId, request);

        // 3. 封装响应结果（使用项目统一Result工具类，保持响应格式一致）
        return Result.success("按SKU ID删除购物车商品成功", response);
    }
}