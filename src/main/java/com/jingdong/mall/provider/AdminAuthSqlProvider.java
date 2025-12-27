package com.jingdong.mall.provider;

import org.apache.ibatis.jdbc.SQL;

/**
 * 管理员认证动态SQL提供者
 */
public class AdminAuthSqlProvider {

    /**
     * 根据手机号或邮箱查询管理员用户（包含角色验证）
     */
    public String selectAdminByAccount(String account) {
        SQL sql = new SQL();
        sql.SELECT("*");
        sql.FROM("user");
        sql.WHERE("(phone = #{account} OR email = #{account})");
        sql.WHERE("role IN (1, 2)"); // 只查询管理员用户

        return sql.toString();
    }

    /**
     * 根据手机号查询管理员用户（用于找回密码）
     */
    public String selectAdminByPhone(String phone) {
        SQL sql = new SQL();
        sql.SELECT("*");
        sql.FROM("user");
        sql.WHERE("phone = #{phone}");
        sql.WHERE("role IN (1, 2)"); // 只查询管理员用户

        return sql.toString();
    }
}