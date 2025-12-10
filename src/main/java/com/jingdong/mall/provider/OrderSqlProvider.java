package com.jingdong.mall.provider;

import com.jingdong.mall.model.dto.request.OrderListRequest;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;

/**
 * 订单动态SQL提供者
 */
public class OrderSqlProvider {

    /**
     * 构建订单列表查询SQL
     */
    public String selectOrderList(OrderListRequest request, Long userId) {
        SQL sql = new SQL();
        sql.SELECT("id, order_sn, total_amount, pay_amount, status, created_time, shipping_fee, discount_amount");
        sql.FROM("`order`");
        sql.WHERE("user_id = #{userId}");

        // 订单状态筛选
        if (request.getStatus() != null) {
            sql.WHERE("status = #{request.status}");
        }

        // 时间范围筛选
        if (request.getStartDate() != null) {
            sql.WHERE("DATE(created_time) >= #{request.startDate}");
        }
        if (request.getEndDate() != null) {
            sql.WHERE("DATE(created_time) <= #{request.endDate}");
        }

        // 按创建时间倒序排列
        sql.ORDER_BY("created_time DESC");

        // 分页
        if (request.getPageSize() != null && request.getPage() != null) {
            int offset = request.getOffset();
            return sql.toString() + " LIMIT " + request.getPageSize() + " OFFSET " + offset;
        }

        return sql.toString();
    }

    /**
     * 构建订单总数统计SQL
     */
    public String countOrderList(OrderListRequest request, Long userId) {
        SQL sql = new SQL();
        sql.SELECT("COUNT(*)");
        sql.FROM("`order`");
        sql.WHERE("user_id = #{userId}");

        // 订单状态筛选
        if (request.getStatus() != null) {
            sql.WHERE("status = #{request.status}");
        }

        // 时间范围筛选
        if (request.getStartDate() != null) {
            sql.WHERE("DATE(created_time) >= #{request.startDate}");
        }
        if (request.getEndDate() != null) {
            sql.WHERE("DATE(created_time) <= #{request.endDate}");
        }

        return sql.toString();
    }

    /**
     * 批量查询订单项（用于预览）
     */
    public String selectPreviewItemsByOrderIds(List<Long> orderIds) {
        SQL sql = new SQL();
        sql.SELECT("order_id, product_name, main_image, quantity");
        sql.FROM("order_item");

        // 构建IN条件
        if (orderIds != null && !orderIds.isEmpty()) {
            StringBuilder ids = new StringBuilder();
            for (int i = 0; i < orderIds.size(); i++) {
                ids.append("#{orderIds[").append(i).append("]}");
                if (i < orderIds.size() - 1) {
                    ids.append(", ");
                }
            }
            sql.WHERE("order_id IN (" + ids + ")");
        }

        sql.ORDER_BY("order_id, id ASC");

        return sql.toString();
    }

    /**
     * 统计每个订单的商品种类数
     */
    public String countItemsByOrderIds(List<Long> orderIds) {
        SQL sql = new SQL();
        sql.SELECT("order_id, COUNT(*) as item_count");
        sql.FROM("order_item");

        // 构建IN条件
        if (orderIds != null && !orderIds.isEmpty()) {
            StringBuilder ids = new StringBuilder();
            for (int i = 0; i < orderIds.size(); i++) {
                ids.append("#{orderIds[").append(i).append("]}");
                if (i < orderIds.size() - 1) {
                    ids.append(", ");
                }
            }
            sql.WHERE("order_id IN (" + ids + ")");
        }

        sql.GROUP_BY("order_id");

        return sql.toString();
    }
}