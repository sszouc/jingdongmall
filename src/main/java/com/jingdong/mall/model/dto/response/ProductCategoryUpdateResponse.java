package com.jingdong.mall.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新分类响应结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategoryUpdateResponse {
    private Integer id; // 更新成功的分类ID
    private String name; // 更新后的分类名称
}