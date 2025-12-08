package com.jingdong.mall.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.math.BigDecimal; // 必须导入这个包

/**
 * 购物车列表响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor // 构造函数的参数类型会自动匹配成员变量类型
public class CartListResponse {
    private List<CartItemResponse> cartItems;
    private Integer totalCount;
    private BigDecimal totalPrice; // 这里的BigDecimal是java.math.BigDecimal
}