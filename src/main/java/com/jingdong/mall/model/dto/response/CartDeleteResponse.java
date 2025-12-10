package com.jingdong.mall.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 购物车删除响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDeleteResponse {
    private Integer deletedCount; // 删除成功的购物车条目数量
}