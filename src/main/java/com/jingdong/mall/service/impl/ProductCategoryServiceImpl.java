package com.jingdong.mall.service.impl;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.mapper.ProductCategoryMapper;
import com.jingdong.mall.model.dto.request.ProductCategoryAddRequest;
import com.jingdong.mall.model.dto.request.ProductCategoryUpdateRequest;
import com.jingdong.mall.model.dto.response.ProductCategoryAddResponse;
import com.jingdong.mall.model.dto.response.ProductCategoryUpdateResponse;
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

    // 新增更新分类实现
    @Override
    @Transactional
    public ProductCategoryUpdateResponse updateCategory(ProductCategoryUpdateRequest request) {
        try {
            log.info("更新分类：id={}, name={}", request.getId(), request.getName());

            // 1. 检查分类是否存在
            int existCount = productCategoryMapper.countById(request.getId());
            if (existCount == 0) {
                throw new BusinessException(ErrorCode.PRODUCT_NOT_EXIST);
            }

            // 2. 检查分类名称是否重复（排除当前分类）
            int nameCount = productCategoryMapper.countNameExcludeId(request.getId(), request.getName());
            if (nameCount > 0) {
                throw new BusinessException(ErrorCode.CATEGORY_NAME_EXIST);
            }

            // 3. 执行更新操作
            int updateResult = productCategoryMapper.updateCategory(request);
            if (updateResult <= 0) {
                throw new BusinessException(ErrorCode.CATEGORY_CREATE_FAILED);
            }

            // 4. 构建响应结果
            return new ProductCategoryUpdateResponse(request.getId(), request.getName());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新分类异常", e);
            throw new BusinessException(ErrorCode.CATEGORY_CREATE_FAILED);
        }
    }
}