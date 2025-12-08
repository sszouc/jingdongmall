// src/main/java/com/jingdong/mall/model/dto/response/CartItemResponse.java
package com.jingdong.mall.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 购物车条目响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    private Integer id; // 购物车ID
    private Integer productId; // 商品ID
    private Integer skuId; // SKU ID
    private String name; // 商品名称
    private String imgUrl; // 商品主图URL
    private Map<String, String> specs; // 商品规格（如{"硬盘":"1T","显卡":"RTX 5060"}）
    private BigDecimal price; // 商品单价
    private Integer count; // 购买数量
    private Integer stock; // 库存数量
    private Boolean selected; // 是否选中
}