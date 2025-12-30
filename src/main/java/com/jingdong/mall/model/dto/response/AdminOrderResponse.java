// model/dto/response/OrderResponse.java
package com.jingdong.mall.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AdminOrderResponse {
    private String orderSn;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private Integer itemCount;
    private Long userId;
    private String receiverName;
    private String receiverPhone;

    // 额外信息
    private String shippingMethod;
    private String trackingNumber;
    private BigDecimal discountAmount;
    private BigDecimal shippingFee;
    private LocalDateTime shippingTime;
    private LocalDateTime payTime;
}