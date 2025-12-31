package com.jingdong.mall.provider;

import com.jingdong.mall.model.dto.request.CouponListRequest;
import org.apache.ibatis.jdbc.SQL;

public class CouponSqlProvider {

    public String selectCouponList(CouponListRequest request) {
        SQL sql = new SQL();
        sql.SELECT("c.id, c.name, c.type, c.value, c.min_spend as minSpend, c.start_time as startTime, c.end_time as endTime, c.status, c.total_count as totalCount, c.used_count as usedCount, c.created_time as createTime");
        sql.FROM("coupon c");

        // name模糊搜索
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            sql.WHERE("c.name LIKE CONCAT('%', #{request.name}, '%')");
        }

        // type筛选
        if (request.getType() != null) {
            sql.WHERE("c.type = #{request.type}");
        }

        // status筛选
        if (request.getStatus() != null) {
            sql.WHERE("c.status = #{request.status}");
        }

        // 默认按创建时间倒序
        sql.ORDER_BY("c.created_time DESC");

        if (request.getPage() != null && request.getPageSize() != null) {
            int offset = (request.getPage() - 1) * request.getPageSize();
            return sql.toString() + " LIMIT " + request.getPageSize() + " OFFSET " + offset;
        }

        return sql.toString();
    }

    public String countCouponList(CouponListRequest request) {
        SQL sql = new SQL();
        sql.SELECT("COUNT(*)");
        sql.FROM("coupon c");

        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            sql.WHERE("c.name LIKE CONCAT('%', #{request.name}, '%')");
        }

        if (request.getType() != null) {
            sql.WHERE("c.type = #{request.type}");
        }

        if (request.getStatus() != null) {
            sql.WHERE("c.status = #{request.status}");
        }

        return sql.toString();
    }
}

