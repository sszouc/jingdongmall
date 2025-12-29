package com.jingdong.mall.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 新增商品响应结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductAddResponse {
    private Integer id; // 数据库自动分配的商品ID
}