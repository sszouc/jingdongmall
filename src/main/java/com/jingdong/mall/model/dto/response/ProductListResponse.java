package com.jingdong.mall.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 商品列表响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductListResponse {

    /**
     * 商品列表
     */
    private List<ProductSimpleResponse> productSimple;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页码
     */
    private Integer page;

    /**
     * 每页数量（字符串类型，符合OpenAPI规范）
     */
    private String pageSize;

    /**
     * 总页数（计算属性，不参与序列化）
     */
    public Integer getTotalPages() {
        if (total == null || pageSize == null || pageSize.isEmpty()) {
            return 0;
        }
        try {
            int size = Integer.parseInt(pageSize);
            if (size == 0) {
                return 0;
            }
            return (int) Math.ceil((double) total / size);
        } catch (NumberFormatException e) {
            // 如果pageSize不是有效的数字，返回0
            return 0;
        }
    }
}