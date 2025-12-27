package com.jingdong.mall.service.impl;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.mapper.ProductCategoryMapper;
import com.jingdong.mall.model.dto.request.ProductCategoryAddRequest;
import com.jingdong.mall.model.dto.response.ProductCategoryAddResponse;
import com.jingdong.mall.model.entity.ProductCategory;
import com.jingdong.mall.service.ProductCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

    @Autowired
    private ProductCategoryMapper productCategoryMapper;

    @Override
    @Transactional
    public ProductCategoryAddResponse addCategory(ProductCategoryAddRequest request) {
        // 1. 校验参数
        if (request == null) {
            throw new BusinessException(ErrorCode.CATEGORY_PARAM_ERROR);
        }

        // 2. 检查分类名称是否已存在
        int nameCount = productCategoryMapper.countByName(request.getName());
        if (nameCount > 0) {
            throw new BusinessException(ErrorCode.CATEGORY_NAME_EXIST);
        }

        // 3. 构建分类实体（默认父级ID=0，层级=1，排序=0，状态=启用）
        ProductCategory category = new ProductCategory();
        category.setName(request.getName());
        category.setSubTitle(request.getSubTitle());
        category.setThemeColor(request.getThemeColor());
        category.setParentId(0); // 一级分类
        category.setLevel(1);
        category.setSortOrder(0);
        category.setIsActive(1); // 启用状态

        // 4. 保存到数据库
        int result = productCategoryMapper.insert(category);
        if (result <= 0) {
            log.error("创建分类失败：name={}", request.getName());
            throw new BusinessException(ErrorCode.CATEGORY_CREATE_FAILED);
        }

        // 5. 构建响应
        ProductCategoryAddResponse response = new ProductCategoryAddResponse();
        response.setId(category.getId());
        return response;
    }
}