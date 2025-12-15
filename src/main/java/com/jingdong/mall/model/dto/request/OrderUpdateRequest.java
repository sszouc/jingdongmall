package com.jingdong.mall.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新订单状态请求参数
 */
@Data
public class OrderUpdateRequest {

    /**
     * 操作类型
     * 0: 确认收货, 1: 取消订单, 2: 申请退款, 3: 支付订单
     */
    @NotNull(message = "操作类型不能为空")
    private Integer action;

    /**
     * 操作原因（如退款原因）
     */
    private String reason;
}