package com.jingdong.mall.model.dto.request;

import lombok.Data;

/**
 * 商品列表查询请求参数
 */
@Data
public class ProductListRequest {

    /**
     * 搜索关键字
     */
    private String keyword;

    /**
     * 分类ID
     */
    private Integer categoryId;

    /**
     * 当前页码，默认第1页
     */
    private Integer page = 1;

    /**
     * 每页数量，默认10条
     */
    private Integer pageSize = 10;

    /**
     * 排序方式
     * price_asc: 价格升序, price_desc: 价格降序
     * created_desc: 最新创建, sales_desc: 销量降序
     */
    private String sort = "created_desc";

    /**
     * 最低价格
     */
    private java.math.BigDecimal minPrice;

    /**
     * 最高价格
     */
    private java.math.BigDecimal maxPrice;

    /**
     * 获取偏移量（用于分页查询）
     */
    public Integer getOffset() {
        return (page - 1) * pageSize;
    }
}