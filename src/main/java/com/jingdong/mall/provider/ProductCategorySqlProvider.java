package com.jingdong.mall.provider;

import com.jingdong.mall.model.dto.request.ProductCategoryUpdateRequest;
import org.apache.ibatis.jdbc.SQL;

/**
 * 商品分类动态SQL提供者
 */
public class ProductCategorySqlProvider {

    /**
     * 构建更新分类SQL
     */
    public String updateCategory(ProductCategoryUpdateRequest request) {
        SQL sql = new SQL();
        sql.UPDATE("product_category");

        // 动态设置更新字段
        if (request.getName() != null) {
            sql.SET("name = #{name}");
        }
        if (request.getSubTitle() != null) {
            sql.SET("sub_title = #{subTitle}");
        }
        if (request.getThemeColor() != null) {
            sql.SET("theme_color = #{themeColor}");
        }
        sql.SET("updated_time = NOW()");

        // 条件：分类ID和启用状态
        sql.WHERE("id = #{id}");
        sql.WHERE("is_active = 1");

        return sql.toString();
    }

    /**
     * 检查分类名称是否已存在（排除当前分类）
     */
    public String countNameExcludeId(Integer id, String name) {
        SQL sql = new SQL();
        sql.SELECT("COUNT(*)");
        sql.FROM("product_category");
        sql.WHERE("name = #{name}");
        sql.WHERE("id != #{id}");
        sql.WHERE("is_active = 1");
        return sql.toString();
    }

    /**
     * 检查分类是否存在
     */
    public String countById(Integer id) {
        SQL sql = new SQL();
        sql.SELECT("COUNT(*)");
        sql.FROM("product_category");
        sql.WHERE("id = #{id}");
        sql.WHERE("is_active = 1");
        return sql.toString();
    }
}