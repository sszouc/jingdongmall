// model/dto/response/OrderListResponse.java
package com.jingdong.mall.model.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class AdminOrderQueryResponse {
    private Long total;
    private Integer page;
    private Integer pageSize;
    private List<AdminOrderResponse> orders;
}