// model/dto/request/OrderStatusUpdateRequest.java
package com.jingdong.mall.model.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class AdminOrderStatusUpdateRequest {

    @NotNull(message = "操作类型不能为空")
    @Min(value = 1, message = "操作类型无效")
    private Integer action; // 1:发货, 2:退款, 3:拒绝退款

    private ShippingInfo shippingInfo; // 发货信息

    private String reason; // 原因

    @Data
    public static class ShippingInfo {
        private String shippingMethod; // 快递公司
        private String trackingNumber; // 快递单号
    }
}