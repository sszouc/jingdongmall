package com.jingdong.mall.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 轮播图删除响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarouselDeleteResponse {
    private Long id;             // 删除的轮播图ID
    private String deleteTime;   // 删除时间（格式：yyyy-MM-dd HH:mm:ss）
}