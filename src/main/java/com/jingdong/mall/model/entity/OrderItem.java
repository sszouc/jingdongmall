package com.jingdong.mall.model.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单商品明细实体类
 */
@Data
public class OrderItem {
    private Long id;
    private Long orderId;
    private Integer skuId;
    private String productName;
    private String skuSpecs; // JSON格式存储
    private String mainImage;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal totalPrice;
    private Integer afterSaleStatus;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}