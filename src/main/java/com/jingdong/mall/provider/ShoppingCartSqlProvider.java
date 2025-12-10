package com.jingdong.mall.provider;

import com.jingdong.mall.model.entity.ShoppingCart;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;

/**
 * 购物车动态SQL提供者
 */
public class ShoppingCartSqlProvider {

    /**
     * 根据ID列表查询购物车项
     */
    public String selectByIds(List<Integer> cartItemIds, Long userId) {
        SQL sql = new SQL();
        sql.SELECT("*");
        sql.FROM("shopping_cart");
        sql.WHERE("user_id = #{userId}");

        if (cartItemIds != null && !cartItemIds.isEmpty()) {
            StringBuilder ids = new StringBuilder();
            for (int i = 0; i < cartItemIds.size(); i++) {
                ids.append("#{cartItemIds[").append(i).append("]}");
                if (i < cartItemIds.size() - 1) {
                    ids.append(", ");
                }
            }
            sql.WHERE("id IN (" + ids + ")");
        }

        return sql.toString();
    }

    /**
     * 批量删除购物车项
     */
    public String batchDelete(List<Integer> cartItemIds, Long userId) {
        SQL sql = new SQL();
        sql.DELETE_FROM("shopping_cart");
        sql.WHERE("user_id = #{userId}");

        if (cartItemIds != null && !cartItemIds.isEmpty()) {
            StringBuilder ids = new StringBuilder();
            for (int i = 0; i < cartItemIds.size(); i++) {
                ids.append("#{cartItemIds[").append(i).append("]}");
                if (i < cartItemIds.size() - 1) {
                    ids.append(", ");
                }
            }
            sql.WHERE("id IN (" + ids + ")");
        }


        return sql.toString();
    }

    /**
     * 动态构建购物车更新SQL
     * 只更新count（数量）和selected（选中状态）中非空的字段
     */
    public String updateCartItem(ShoppingCart shoppingCart) {
        SQL sql = new SQL();
        sql.UPDATE("shopping_cart");

        // 动态添加更新字段：数量非空则更新数量
        if (shoppingCart.getQuantity() != null) {
            sql.SET("quantity = #{quantity}");
        }
        // 动态添加更新字段：选中状态非空则更新选中状态
        if (shoppingCart.getSelected() != null) {
            sql.SET("selected = #{selected}");
        }
        // 每次更新必更更新时间
        sql.SET("updated_time = NOW()");

        // 条件：购物车ID和用户ID（确保只能更新自己的购物车）
        sql.WHERE("id = #{id}");
        sql.WHERE("user_id = #{userId}");
        // 只更新有效条目（数量>0）
        sql.WHERE("quantity > 0");

        return sql.toString();
    }

    /**
     * 批量更新购物车选中状态的动态SQL
     */
    public String batchUpdateSelectedStatus(List<Integer> cartItemIds, Long userId, Boolean selected) {
        SQL sql = new SQL();
        sql.UPDATE("shopping_cart");
        // 设置目标选中状态
        sql.SET("selected = #{selected}");
        // 每次更新必更更新时间
        sql.SET("updated_time = NOW()");

        // 条件：购物车ID列表、用户ID（确保只能更新自己的购物车）
        sql.WHERE("user_id = #{userId}");

        // 构建ID列表IN条件
        if (cartItemIds != null && !cartItemIds.isEmpty()) {
            StringBuilder ids = new StringBuilder();
            for (int i = 0; i < cartItemIds.size(); i++) {
                ids.append("#{cartItemIds[").append(i).append("]}");
                if (i < cartItemIds.size() - 1) {
                    ids.append(", ");
                }
            }
            sql.WHERE("id IN (" + ids + ")");
        }

        // 只更新有效条目（数量>0）
        sql.WHERE("quantity > 0");

        return sql.toString();
    }
}