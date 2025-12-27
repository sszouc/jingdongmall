package com.jingdong.mall.provider;

import org.apache.ibatis.jdbc.SQL;

import java.time.LocalDateTime;

/**
 * 管理员密码修改动态SQL提供者
 */
public class AdminPasswordSqlProvider {

    /**
     * 更新管理员密码的SQL
     */
    public String updateAdminPassword(Long userId, String newPassword, LocalDateTime updatedTime) {
        SQL sql = new SQL();
        sql.UPDATE("user");
        sql.SET("password = #{newPassword}");
        sql.SET("updated_time = #{updatedTime}");
        sql.WHERE("id = #{userId}");
        sql.WHERE("role IN (1, 2)"); // 只允许更新管理员
        sql.WHERE("status = 1"); // 只允许更新正常状态的用户

        return sql.toString();
    }

    /**
     * 查询管理员密码的SQL
     */
    public String selectAdminPassword(Long userId) {
        SQL sql = new SQL();
        sql.SELECT("password, role, status");
        sql.FROM("user");
        sql.WHERE("id = #{userId}");
        sql.WHERE("role IN (1, 2)"); // 只查询管理员

        return sql.toString();
    }
}