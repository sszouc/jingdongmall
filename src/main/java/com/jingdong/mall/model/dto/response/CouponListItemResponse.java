package com.jingdong.mall.model.dto.response;

import lombok.Data;
import java.util.Date;

/**
 * 优惠券列表项响应list
 */
@Data
public class CouponListItemResponse {
    private Long id;
    private String name;
    private Integer type;
    private Double value;
    private Double minSpend;
    private Date startTime;
    private Date endTime;
    private Integer status;
    private Integer totalCount;
    private Integer usedCount;
    private Date createTime;
}

