package com.jingdong.mall.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建管理员响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminCreateResponse {
    private Long id;
    private String username;
    private String avatar;
}