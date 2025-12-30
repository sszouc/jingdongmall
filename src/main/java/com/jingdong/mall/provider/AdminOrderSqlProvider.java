package com.jingdong.mall.provider;

import com.jingdong.mall.model.dto.request.AdminOrderQueryRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.SQL;

@Slf4j
public class AdminOrderSqlProvider {

    public String selectOrderList(AdminOrderQueryRequest query) {
        String sql = new SQL() {{
            SELECT("o.*");
            FROM("`order` o");

            // 动态条件
            if (query.getStatus() != null) {
                WHERE("o.status = #{query.status}");
            }
            if (query.getOrderSn() != null && !query.getOrderSn().isEmpty()) {
                WHERE("o.order_sn LIKE CONCAT('%', #{query.orderSn}, '%')");
            }
            if (query.getUserId() != null) {
                WHERE("o.user_id = #{query.userId}");
            }
            if (query.getPhone() != null && !query.getPhone().isEmpty()) {
                WHERE("o.receiver_phone LIKE CONCAT('%', #{query.phone}, '%')");
            }
            if (query.getStartTime() != null) {
                WHERE("o.created_time >= #{query.startTime}");
            }
            if (query.getEndTime() != null) {
                WHERE("o.created_time <= #{query.endTime}");
            }

            // 排序
            ORDER_BY("o.created_time DESC");
        }}.toString();

        // 添加分页
        if (query.getPage() != null && query.getPageSize() != null) {
            int offset = (query.getPage() - 1) * query.getPageSize();
            sql += " LIMIT " + offset + ", " + query.getPageSize();
        }

        log.info("生成的查询SQL: {}", sql);
        log.info("查询参数: userId={}", query.getUserId());

        return sql;
    }

    public String countOrderList(AdminOrderQueryRequest query) {
        String sql = new SQL() {{
            SELECT("COUNT(*)");
            FROM("`order` o");

            // 动态条件（与查询列表一致）
            if (query.getStatus() != null) {
                WHERE("o.status = #{query.status}");
            }
            if (query.getOrderSn() != null && !query.getOrderSn().isEmpty()) {
                WHERE("o.order_sn LIKE CONCAT('%', #{query.orderSn}, '%')");
            }
            if (query.getUserId() != null) {
                WHERE("o.user_id = #{query.userId}");
            }
            if (query.getPhone() != null && !query.getPhone().isEmpty()) {
                WHERE("o.receiver_phone LIKE CONCAT('%', #{query.phone}, '%')");
            }
            if (query.getStartTime() != null) {
                WHERE("o.created_time >= #{query.startTime}");
            }
            if (query.getEndTime() != null) {
                WHERE("o.created_time <= #{query.endTime}");
            }
        }}.toString();

        log.info("生成的查询SQL: {}", sql);
        log.info("查询参数: userId={}", query.getUserId());

        return sql;
    }
}