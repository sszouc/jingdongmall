// model/dto/request/OrderQueryRequest.java
package com.jingdong.mall.model.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class AdminOrderQueryRequest {

    @Min(value = 1, message = "页码最小为1")
    private Integer page = 1;

    @Min(value = 1, message = "每页数量最小为1")
    private Integer pageSize = 10;

    private String orderSn; // 订单号搜索
    private Integer userId; // 用户ID筛选
    private Integer status; // 状态筛选
    private String phone;   // 收货人手机号搜索

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

}