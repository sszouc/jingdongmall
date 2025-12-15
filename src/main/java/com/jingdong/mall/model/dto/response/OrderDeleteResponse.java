package com.jingdong.mall.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单删除响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDeleteResponse {
    /**
     * 删除成功的订单号
     */
    private String orderSn;

    /**
     * 删除时间（格式：yyyy-MM-dd HH:mm:ss）
     */
    private String deletedTime;
}