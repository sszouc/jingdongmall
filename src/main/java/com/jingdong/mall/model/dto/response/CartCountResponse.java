package com.jingdong.mall.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 购物车商品总数量响应DTO
 * 用于顶部角标显示购物车商品总数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartCountResponse {
    /**
     * 购物车商品总数量（累加所有购物车条目的quantity）
     */
    private Integer totalCount;
}