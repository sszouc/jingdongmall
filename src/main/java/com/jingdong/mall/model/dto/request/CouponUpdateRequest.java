package com.jingdong.mall.model.dto.request;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 更新优惠券请求（所有字段可选，可部分更新）
 */
@Data
public class CouponUpdateRequest {
    private String name;
    private Integer type; // 1-满减，2-折扣
    private BigDecimal value;
    private BigDecimal minSpend;
    private String startTime; // yyyy-MM-dd HH:mm:ss
    private String endTime;   // yyyy-MM-dd HH:mm:ss
    private Integer totalCount;
    private Integer status; // 1启用，0禁用
}

