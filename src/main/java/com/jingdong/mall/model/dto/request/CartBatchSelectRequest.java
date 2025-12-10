package com.jingdong.mall.model.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

/**
 * 批量更新购物车选中状态请求参数
 */
@Data
public class CartBatchSelectRequest {

    @NotEmpty(message = "购物车条目ID列表不能为空")
    private List<Integer> ids; // 购物车条目ID数组（对应数据库shopping_cart表的id）

    @NotNull(message = "选中状态不能为空")
    private Boolean selected; // 目标选中状态（true：选中，false：取消选中）
}