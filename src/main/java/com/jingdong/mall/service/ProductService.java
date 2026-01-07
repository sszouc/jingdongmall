package com.jingdong.mall.service;

import com.jingdong.mall.model.dto.request.ProductAddRequest;
import com.jingdong.mall.model.dto.request.ProductListRequest;
import com.jingdong.mall.model.dto.request.ProductUpdateRequest;
import com.jingdong.mall.model.dto.request.SkuAddRequest;
import com.jingdong.mall.model.dto.response.ProductAddResponse;
import com.jingdong.mall.model.dto.response.ProductDetailResponse;
import com.jingdong.mall.model.dto.response.ProductListResponse;
import com.jingdong.mall.model.dto.response.SkuAddResponse;

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

    /**
     * 新增商品
     * @param request 新增商品请求参数
     * @return 新增商品响应
     */
    ProductAddResponse addProduct(ProductAddRequest request);

    /**
     * 更新商品（管理员）
     * @param id 商品ID
     * @param request 更新请求
     */
    void updateProduct(Integer id, ProductUpdateRequest request);

    /**
     * 删除商品（管理员）
     * @param id 商品ID
     */
    void deleteProduct(Integer id);

    /**
     * 批量上下架商品（管理员）
     * @param ids 商品ID列表
     * @param status 状态 0/1
     */
    void batchUpdateStatus(java.util.List<Integer> ids, Integer status);
}