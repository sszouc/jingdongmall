package com.jingdong.mall.provider;

import com.jingdong.mall.model.dto.request.AdminUserListRequest;
import org.apache.ibatis.jdbc.SQL;

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

            ORDER_BY("created_time DESC");
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
}