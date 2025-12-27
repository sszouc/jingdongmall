package com.jingdong.mall.provider;

import org.apache.ibatis.jdbc.SQL;

/**
 * 管理员相关SQL提供者
 */
public class AdminSqlProvider {

    /**
     * 检查是否存在具有管理员角色的用户
     */
    public String checkAdminExists(String phone) {
        SQL sql = new SQL();
        sql.SELECT("COUNT(*)");
        sql.FROM("user");
        sql.WHERE("phone = #{phone}");
        sql.WHERE("role IN (1, 2)"); // 检查是否已经是管理员或超级管理员
        sql.WHERE("status = 1"); // 只检查状态正常的用户

        return sql.toString();
    }

    /**
     * 更新用户角色为管理员
     */
    public String updateUserToAdmin(Long userId) {
        SQL sql = new SQL();
        sql.UPDATE("user");
        sql.SET("role = 1"); // 管理员角色
        sql.SET("updated_time = NOW()");
        sql.WHERE("id = #{userId}");
        sql.WHERE("role = 0"); // 只能将普通用户升级为管理员
        sql.WHERE("status = 1"); // 只能升级状态正常的用户

        return sql.toString();
    }
}