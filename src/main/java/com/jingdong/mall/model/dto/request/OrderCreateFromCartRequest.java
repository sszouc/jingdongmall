package com.jingdong.mall.model.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 批量创建订单请求参数
 */
@Data
public class OrderCreateFromCartRequest {

    @NotNull(message = "地址ID不能为空")
    private Integer addressId;

    @NotEmpty(message = "购物车项ID不能为空")
    private List<Integer> cartItemIds;

    private String buyerRemark;
}