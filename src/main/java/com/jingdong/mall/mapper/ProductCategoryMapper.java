package com.jingdong.mall.mapper;

import com.jingdong.mall.model.entity.ProductCategory;
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
}