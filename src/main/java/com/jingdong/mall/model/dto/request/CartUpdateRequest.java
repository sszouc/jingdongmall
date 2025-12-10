package com.jingdong.mall.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新购物车请求参数
 */
@Data
public class CartUpdateRequest {

    @NotNull(message = "购物车ID不能为空")
    private Integer id; // 购物车条目ID（对应数据库shopping_cart表的id）

    @Min(value = 1, message = "购买数量不能小于1")
    private Integer count; // 商品数量（可选，不填则不更新数量）

    private Boolean selected; // 选中状态（可选，不填则不更新选中状态）

    /**
     * 校验是否有可更新的字段
     * 避免空请求（既不更新数量也不更新选中状态）
     */
    public boolean hasUpdatableField() {
        return count != null || selected != null;
    }
}