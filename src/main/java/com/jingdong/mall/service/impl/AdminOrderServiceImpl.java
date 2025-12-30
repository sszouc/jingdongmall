package com.jingdong.mall.service.impl;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.mapper.AdminOrderMapper;
import com.jingdong.mall.model.dto.request.AdminOrderQueryRequest;
import com.jingdong.mall.model.dto.request.AdminOrderStatusUpdateRequest;
import com.jingdong.mall.model.dto.response.AdminOrderQueryResponse;
import com.jingdong.mall.model.dto.response.AdminOrderResponse;
import com.jingdong.mall.model.entity.Order;
import com.jingdong.mall.service.AdminOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdminOrderServiceImpl implements AdminOrderService {

    @Autowired
    private AdminOrderMapper orderMapper;

    // 订单状态常量
    private static final int STATUS_PENDING_PAYMENT = 0;      // 待付款
    private static final int STATUS_PENDING_SHIPMENT = 1;     // 待发货
    private static final int STATUS_PENDING_RECEIPT = 2;      // 待收货
    private static final int STATUS_COMPLETED = 3;            // 已完成
    private static final int STATUS_CANCELLED = 4;            // 已取消
    private static final int STATUS_REFUNDING = 5;            // 退款中
    private static final int STATUS_REFUND_SUCCESS = 6;       // 退款成功
    private static final int STATUS_REFUND_FAILED = 7;        // 退款失败

    // 操作类型常量
    private static final int ACTION_SHIP = 1;                 // 发货
    private static final int ACTION_REFUND = 2;               // 退款
    private static final int ACTION_REJECT_REFUND = 3;        // 拒绝退款

    @Override
    public AdminOrderQueryResponse getOrderList(Integer currentUserRole, AdminOrderQueryRequest request) {
        // 0. 权限验证：只有管理员(role=2 || role=1)可以创建管理员
        validatePermission(currentUserRole);

        // 调试：打印查询参数
        log.info("查询参数: userId={}, orderSn={}, status={}, phone={}, startTime={}, endTime={}, page={}, pageSize={}",
                request.getUserId(), request.getOrderSn(), request.getStatus(),
                request.getPhone(), request.getStartTime(), request.getEndTime(),
                request.getPage(), request.getPageSize());

        // 1. 查询订单列表
        List<Order> orders = orderMapper.selectOrderList(request);
        log.info("查询到订单数量: {}", orders.size());

        // 2. 查询订单总数
        Long total = orderMapper.countOrderList(request);
        log.info("订单总数: {}", total);

        // 3. 转换为响应DTO
        List<AdminOrderResponse> orderResponses = orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        // 4. 构建响应
        AdminOrderQueryResponse response = new AdminOrderQueryResponse();
        response.setTotal(total);
        response.setPage(request.getPage());
        response.setPageSize(request.getPageSize());
        response.setOrders(orderResponses);

        return response;
    }

    @Transactional
    @Override
    public void updateOrderStatus(Integer currentUserRole, String orderSn, AdminOrderStatusUpdateRequest request) {
        // 0. 权限验证：只有管理员(role=2 || role=1)可以创建管理员
        validatePermission(currentUserRole);

        // 1. 查询订单
        Order order = orderMapper.selectByOrderSn(orderSn);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_EXIST);
        }

        // 2. 根据操作类型处理
        switch (request.getAction()) {
            case ACTION_SHIP:
                handleShipOrder(order, request);
                break;
            case ACTION_REFUND:
                handleRefundOrder(order, request);
                break;
            case ACTION_REJECT_REFUND:
                handleRejectRefund(order, request);
                break;
            default:
                throw new BusinessException(ErrorCode.INVALID_OPERATION);
        }
    }

    /**
     * 处理发货操作
     */
    private void handleShipOrder(Order order, AdminOrderStatusUpdateRequest request) {
        // 检查当前状态是否为待发货
        if (order.getStatus() != STATUS_PENDING_SHIPMENT) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR, "订单当前状态不能发货");
        }

        String shippingMethod = request.getShippingInfo().getShippingMethod();
        String trackingNumber = request.getShippingInfo().getTrackingNumber();

        // 更新订单状态为待收货
        int result = orderMapper.updateOrderShipping(
                order.getOrderSn(),
                STATUS_PENDING_RECEIPT,
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
     * 处理退款操作
     */
    private void handleRefundOrder(Order order, AdminOrderStatusUpdateRequest request) {
        // 检查当前状态是否为退款中
        if (order.getStatus() != STATUS_REFUNDING) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR, "订单当前状态不能退款");
        }

        // 更新订单状态为退款成功
        int result = orderMapper.updateOrderRefund(
                order.getOrderSn(),
                STATUS_REFUND_SUCCESS,
                LocalDateTime.now(), // 退款时间
                request.getReason()  // 退款原因
        );

        if (result <= 0) {
            throw new BusinessException(ErrorCode.REFUND_FAILED);
        }

        log.info("订单退款成功: orderSn={}, reason={}", order.getOrderSn(), request.getReason());
    }

    /**
     * 处理拒绝退款操作
     */
    private void handleRejectRefund(Order order, AdminOrderStatusUpdateRequest request) {
        // 检查当前状态是否为退款中
        if (order.getStatus() != STATUS_REFUNDING) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR, "订单当前状态不能拒绝退款");
        }

        // 注意：这里可能需要更新订单状态为原来的状态（比如待收货）
        // 假设我们要将状态从退款中改为待收货
        orderMapper.updateOrderRefund(
                order.getOrderSn(),
                STATUS_REFUND_FAILED, // 恢复为待收货状态
                null, // 退款时间
                "退款被拒绝: " + request.getReason()
        );

        log.info("订单拒绝退款: orderSn={}, reason={}", order.getOrderSn(), request.getReason());
    }

    /**
     * 转换订单实体为响应DTO
     */
    private AdminOrderResponse convertToResponse(Order order) {
        AdminOrderResponse response = new AdminOrderResponse();
        BeanUtils.copyProperties(order, response);

        // 设置状态描述
        response.setStatus(getStatusDesc(order.getStatus()));
        response.setUserId(order.getUserId());

        // 查询订单商品数量（注意：这里应该是order_id，不是order_sn）
        Integer itemCount = orderMapper.countOrderItems(order.getOrderSn());
        response.setItemCount(itemCount != null ? itemCount : 0);

        // 复制额外的属性
        response.setCreatedAt(order.getCreatedTime());
        response.setReceiverName(order.getReceiverName());
        response.setReceiverPhone(order.getReceiverPhone());
        response.setDiscountAmount(order.getDiscountAmount());
        response.setPayAmount(order.getPayAmount());
        response.setShippingMethod(order.getShippingMethod());
        response.setTrackingNumber(order.getTrackingNumber());
        response.setShippingTime(order.getShippingTime());
        response.setPayTime(order.getPayTime());
        response.setReceiverName(order.getReceiverName());
        response.setReceiverPhone(order.getReceiverPhone());
        response.setDiscountAmount(order.getDiscountAmount());
        response.setPayAmount(order.getPayAmount());
        response.setShippingMethod(order.getShippingMethod());
        response.setTrackingNumber(order.getTrackingNumber());


        return response;
    }

    @Override
    public String getStatusDesc(Integer status) {
        if (status == null) {
            return "未知状态";
        }

        switch (status) {
            case STATUS_PENDING_PAYMENT:
                return "待付款";
            case STATUS_PENDING_SHIPMENT:
                return "待发货";
            case STATUS_PENDING_RECEIPT:
                return "待收货";
            case STATUS_COMPLETED:
                return "已完成";
            case STATUS_CANCELLED:
                return "已取消";
            case STATUS_REFUNDING:
                return "退款中";
            case STATUS_REFUND_SUCCESS:
                return "退款成功";
            case STATUS_REFUND_FAILED:
                return "退款失败";
            default:
                return "未知状态";
        }
    }

    /**
     * 验证权限：只有管理员可以调用这个接口
     */
    private void validatePermission(Integer currentUserRole) {
        if (currentUserRole == null || (currentUserRole != 2 && currentUserRole != 1)) {
            log.warn("非管理员尝试操作订单，当前角色：{}", currentUserRole);
            throw new BusinessException(ErrorCode.ADMIN_NOT_PERMISSION);
        }
    }
}