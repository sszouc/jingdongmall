package com.jingdong.mall.model.dto.request;

import lombok.Data;

import java.time.LocalDate;

/**
 * 订单列表查询请求参数
 */
@Data
public class OrderListRequest {

    /**
     * 当前页码，默认第1页
     */
    private Integer page = 1;

    /**
     * 每页数量，默认10条
     */
    private Integer pageSize = 10;

    /**
     * 订单状态筛选
     * 0待付款，1待发货，2待收货，3已完成，4已取消，5退款中，6退款成功，7退款失败
     */
    private Integer status;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    /**
     * 获取偏移量（用于分页查询）
     */
    public Integer getOffset() {
        return (page - 1) * pageSize;
    }
}