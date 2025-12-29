package com.jingdong.mall.provider;

import org.apache.ibatis.jdbc.SQL;

/**
 * 分类树动态SQL提供者
 */
public class ProductCategoryTreeProvider {

    /**
     * 查询所有启用的一级分类（parent_id=0）
     */
    public String selectAllActiveCategories() {
        return new SQL() {{
            SELECT("id, name, sub_title, theme_color");
            FROM("product_category");
            WHERE("is_active = 1");
            WHERE("parent_id = 0"); // 只查一级分类，符合前端平行展示需求
            ORDER_BY("sort_order ASC, created_time DESC");
        }}.toString();
    }
}