// src/main/java/com/jingdong/mall/service/ShoppingCartService.java
package com.jingdong.mall.service;

import com.jingdong.mall.model.dto.response.CartListResponse;

/**
 * 购物车服务接口
 */
public interface ShoppingCartService {
    /**
     * 获取当前用户购物车列表
     */
    CartListResponse getUserCartList(Long userId);
}