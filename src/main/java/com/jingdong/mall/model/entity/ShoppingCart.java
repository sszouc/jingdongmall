// src/main/java/com/jingdong/mall/model/entity/ShoppingCart.java
package com.jingdong.mall.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 购物车实体类（对应数据库shopping_cart表）
 */
@Data
public class ShoppingCart {
    private Long id; // 购物车主键ID
    private Long userId; // 用户ID
    private Integer skuId; // SKU ID
    private Integer quantity; // 商品数量
    private Boolean selected; // 是否选中（1选中，0未选中）
    private LocalDateTime createdTime; // 创建时间
    private LocalDateTime updatedTime; // 更新时间
}