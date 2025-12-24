package com.jingdong.mall.provider;

import com.jingdong.mall.model.dto.request.AdminUserListRequest;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

/**
 * 用户动态SQL提供者
 */
public class UserSqlProvider {

    /**
     * 构建分页查询用户列表的SQL
     */
    public String selectUserList(AdminUserListRequest request) {
        return new SQL() {{
            SELECT("id, username, phone, email, avatar, status, gender, birthday, created_time, updated_time");
            FROM("user");

            // 关键词搜索（用户名、手机号、邮箱）
            if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
                WHERE("(username LIKE CONCAT('%', #{request.keyword}, '%') OR " +
                        "phone LIKE CONCAT('%', #{request.keyword}, '%') OR " +
                        "email LIKE CONCAT('%', #{request.keyword}, '%'))");
            }

            // 状态筛选
            if (request.getStatus() != null) {
                WHERE("status = #{request.status}");
            }

            ORDER_BY("id ASC");
        }}.toString();
    }

    /**
     * 构建查询用户总数的SQL
     */
    public String countUserList(AdminUserListRequest request) {
        return new SQL() {{
            SELECT("COUNT(*)");
            FROM("user");

            // 关键词搜索（用户名、手机号、邮箱）
            if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
                WHERE("(username LIKE CONCAT('%', #{request.keyword}, '%') OR " +
                        "phone LIKE CONCAT('%', #{request.keyword}, '%') OR " +
                        "email LIKE CONCAT('%', #{request.keyword}, '%'))");
            }

            // 状态筛选
            if (request.getStatus() != null) {
                WHERE("status = #{request.status}");
            }
        }}.toString();
    }

    /**
     * 添加分页限制的方法
     */
    public String selectUserListWithPage(AdminUserListRequest request) {
        String sql = selectUserList(request);
        if (request.getPageSize() != null && request.getPage() != null) {
            int offset = request.getOffset();
            sql += " LIMIT " + request.getPageSize() + " OFFSET " + offset;
        }
        return sql;
    }

    /**
     * 更新用户状态和封禁时间
     */
    public String updateUserStatusAndBanTime(Map<String, Object> params) {
        Long id = (Long) params.get("id");
        Integer status = (Integer) params.get("status");
        java.time.LocalDateTime bannedStartTime = (java.time.LocalDateTime) params.get("bannedStartTime");
        java.time.LocalDateTime bannedEndTime = (java.time.LocalDateTime) params.get("bannedEndTime");

        SQL sql = new SQL();
        sql.UPDATE("user");

        if (status != null) {
            sql.SET("status = #{status}");
        }

        if (bannedStartTime != null) {
            sql.SET("banned_start_time = #{bannedStartTime}");
        } else {
            // 如果传入null，则设置为null
            sql.SET("banned_start_time = null");
        }

        if (bannedEndTime != null) {
            sql.SET("banned_end_time = #{bannedEndTime}");
        } else {
            sql.SET("banned_end_time = null");
        }

        // 总是更新updated_time
        sql.SET("updated_time = NOW()");
        sql.WHERE("id = #{id}");

        return sql.toString();
    }
}