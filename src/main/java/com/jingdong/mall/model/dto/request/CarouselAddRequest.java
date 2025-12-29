package com.jingdong.mall.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 新增轮播图请求参数
 */
@Data
public class CarouselAddRequest {

    @NotBlank(message = "图片URL不能为空")
    private String imgUrl;       // 图片URL（必填）

    private String linkUrl;      // 跳转链接（可选）

    private Integer sortOrder;   // 排序字段（可选，默认0）

    private Integer isActive;    // 是否启用（可选，默认1）
}