// src/main/java/com/jingdong/mall/service/ShoppingCartService.java
package com.jingdong.mall.service;

import com.jingdong.mall.model.dto.request.*;
import com.jingdong.mall.model.dto.response.*;

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

    // 新增：批量删除购物车商品
    CartDeleteResponse deleteCartItems(Long userId, CartDeleteRequest request);

    // 新增：获取购物车商品总数量
    CartCountResponse getCartTotalCount(Long userId);

    /**
     * 批量更新购物车选中状态
     * @param userId 用户ID（从Token解析）
     * @param request 批量更新请求参数
     * @return 批量更新响应（包含成功更新数量）
     */
    CartBatchSelectResponse batchUpdateSelectedStatus(Long userId, CartBatchSelectRequest request);

    /**
     * 按SKU ID删除购物车商品
     * @param userId 用户ID（从Token解析）
     * @param request 按SKU删除请求参数
     * @return 删除结果响应（包含删除数量）
     */
    CartDeleteBySkuResponse deleteCartBySkuId(Long userId, CartDeleteBySkuRequest request);

    /**
     * 清空当前用户所有购物车条目
     * @param userId 用户ID（从Token解析）
     * @return 清空结果响应（包含成功清空数量）
     */
    CartClearResponse clearCart(Long userId);
}