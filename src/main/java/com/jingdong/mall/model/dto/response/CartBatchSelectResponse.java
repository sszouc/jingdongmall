package com.jingdong.mall.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 批量更新购物车选中状态响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartBatchSelectResponse {
    /**
     * 成功更新的购物车条目数量
     */
    private Integer updatedCount;
}