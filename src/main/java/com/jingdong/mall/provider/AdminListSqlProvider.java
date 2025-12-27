package com.jingdong.mall.provider;

import org.apache.ibatis.jdbc.SQL;

/**
 * 管理员列表动态SQL提供者
 */
public class AdminListSqlProvider {

    /**
     * 查询所有管理员（role=1）的SQL
     * 不包括超级管理员（role=2）
     */
    public String selectAllAdmins() {
        SQL sql = new SQL();
        sql.SELECT("id, username, phone, email, avatar, status, birthday, created_time, updated_time");
        sql.FROM("user");
        sql.WHERE("role = 1"); // 只查询普通管理员，不包括超级管理员
        sql.ORDER_BY("created_time DESC"); // 按创建时间倒序排列

        return sql.toString();
    }

    /**
     * 统计管理员数量（role=1）
     */
    public String countAdmins() {
        SQL sql = new SQL();
        sql.SELECT("COUNT(*)");
        sql.FROM("user");
        sql.WHERE("role = 1"); // 只统计普通管理员，不包括超级管理员

        return sql.toString();
    }
}