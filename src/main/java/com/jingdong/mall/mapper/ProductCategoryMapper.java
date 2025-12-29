package com.jingdong.mall.mapper;

import com.jingdong.mall.model.dto.request.ProductCategoryUpdateRequest;
import com.jingdong.mall.model.entity.ProductCategory;
import com.jingdong.mall.provider.ProductCategoryDeleteProvider;
import com.jingdong.mall.provider.ProductCategorySqlProvider;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ProductCategoryMapper {

    /**
     * 新增分类
     */
    @Insert("INSERT INTO product_category (name, sub_title, theme_color, parent_id, level, sort_order, is_active, created_time, updated_time) " +
            "VALUES (#{name}, #{subTitle}, #{themeColor}, 0, 1, 0, 1, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductCategory productCategory);

    /**
     * 检查分类名称是否已存在
     */
    @Select("SELECT COUNT(*) FROM product_category WHERE name = #{name} AND is_active = 1")
    int countByName(@Param("name") String name);

    // 新增更新分类相关方法
    @UpdateProvider(type = ProductCategorySqlProvider.class, method = "updateCategory")
    int updateCategory(ProductCategoryUpdateRequest request);

    @SelectProvider(type = ProductCategorySqlProvider.class, method = "countNameExcludeId")
    int countNameExcludeId(@Param("id") Integer id, @Param("name") String name);

    @SelectProvider(type = ProductCategorySqlProvider.class, method = "countById")
    int countById(@Param("id") Integer id);

    /**
     * 统计子分类数量
     */
    @SelectProvider(type = ProductCategoryDeleteProvider.class, method = "countSubCategories")
    int countSubCategories(@Param("id") Integer id);

    /**
     * 统计分类下商品数量
     */
    @SelectProvider(type = ProductCategoryDeleteProvider.class, method = "countProductsByCategory")
    int countProductsByCategory(@Param("id") Integer id);

    /**
     * 逻辑删除分类
     */
    @DeleteProvider(type = ProductCategoryDeleteProvider.class, method = "deleteCategory")
    int deleteCategory(@Param("id") Integer id);
}