package com.jingdong.mall.model.dto.response;

import lombok.Data;

/**
 * 更新订单状态响应
 */
@Data
public class OrderUpdateResponse {
    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 更新后的状态
     */
    private Integer status;

    /**
     * 状态文本
     */
    private String statusText;

    /**
     * 更新时间
     */
    private String updatedAt;
}