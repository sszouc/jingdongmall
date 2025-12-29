package com.jingdong.mall.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 轮播图响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarouselResponse {
    private Long id;             // 轮播图ID
    private String imgUrl;       // 图片URL
    private String linkUrl;      // 点击跳转链接
    private Integer sortOrder;   // 排序字段
    private Integer isActive;    // 是否启用（1-启用，0-禁用）
}