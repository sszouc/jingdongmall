package com.jingdong.mall.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.mapper.AdminOrderMapper;
import com.jingdong.mall.mapper.OrderItemMapper;
import com.jingdong.mall.model.dto.request.AdminOrderQueryRequest;
import com.jingdong.mall.model.dto.request.AdminOrderStatusUpdateRequest;
import com.jingdong.mall.model.dto.response.AdminOrderQueryResponse;
import com.jingdong.mall.model.dto.response.AdminOrderResponse;
import com.jingdong.mall.model.dto.response.OrderDetailResponse;
import com.jingdong.mall.model.entity.Order;
import com.jingdong.mall.model.entity.OrderItem;
import com.jingdong.mall.service.AdminOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdminOrderServiceImpl implements AdminOrderService {

    @Autowired
    private AdminOrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ObjectMapper objectMapper;

    // 订单状态枚举（可考虑抽取到常量类）
    private enum OrderStatus {
        PENDING_PAYMENT(0, "待付款"),
        PENDING_SHIPMENT(1, "待发货"),
        PENDING_RECEIPT(2, "待收货"),
        COMPLETED(3, "已完成"),
        CANCELLED(4, "已取消"),
        REFUNDING(5, "退款中"),
        REFUND_SUCCESS(6, "退款成功"),
        REFUND_FAILED(7, "退款失败");

        private final int code;
        private final String desc;

        OrderStatus(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

        public static String getDescByCode(Integer code) {
            if (code == null) return "未知状态";
            for (OrderStatus status : values()) {
                if (status.code == code) {
                    return status.desc;
                }
            }
            return "未知状态";
        }
    }

    // 操作类型枚举
    private enum ActionType {
        SHIP(1),
        REFUND(2),
        REJECT_REFUND(3);

        private final int code;

        ActionType(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static ActionType fromCode(int code) {
            for (ActionType action : values()) {
                if (action.code == code) {
                    return action;
                }
            }
            throw new BusinessException(ErrorCode.INVALID_OPERATION);
        }
    }

    // 支付方式枚举
    private enum PaymentMethod {
        UNPAID(0, "未支付"),
        ALIPAY(1, "支付宝"),
        WECHAT(2, "微信支付"),
        BANK_CARD(3, "银行卡");

        private final int code;
        private final String desc;

        PaymentMethod(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public static String getDescByCode(Integer code) {
            if (code == null) return "未支付";
            for (PaymentMethod method : values()) {
                if (method.code == code) {
                    return method.desc;
                }
            }
            return "其他";
        }
    }

    @Override
    public AdminOrderQueryResponse getOrderList(Integer currentUserRole, AdminOrderQueryRequest request) {
        // 权限验证
        validateAdminPermission(currentUserRole);

        // 日志记录
        logQueryParams(request);

        // 查询订单列表和总数
        List<Order> orders = orderMapper.selectOrderList(request);
        Long total = orderMapper.countOrderList(request);

        log.info("查询结果: 数量={}, 总数={}", orders.size(), total);

        // 构建响应
        AdminOrderQueryResponse response = new AdminOrderQueryResponse();
        response.setTotal(total);
        response.setPage(request.getPage());
        response.setPageSize(request.getPageSize());
        response.setOrders(orders.stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList()));

        return response;
    }

    @Transactional
    @Override
    public void updateOrderStatus(Integer currentUserRole, String orderSn, AdminOrderStatusUpdateRequest request) {
        validateAdminPermission(currentUserRole);

        Order order = orderMapper.selectByOrderSn(orderSn);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_EXIST);
        }

        ActionType action = ActionType.fromCode(request.getAction());
        switch (action) {
            case SHIP:
                handleOrderShipment(order, request);
                break;
            case REFUND:
                handleOrderRefund(order, request, true);
                break;
            case REJECT_REFUND:
                handleOrderRefund(order, request, false);
                break;
        }
    }

    @Override
    public OrderDetailResponse getOrderDetail(Integer currentUserRole, String orderSn) {
        validateOrderSn(orderSn);
        validateAdminPermission(currentUserRole);

        Order order = orderMapper.selectByOrderSn(orderSn);

        log.info(String.valueOf(order));
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_EXIST);
        }

        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(order.getId());

        return buildOrderDetailResponse(order, orderItems);
    }


    /**
     * 处理订单发货
     */
    private void handleOrderShipment(Order order, AdminOrderStatusUpdateRequest request) {
        validateOrderStatus(order, OrderStatus.PENDING_SHIPMENT, "发货");

        String shippingMethod = request.getShippingInfo().getShippingMethod();
        String trackingNumber = request.getShippingInfo().getTrackingNumber();

        int result = orderMapper.updateOrderShipping(
                order.getOrderSn(),
                OrderStatus.PENDING_RECEIPT.getCode(),
                shippingMethod,
                trackingNumber,
                LocalDateTime.now()
        );

        if (result <= 0) {
            throw new BusinessException(ErrorCode.SHIPPING_FAILED);
        }

        log.info("订单发货成功: orderSn={}, shippingMethod={}, trackingNumber={}",
                order.getOrderSn(), shippingMethod, trackingNumber);
    }

    /**
     * 处理订单退款/拒绝退款
     */
    private void handleOrderRefund(Order order, AdminOrderStatusUpdateRequest request, boolean isApprove) {
        validateOrderStatus(order, OrderStatus.REFUNDING, isApprove ? "退款" : "拒绝退款");

        int targetStatus = isApprove ?
                OrderStatus.REFUND_SUCCESS.getCode() :
                OrderStatus.REFUND_FAILED.getCode();

        String reason = isApprove ?
                request.getReason() :
                "退款被拒绝: " + request.getReason();

        int result = orderMapper.updateOrderRefund(
                order.getOrderSn(),
                targetStatus,
                isApprove ? LocalDateTime.now() : null,
                reason,
                isApprove
        );

        if (result <= 0) {
            throw new BusinessException(isApprove ? ErrorCode.REFUND_FAILED : ErrorCode.REFUND_REJECT_FAILED);
        }

        log.info("订单{}成功: orderSn={}, reason={}",
                isApprove ? "退款" : "拒绝退款", order.getOrderSn(), request.getReason());
    }

    /**
     * 构建订单详情响应
     */
    private OrderDetailResponse buildOrderDetailResponse(Order order, List<OrderItem> orderItems) {
        OrderDetailResponse response = new OrderDetailResponse();
        response.setOrder(convertToOrderDetailDTO(order));
        response.setItems(convertToOrderItemDTOs(orderItems));
        return response;
    }

    /**
     * 转换订单为响应DTO（复用逻辑）
     */
    private AdminOrderResponse convertToOrderResponse(Order order) {
        AdminOrderResponse response = new AdminOrderResponse();

        // 复制基础属性
        BeanUtils.copyProperties(order, response);

        // 设置状态和用户ID
        response.setStatus(OrderStatus.getDescByCode(order.getStatus()));
        response.setUserId(order.getUserId());

        // 查询商品数量
        Integer itemCount = orderMapper.countOrderItems(String.valueOf(order.getId()));
        response.setItemCount(itemCount != null ? itemCount : 0);

        // 复制扩展属性（避免重复代码）
        copyExtendedProperties(order, response);

        return response;
    }

    /**
     * 复制扩展属性（提取公共代码）
     */
    private void copyExtendedProperties(Order source, AdminOrderResponse target) {
        target.setCreatedAt(source.getCreatedTime());
        target.setReceiverName(source.getReceiverName());
        target.setReceiverPhone(source.getReceiverPhone());
        target.setDiscountAmount(source.getDiscountAmount());
        target.setPayAmount(source.getPayAmount());
        target.setShippingMethod(source.getShippingMethod());
        target.setTrackingNumber(source.getTrackingNumber());
        target.setShippingTime(source.getShippingTime());
        target.setPayTime(source.getPayTime());
        // 避免重复，上面已经设置过的属性不再设置
    }

    /**
     * 转换订单详情DTO
     */
    private OrderDetailResponse.OrderDetailDTO convertToOrderDetailDTO(Order order) {
        OrderDetailResponse.OrderDetailDTO dto = new OrderDetailResponse.OrderDetailDTO();

        // 复制基础属性
        BeanUtils.copyProperties(order, dto, "status", "paymentMethod");

        //屎山代码
        dto.setUpdatedAt(order.getUpdatedTime());
        dto.setCreatedAt(order.getCreatedTime());

        // 设置转换后的状态和支付方式
        dto.setStatus(OrderStatus.getDescByCode(order.getStatus()));
        dto.setPaymentMethod(PaymentMethod.getDescByCode(order.getPaymentMethod()));

        return dto;
    }

    /**
     * 转换订单项DTO列表
     */
    private List<OrderDetailResponse.OrderItemDTO> convertToOrderItemDTOs(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(this::convertToOrderItemDTO)
                .collect(Collectors.toList());
    }

    /**
     * 转换单个订单项DTO
     */
    private OrderDetailResponse.OrderItemDTO convertToOrderItemDTO(OrderItem item) {
        OrderDetailResponse.OrderItemDTO dto = new OrderDetailResponse.OrderItemDTO();

        // 复制基础属性
        BeanUtils.copyProperties(item, dto, "skuSpecs");

        // 解析规格JSON
        dto.setSkuSpecs(parseSkuSpecs(item.getSkuSpecs()));

        return dto;
    }

    /**
     * 解析商品规格JSON（提取公共解析逻辑）
     */
    private Object parseSkuSpecs(String skuSpecsJson) {
        if (skuSpecsJson == null || skuSpecsJson.isEmpty()) {
            return Map.of();
        }

        try {
            return objectMapper.readValue(skuSpecsJson, Object.class);
        } catch (Exception e) {
            log.warn("解析商品规格JSON失败: {}", skuSpecsJson, e);
            return Map.of();
        }
    }

    /**
     * 验证订单状态
     */
    private void validateOrderStatus(Order order, OrderStatus expectedStatus, String operation) {
        if (order.getStatus() != expectedStatus.getCode()) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR,
                    String.format("订单当前状态不能%s", operation));
        }
    }

    /**
     * 验证管理员权限
     */
    private void validateAdminPermission(Integer currentUserRole) {
        if (currentUserRole == null || (currentUserRole != 2 && currentUserRole != 1)) {
            log.warn("非管理员尝试操作，当前角色：{}", currentUserRole);
            throw new BusinessException(ErrorCode.ADMIN_NOT_PERMISSION);
        }
    }

    /**
     * 验证订单号
     */
    private void validateOrderSn(String orderSn) {
        if (orderSn == null || orderSn.trim().isEmpty()) {
            throw new BusinessException("订单号不能为空");
        }
    }

    /**
     * 记录查询参数日志
     */
    private void logQueryParams(AdminOrderQueryRequest request) {
        log.info("查询参数: userId={}, orderSn={}, status={}, phone={}, startTime={}, endTime={}, page={}, pageSize={}",
                request.getUserId(), request.getOrderSn(), request.getStatus(),
                request.getPhone(), request.getStartTime(), request.getEndTime(),
                request.getPage(), request.getPageSize());
    }
}