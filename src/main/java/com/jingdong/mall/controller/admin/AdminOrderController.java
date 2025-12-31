// controller/api/admin/OrderController.java
package com.jingdong.mall.controller.admin;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.common.response.Result;
import com.jingdong.mall.common.utils.JwtUtil;
import com.jingdong.mall.model.dto.request.AdminOrderQueryRequest;
import com.jingdong.mall.model.dto.request.AdminOrderStatusUpdateRequest;
import com.jingdong.mall.model.dto.response.AdminOrderQueryResponse;
import com.jingdong.mall.model.dto.response.OrderDetailResponse;
import com.jingdong.mall.service.AdminOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/admin/orders")
@Tag(name = "订单管理", description = "管理员订单管理接口")
public class AdminOrderController {

    @Autowired
    private AdminOrderService orderService;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "分页查询订单列表",
            description = "支持订单号、用户ID、状态、手机号等多条件筛选",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    public Result<AdminOrderQueryResponse> getOrderList(
            @Parameter(description = "页码", required = false, example = "1")
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @Parameter(description = "每页数量", required = false, example = "10")
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @Parameter(description = "订单号", required = false)
            @RequestParam(required = false) String orderSn,
            @Parameter(description = "订单状态筛选", required = false)
            @RequestParam(required = false) Integer status,
            @Parameter(description = "用户id筛选", required = false)
            @RequestParam(required = false) Integer userId,
            @Parameter(description = "收件人手机号筛选", required = false)
            @RequestParam(required = false) String phone,
            @Parameter(description = "JWT认证令牌", required = true, example = "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String authHeader) {

        // 1. 提取并验证token
        String token = extractTokenFromHeader(authHeader);
        Integer currentUserRole = jwtUtil.getUserRoleFromToken(token);

        AdminOrderQueryRequest request = new AdminOrderQueryRequest();

        request.setPage(page);
        request.setPageSize(pageSize);
        request.setStatus(status);
        request.setUserId(userId);
        request.setPhone(phone);
        request.setOrderSn(orderSn);

        AdminOrderQueryResponse response = orderService.getOrderList(currentUserRole, request);
        return Result.success(response);


    }

    @Operation(summary = "更新订单状态",
            description = "支持发货、退款、拒绝退款等操作",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/{orderSn}/status")
    public Result<Void> updateOrderStatus(
            @Parameter(description = "JWT认证令牌", required = true, example = "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "订单号", required = true, example = "202312010001")
            @PathVariable String orderSn,
            @Parameter(description = "状态更新请求")
            @RequestBody @Valid AdminOrderStatusUpdateRequest request) {

        String token = extractTokenFromHeader(authHeader);
        Integer currentUserRole = jwtUtil.getUserRoleFromToken(token);

        orderService.updateOrderStatus(currentUserRole, orderSn, request);
        return Result.success();
    }

    @Operation(summary = "获取订单详情", description = "根据订单号获取订单详细信息")
    @GetMapping("/{orderSn}")
    public Result<OrderDetailResponse> getOrderDetail(
            @Parameter(description = "JWT认证令牌", required = true, example = "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "订单号", required = true, example = "202312010001")
            @PathVariable String orderSn) {

        String token = extractTokenFromHeader(authHeader);
        Integer currentUserRole = jwtUtil.getUserRoleFromToken(token);

        OrderDetailResponse response = orderService.getOrderDetail(currentUserRole, orderSn);
        return Result.success(response);
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