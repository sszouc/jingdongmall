package com.jingdong.mall.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 商品简略信息响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSimpleResponse {

    /**
     * 商品ID
     */
    private Integer id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品价格（最低价）
     */
    private BigDecimal price;

    /**
     * 商品主图（取第一张）
     */
    private String image;

    /**
     * 商品标签
     */
    private String tag;

    /**
     * 商品描述（可选）
     */
    private String description;
}