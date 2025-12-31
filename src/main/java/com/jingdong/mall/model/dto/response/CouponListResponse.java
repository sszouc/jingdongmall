package com.jingdong.mall.model.dto.response;

import lombok.Data;
import java.util.List;

/**
 * 优惠券列表响应结果
 */
@Data
public class CouponListResponse {
    private List<CouponListItemResponse> list;
    private Integer total;
    private Integer page;
    private Integer pageSize;
}

