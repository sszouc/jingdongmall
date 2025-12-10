package com.jingdong.mall.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 按SKU ID删除购物车商品请求参数
 */
@Data
public class CartDeleteBySkuRequest {

    @NotNull(message = "SKU ID不能为空")
    private Integer skuId; // 商品SKU ID（对应product_sku表的id）
}