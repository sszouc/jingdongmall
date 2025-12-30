package com.jingdong.mall.service.impl;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.mapper.ProductCategoryMapper;
import com.jingdong.mall.model.dto.request.ProductCategoryAddRequest;
import com.jingdong.mall.model.dto.request.ProductCategoryUpdateRequest;
import com.jingdong.mall.model.dto.response.CategoryTreeResponse;
import com.jingdong.mall.model.dto.response.ProductCategoryAddResponse;
import com.jingdong.mall.model.dto.response.ProductCategoryUpdateResponse;
import com.jingdong.mall.model.entity.ProductCategory;
import com.jingdong.mall.service.ProductCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

        // 3. 构建分类实体（默认父级ID=24，层级=2，排序=0，状态=启用）
        ProductCategory category = new ProductCategory();
        category.setName(request.getName());
        category.setSubTitle(request.getSubTitle());
        category.setThemeColor(request.getThemeColor());
        category.setParentId(24); // 一级分类
        category.setLevel(2);
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
                throw new BusinessException(ErrorCode.CATEGORY_NOT_EXIST);
            }

            // 2. 检查分类名称是否重复（排除当前分类）
            int nameCount = productCategoryMapper.countNameExcludeId(request.getId(), request.getName());
            if (nameCount > 0) {
                throw new BusinessException(ErrorCode.CATEGORY_NAME_EXIST);
            }

            // 3. 执行更新操作
            int updateResult = productCategoryMapper.updateCategory(request);
            if (updateResult <= 0) {
                throw new BusinessException(ErrorCode.CATEGORY_UPDATE_FAILED);
            }

            // 4. 构建响应结果
            return new ProductCategoryUpdateResponse(request.getId(), request.getName());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新分类异常", e);
            throw new BusinessException(ErrorCode.CATEGORY_UPDATE_FAILED);
        }
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Integer id) {
        try {
            // 1. 验证分类是否存在（复用已有方法）
            if (productCategoryMapper.countById(id) == 0) {
                throw new BusinessException(ErrorCode.CATEGORY_NOT_EXIST);
            }

            // 2. 检查是否有关联子分类
            int subCategoryCount = productCategoryMapper.countSubCategories(id);
            if (subCategoryCount > 0) {
                throw new BusinessException(ErrorCode.CATEGORY_DELETE_FAILED, "该分类下存在子分类，无法删除");
            }

            // 3. 检查是否有关联商品
            int productCount = productCategoryMapper.countProductsByCategory(id);
            if (productCount > 0) {
                throw new BusinessException(ErrorCode.CATEGORY_DELETE_FAILED, "该分类下存在商品，无法删除");
            }

            // 4. 执行逻辑删除
            int result = productCategoryMapper.deleteCategory(id);
            if (result <= 0) {
                throw new BusinessException(ErrorCode.CATEGORY_DELETE_FAILED);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.CATEGORY_DELETE_FAILED);
        }
    }
    @Override
    public CategoryTreeResponse getCategoryTree() {
        try {
            log.info("开始查询分类树");
            // 查询所有启用的一级分类
            List<ProductCategory> categories = productCategoryMapper.selectAllActiveCategories();
            if (categories == null || categories.isEmpty()) {
                log.warn("未查询到有效分类");
                throw new BusinessException("暂无分类数据");
            }
            // 转换为响应DTO
            List<CategoryTreeResponse.CategoryItem> itemList = categories.stream()
                    .map(this::convertToCategoryItem)
                    .collect(Collectors.toList());
            log.info("分类树查询成功，共{}个分类", itemList.size());
            return new CategoryTreeResponse(itemList);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询分类树系统异常", e);
            throw new BusinessException(ErrorCode.CATEGORY_GET_EXIST);
        }
    }

    /**
     * 转换分类实体为响应项
     */
    private CategoryTreeResponse.CategoryItem convertToCategoryItem(ProductCategory category) {
        CategoryTreeResponse.CategoryItem item = new CategoryTreeResponse.CategoryItem();
        item.setId(String.valueOf(category.getId())); // 按OpenAPI要求返回字符串类型ID
        item.setName(category.getName());
        item.setSubTitle(category.getSubTitle());
        item.setThemeColor(category.getThemeColor());
        return item;
    }
}