package com.jingdong.mall.service;

import com.jingdong.mall.model.dto.request.ProductCategoryAddRequest;
import com.jingdong.mall.model.dto.request.ProductCategoryUpdateRequest;
import com.jingdong.mall.model.dto.response.ProductCategoryAddResponse;
import com.jingdong.mall.model.dto.response.ProductCategoryUpdateResponse;

/**
 * 商品分类服务接口
 */
public interface ProductCategoryService {

    /**
     * 新增商品分类
     * @param request 新增分类请求参数
     * @return 新增分类响应（含ID）
     */
    ProductCategoryAddResponse addCategory(ProductCategoryAddRequest request);

    /**
     * 更新商品分类
     */
    ProductCategoryUpdateResponse updateCategory(ProductCategoryUpdateRequest request);
}