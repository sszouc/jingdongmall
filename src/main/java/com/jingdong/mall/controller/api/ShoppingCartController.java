// src/main/java/com/jingdong/mall/controller/api/ShoppingCartController.java
package com.jingdong.mall.controller.api;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.common.response.Result;
import com.jingdong.mall.common.utils.JwtUtil;
import com.jingdong.mall.model.dto.response.CartListResponse;
import com.jingdong.mall.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}