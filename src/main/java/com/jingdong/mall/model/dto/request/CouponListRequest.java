package com.jingdong.mall.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 优惠券列表请求参数
 */
@Data
public class CouponListRequest {

    @Min(1)
    private Integer page = 1;

    @Min(1)
    private Integer pageSize = 10;

    private String name;

    private Integer type; // 1-满减，2-折扣

    private Integer status; // 1-启用，0-禁用
}

