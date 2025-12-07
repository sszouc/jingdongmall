package com.jingdong.mall.service;

import com.jingdong.mall.model.dto.response.ProductDetailResponse;

public interface ProductService {

    /**
     * 获取商品详情
     * @param productId 商品ID
     * @return 商品详情响应
     */
    ProductDetailResponse getProductDetail(Integer productId);
}