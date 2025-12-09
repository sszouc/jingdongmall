package com.jingdong.mall.provider;

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
}