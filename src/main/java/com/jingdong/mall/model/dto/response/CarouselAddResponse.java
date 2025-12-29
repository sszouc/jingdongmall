package com.jingdong.mall.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 新增轮播图响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarouselAddResponse {
    private Long id;             // 新增轮播图ID
    private String imgUrl;       // 图片URL
    private String linkUrl;      // 跳转链接
    private Integer sortOrder;   // 排序字段
    private Integer isActive;    // 是否启用
}