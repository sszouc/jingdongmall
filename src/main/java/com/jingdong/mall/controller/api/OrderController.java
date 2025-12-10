package com.jingdong.mall.controller.api;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.common.response.Result;
import com.jingdong.mall.common.utils.JwtUtil;
import com.jingdong.mall.model.dto.request.OrderCreateRequest;
import com.jingdong.mall.model.dto.request.OrderCreateFromCartRequest;
import com.jingdong.mall.model.dto.response.OrderCreateResponse;
import com.jingdong.mall.service.OrderService;
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
@RequestMapping("/api/orders")
@Tag(name = "订单管理", description = "订单相关接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private JwtUtil jwtUtil;

    // 原有的批量创建订单接口保持不变
    @Operation(
            summary = "批量创建订单",
            description = "从购物车批量创建订单",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/from-cart")
    public Result<OrderCreateResponse> createOrderFromCart(
            @Parameter(description = "JWT认证令牌", required = true, example = "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody OrderCreateFromCartRequest request) {

        try {
            // 提取并验证Token
            String token = extractTokenFromHeader(authHeader);
            String userIdStr = jwtUtil.getUserIdFromToken(token);
            Long userId = Long.parseLong(userIdStr);

            log.info("用户 {} 请求批量创建订单，购物车项数量: {}", userId, request.getCartItemIds().size());

            // 调用Service创建订单
            OrderCreateResponse response = orderService.createOrderFromCart(userId, request);

            return Result.success("创建成功", response);
        } catch (BusinessException e) {
            log.warn("创建订单业务异常: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("创建订单系统异常", e);
            throw new BusinessException("创建订单失败，请稍后重试");
        }
    }

    /**
     * 新增：单个商品创建订单（立即购买）
     */
    @Operation(
            summary = "单个商品创建订单",
            description = "单个商品立即购买创建订单",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/buy-now")
    public Result<OrderCreateResponse> createOrder(
            @Parameter(description = "JWT认证令牌", required = true, example = "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody OrderCreateRequest request) {

        try {
            // 提取并验证Token
            String token = extractTokenFromHeader(authHeader);
            String userIdStr = jwtUtil.getUserIdFromToken(token);
            Long userId = Long.parseLong(userIdStr);

            log.info("用户 {} 请求单个商品创建订单，specId: {}, quantity: {}",
                    userId, request.getSpecId(), request.getQuantity());

            // 调用Service创建订单
            OrderCreateResponse response = orderService.createOrder(userId, request);

            return Result.success("订单创建成功", response);
        } catch (BusinessException e) {
            log.warn("创建单个商品订单业务异常: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("创建单个商品订单系统异常", e);
            throw new BusinessException("创建订单失败，请稍后重试");
        }
    }

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