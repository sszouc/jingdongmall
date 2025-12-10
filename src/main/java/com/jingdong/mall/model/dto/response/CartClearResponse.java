package com.jingdong.mall.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 清空购物车响应结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartClearResponse {
    /**
     * 成功清空的购物车条目数量
     */
    private Integer clearedCount;
}