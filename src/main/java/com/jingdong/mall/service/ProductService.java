package com.jingdong.mall.service;

import com.jingdong.mall.model.dto.request.ProductListRequest;
import com.jingdong.mall.model.dto.response.ProductDetailResponse;
import com.jingdong.mall.model.dto.response.ProductListResponse;

public interface ProductService {

    /**
     * 获取商品详情
     * @param productId 商品ID
     * @return 商品详情响应
     */
    ProductDetailResponse getProductDetail(Integer productId);

    /**
     * 获取商品列表（分页+条件查询）
     * @param request 查询请求参数
     * @return 商品列表响应
     */
    ProductListResponse getProductList(ProductListRequest request);
}