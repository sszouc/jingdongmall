package com.jingdong.mall.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 单个商品创建订单请求参数
 */
@Data
public class OrderCreateRequest {

    @NotNull(message = "地址ID不能为空")
    private Integer addressId;

    @NotNull(message = "商品规格ID不能为空")
    @Min(value = 1, message = "商品规格ID必须大于0")
    private Integer specId;

    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量必须大于0")
    private Integer quantity;

    private String buyerRemark;
}