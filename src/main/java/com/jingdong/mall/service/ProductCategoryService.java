package com.jingdong.mall.service;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.mapper.ProductCategoryMapper;
import com.jingdong.mall.model.dto.request.ProductCategoryAddRequest;
import com.jingdong.mall.model.dto.request.ProductCategoryUpdateRequest;
import com.jingdong.mall.model.dto.response.CategoryTreeResponse;
import com.jingdong.mall.model.dto.response.ProductCategoryAddResponse;
import com.jingdong.mall.model.dto.response.ProductCategoryUpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 删除商品分类
     * @param id 分类ID
     */
    void deleteCategory(Integer id);

    /**
     * 获取分类树（一级分类列表）
     */
    CategoryTreeResponse getCategoryTree();
}
