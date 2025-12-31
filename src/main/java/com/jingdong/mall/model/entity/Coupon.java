package com.jingdong.mall.model.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券实体类（对应数据库表coupon）
 */
@Data
public class Coupon {
    private Long id;
    private String name;
    private Integer type; // 1-满减，2-折扣
    private BigDecimal value; // 优惠值：金额或折扣
    private BigDecimal minSpend; // 使用门槛
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer totalCount;
    private Integer usedCount;
    private Integer status; // 1-启用，0-禁用
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}

