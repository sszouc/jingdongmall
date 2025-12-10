// src/main/java/com/jingdong/mall/service/ShoppingCartService.java
package com.jingdong.mall.service;

import com.jingdong.mall.model.dto.request.CartAddRequest;
import com.jingdong.mall.model.dto.request.CartUpdateRequest;
import com.jingdong.mall.model.dto.response.CartItemResponse;
import com.jingdong.mall.model.dto.response.CartListResponse;

/**
 * 购物车服务接口
 */
public interface ShoppingCartService {
    /**
     * 获取当前用户购物车列表
     */
    CartListResponse getUserCartList(Long userId);

    // 新增方法：添加商品到购物车
    CartItemResponse addCart(Long userId, CartAddRequest request);

    /**
     * 更新购物车商品（数量/选中状态）
     * @param userId 用户ID（从Token解析）
     * @param request 更新请求参数
     * @return 更新后的购物车条目
     */
    CartItemResponse updateCart(Long userId, CartUpdateRequest request);
}