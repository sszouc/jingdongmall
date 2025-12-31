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
        sql.SELECT("id"); // 修改：只查询商品ID
        sql.FROM("product");
        sql.WHERE("category_id = #{id}");
        sql.WHERE("is_active = 1");
        return sql.toString();
    }

    /**
     * 直接删除分类
     */
    public String deleteCategory(Integer id) {
        SQL sql = new SQL();
        sql.DELETE_FROM("product_category");  // 修改：使用DELETE而不是UPDATE
        sql.WHERE("id = #{id}");
        // 删除 is_active = 1 的条件，因为DELETE会直接删除记录
        return sql.toString();
    }
}