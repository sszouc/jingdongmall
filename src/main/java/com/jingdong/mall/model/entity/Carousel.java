package com.jingdong.mall.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 轮播图实体类（对应数据库表carousel）
 */
@Data
public class Carousel {
    private Long id;             // 轮播图ID
    private String imgUrl;       // 图片URL
    private String linkUrl;      // 跳转链接
    private Integer sortOrder;   // 排序字段（值越小越靠前）
    private Integer isActive;    // 是否启用（1-启用，0-禁用）
    private LocalDateTime createdTime; // 创建时间
    private LocalDateTime updatedTime; // 更新时间
}