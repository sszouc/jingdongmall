package com.jingdong.mall.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 订单创建响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateResponse {
    private String orderSn;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private Integer expiresIn; // 支付剩余时间（秒）
}