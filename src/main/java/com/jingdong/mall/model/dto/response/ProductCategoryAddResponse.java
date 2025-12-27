package com.jingdong.mall.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 新增分类响应结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategoryAddResponse {

    private Integer id; // 数据库分配的分类ID
}