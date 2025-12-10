package com.jingdong.mall.service;

import com.jingdong.mall.model.dto.request.OrderCreateRequest;
import com.jingdong.mall.model.dto.request.OrderCreateFromCartRequest;
import com.jingdong.mall.model.dto.response.OrderCreateResponse;
import com.jingdong.mall.model.dto.response.OrderDetailResponse;
import com.jingdong.mall.model.dto.request.OrderListRequest;
import com.jingdong.mall.model.dto.response.OrderListResponse;

/**
 * 订单服务接口
 */
public interface OrderService {

    /**
     * 批量创建订单（从购物车）
     */
    OrderCreateResponse createOrderFromCart(Long userId, OrderCreateFromCartRequest request);

    /**
     * 单个商品创建订单（立即购买）
     */
    OrderCreateResponse createOrder(Long userId, OrderCreateRequest request);

    /**
     * 获取订单详情
     * @param userId 用户ID
     * @param orderSn 订单号
     * @return 订单详情
     */
    OrderDetailResponse getOrderDetail(Long userId, String orderSn);

    /**
     * 获取订单列表
     * @param userId 用户ID
     * @param request 查询请求参数
     * @return 订单列表响应
     */
    OrderListResponse getOrderList(Long userId, OrderListRequest request);
}