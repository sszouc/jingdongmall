package com.jingdong.mall.model.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

/**
 * 购物车批量删除请求参数
 */
@Data
public class CartDeleteRequest {

    @NotEmpty(message = "购物车ID列表不能为空")
    private List<Integer> ids; // 购物车条目ID列表（对应数据库shopping_cart表的id）
}