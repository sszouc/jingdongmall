package com.jingdong.mall.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 添加购物车请求参数
 */
@Data
public class CartAddRequest {

    @NotNull(message = "商品ID不能为空")
    private Integer productId; // 商品主ID

    @NotNull(message = "SKU ID不能为空")
    private Integer skuId; // 商品SKU ID

    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量不能小于1")
    private Integer count; // 购买数量
}