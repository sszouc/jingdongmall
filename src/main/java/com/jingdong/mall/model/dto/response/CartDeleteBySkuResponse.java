package com.jingdong.mall.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 按SKU ID删除购物车商品响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDeleteBySkuResponse {
    /**
     * 删除成功的购物车条目数量（通常为1，若存在异常重复记录可能大于1）
     */
    private Integer deletedCount;
}