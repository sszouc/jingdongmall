package com.jingdong.mall.service;

import com.jingdong.mall.model.dto.request.OrderCreateFromCartRequest;
import com.jingdong.mall.model.dto.response.OrderCreateResponse;

/**
 * 订单服务接口
 */
public interface OrderService {

    /**
     * 批量创建订单（从购物车）
     */
    OrderCreateResponse createOrderFromCart(Long userId, OrderCreateFromCartRequest request);
}