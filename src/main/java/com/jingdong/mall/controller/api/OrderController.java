package com.jingdong.mall.controller.api;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.common.response.Result;
import com.jingdong.mall.common.utils.JwtUtil;
import com.jingdong.mall.model.dto.request.OrderCreateRequest;
import com.jingdong.mall.model.dto.request.OrderCreateFromCartRequest;
import com.jingdong.mall.model.dto.request.OrderListRequest;
import com.jingdong.mall.model.dto.response.OrderCreateResponse;
import com.jingdong.mall.model.dto.response.OrderDetailResponse;
import com.jingdong.mall.model.dto.response.OrderListResponse;
import com.jingdong.mall.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@Tag(name = "订单管理", description = "订单相关接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 获取订单列表
     */
    @Operation(
            summary = "获取订单列表",
            description = "分页查询用户订单，支持状态筛选和时间范围筛选",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("")
    public Result<OrderListResponse> getOrderList(
            @Parameter(description = "JWT认证令牌", required = true, example = "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "页码", required = false, example = "1")
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @Parameter(description = "每页数量", required = false, example = "10")
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @Parameter(description = "订单状态筛选", required = false, example = "0")
            @RequestParam(required = false) Integer status,
            @Parameter(description = "开始日期", required = false, example = "2024-01-01")
            @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "结束日期", required = false, example = "2024-12-31")
            @RequestParam(required = false) LocalDate endDate) {

        try {
            // 提取并验证Token
            String token = extractTokenFromHeader(authHeader);
            String userIdStr = jwtUtil.getUserIdFromToken(token);
            Long userId = Long.parseLong(userIdStr);

            log.info("用户 {} 请求获取订单列表，参数: page={}, pageSize={}, status={}, startDate={}, endDate={}",
                    userId, page, pageSize, status, startDate, endDate);

            // 构建请求参数
            OrderListRequest request = new OrderListRequest();
            request.setPage(page);
            request.setPageSize(pageSize);
            request.setStatus(status);
            request.setStartDate(startDate);
            request.setEndDate(endDate);

            // 参数校验
            if (page < 1) {
                throw new BusinessException("页码不能小于1");
            }
            if (pageSize < 1 || pageSize > 100) {
                throw new BusinessException("每页数量必须在1-100之间");
            }
            if (status != null && (status < 0 || status > 7)) {
                throw new BusinessException("订单状态不合法");
            }

            // 调用Service获取订单列表
            OrderListResponse response = orderService.getOrderList(userId, request);

            return Result.success("获取订单列表成功", response);
        } catch (BusinessException e) {
            log.warn("获取订单列表业务异常: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("获取订单列表系统异常", e);
            throw new BusinessException("获取订单列表失败，请稍后重试");
        }
    }

    //批量创建订单接口
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
     * 单个商品创建订单（立即购买）
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
     * 新增：获取订单详情
     */
    @Operation(
            summary = "获取订单详情",
            description = "根据订单号获取订单详细信息",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{orderSn}")
    public Result<OrderDetailResponse> getOrderDetail(
            @Parameter(description = "JWT认证令牌", required = true, example = "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "订单号", required = true, example = "100014")
            @PathVariable String orderSn) {

        try {
            // 提取并验证Token
            String token = extractTokenFromHeader(authHeader);
            String userIdStr = jwtUtil.getUserIdFromToken(token);
            Long userId = Long.parseLong(userIdStr);

            log.info("用户 {} 请求获取订单详情，订单号: {}", userId, orderSn);

            // 调用Service获取订单详情
            OrderDetailResponse response = orderService.getOrderDetail(userId, orderSn);

            return Result.success("获取订单详情成功", response);
        } catch (BusinessException e) {
            log.warn("获取订单详情业务异常: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("获取订单详情系统异常", e);
            throw new BusinessException("获取订单详情失败，请稍后重试");
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