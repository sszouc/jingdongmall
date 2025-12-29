package com.jingdong.mall.provider;

import org.apache.ibatis.jdbc.SQL;

/**
 * 商品分类删除动态SQL提供者
 */
public class ProductCategoryDeleteProvider {

    /**
     * 统计分类下关联的子分类数量
     */
    public String countSubCategories(Integer id) {
        SQL sql = new SQL();
        sql.SELECT("COUNT(*)");
        sql.FROM("product_category");
        sql.WHERE("parent_id = #{id}");
        sql.WHERE("is_active = 1");
        return sql.toString();
    }

    /**
     * 统计分类下关联的商品数量
     */
    public String countProductsByCategory(Integer id) {
        SQL sql = new SQL();
        sql.SELECT("COUNT(*)");
        sql.FROM("product");
        sql.WHERE("category_id = #{id}");
        sql.WHERE("is_active = 1");
        return sql.toString();
    }

    /**
     * 逻辑删除分类（更新is_active为0）
     */
    public String deleteCategory(Integer id) {
        SQL sql = new SQL();
        sql.UPDATE("product_category");
        sql.SET("is_active = 0");
        sql.SET("updated_time = NOW()");
        sql.WHERE("id = #{id}");
        sql.WHERE("is_active = 1");
        return sql.toString();
    }
}