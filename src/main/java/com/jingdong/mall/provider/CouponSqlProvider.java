package com.jingdong.mall.provider;

import com.jingdong.mall.model.dto.request.CouponListRequest;
import com.jingdong.mall.model.dto.request.CouponUpdateRequest;
import org.apache.ibatis.jdbc.SQL;

public class CouponSqlProvider {
    // 动态构建查询优惠券列表的SQL
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

    // 动态构建统计优惠券总数的SQL
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

    // 动态构建更新SQL
    public String updateCoupon(CouponUpdateRequest request) {
        SQL sql = new SQL();
        sql.UPDATE("coupon");

        if (request.getName() != null) {
            sql.SET("name = #{request.name}");
        }
        if (request.getType() != null) {
            sql.SET("type = #{request.type}");
        }
        if (request.getValue() != null) {
            sql.SET("value = #{request.value}");
        }
        if (request.getMinSpend() != null) {
            sql.SET("min_spend = #{request.minSpend}");
        }
        if (request.getStartTime() != null) {
            sql.SET("start_time = #{request.startTime}");
        }
        if (request.getEndTime() != null) {
            sql.SET("end_time = #{request.endTime}");
        }
        if (request.getTotalCount() != null) {
            sql.SET("total_count = #{request.totalCount}");
        }
        if (request.getStatus() != null) {
            sql.SET("status = #{request.status}");
        }

        //更新更新时间
        sql.SET("updated_time = NOW()");

        // 只更新目标id
        sql.WHERE("id = #{id}");

        // where id = #{id} 会在 mapper 中通过 @Param 自动追加
        return sql.toString();
    }
}
