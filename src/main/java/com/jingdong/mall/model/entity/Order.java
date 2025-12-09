package com.jingdong.mall.model.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Data
public class Order {
    private Long id;
    private String orderSn;
    private Long userId;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal shippingFee;
    private BigDecimal payAmount;
    private String receiverName;
    private String receiverPhone;
    private String receiverProvince;
    private String receiverCity;
    private String receiverDistrict;
    private String receiverDetail;
    private String receiverPostalCode;
    private Integer status;
    private Integer paymentMethod;
    private LocalDateTime payTime;
    private String transactionId;
    private String shippingMethod;
    private String trackingNumber;
    private LocalDateTime shippingTime;
    private LocalDateTime confirmTime;
    private LocalDateTime cancelTime;
    private String cancelReason;
    private LocalDateTime refundTime;
    private String refundReason;
    private String buyerRemark;
    private String adminRemark;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}