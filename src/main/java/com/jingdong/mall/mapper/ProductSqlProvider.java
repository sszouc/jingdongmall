package com.jingdong.mall.mapper;

import com.jingdong.mall.model.dto.request.ProductListRequest;
import org.apache.ibatis.jdbc.SQL;

/**
 * 商品动态SQL提供者
 */
public class ProductSqlProvider {

    /**
     * 构建商品列表查询SQL
     */
    public String selectProductList(ProductListRequest request) {
        SQL sql = new SQL();

        // 使用反引号避免关键字冲突
        sql.SELECT("p.*");
        sql.SELECT("(SELECT MIN(price) FROM product_sku WHERE product_id = p.id AND is_active = 1) as min_price");
        sql.FROM("product p");
        sql.WHERE("p.is_active = 1");

        // 关键词搜索条件
        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
            sql.WHERE("p.name LIKE CONCAT('%', #{request.keyword}, '%')");
        }

        // 分类筛选条件
        if (request.getCategoryId() != null) {
            sql.WHERE("p.category_id = #{request.categoryId}");
        }

        // 价格区间筛选
        if (request.getMinPrice() != null) {
            sql.WHERE("(SELECT MIN(price) FROM product_sku WHERE product_id = p.id AND is_active = 1) >= #{request.minPrice}");
        }

        if (request.getMaxPrice() != null) {
            sql.WHERE("(SELECT MIN(price) FROM product_sku WHERE product_id = p.id AND is_active = 1) <= #{request.maxPrice}");
        }

        // 排序方式
        String sort = request.getSort();
        if (sort != null) {
            switch (sort) {
                case "price_asc":
                    sql.ORDER_BY("min_price ASC");
                    break;
                case "price_desc":
                    sql.ORDER_BY("min_price DESC");
                    break;
                case "sales_desc":
                    // 假设有一个方法可以获取销量，这里简化处理
                    sql.ORDER_BY("(SELECT SUM(sales_count) FROM product_sku WHERE product_id = p.id) DESC");
                    break;
                default: // created_desc
                    sql.ORDER_BY("p.created_time DESC");
                    break;
            }
        } else {
            sql.ORDER_BY("p.created_time DESC");
        }

        // 分页
        if (request.getPageSize() != null && request.getPage() != null) {
            int offset = (request.getPage() - 1) * request.getPageSize();
            return sql.toString() + " LIMIT " + request.getPageSize() + " OFFSET " + offset;
        }

        return sql.toString();
    }

    /**
     * 构建商品总数统计SQL
     */
    public String countProductList(ProductListRequest request) {
        SQL sql = new SQL();
        sql.SELECT("COUNT(*)");
        sql.FROM("product p");
        sql.WHERE("p.is_active = 1");

        // 关键词搜索条件
        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
            sql.WHERE("p.name LIKE CONCAT('%', #{request.keyword}, '%')");
        }

        // 分类筛选条件
        if (request.getCategoryId() != null) {
            sql.WHERE("p.category_id = #{request.categoryId}");
        }

        // 价格区间筛选
        if (request.getMinPrice() != null) {
            sql.WHERE("(SELECT MIN(price) FROM product_sku WHERE product_id = p.id AND is_active = 1) >= #{request.minPrice}");
        }

        if (request.getMaxPrice() != null) {
            sql.WHERE("(SELECT MIN(price) FROM product_sku WHERE product_id = p.id AND is_active = 1) <= #{request.maxPrice}");
        }

        return sql.toString();
    }
}