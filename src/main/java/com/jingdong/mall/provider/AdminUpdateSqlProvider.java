package com.jingdong.mall.provider;

import com.jingdong.mall.model.dto.request.AdminUpdateRequest;
import org.apache.ibatis.jdbc.SQL;

import java.time.LocalDateTime;

/**
 * 管理员更新动态SQL提供者
 */
public class AdminUpdateSqlProvider {

    /**
     * 动态构建更新管理员信息的SQL
     */
    public String updateAdminInfo(AdminUpdateRequest request, Long userId, LocalDateTime updatedTime) {
        SQL sql = new SQL();
        sql.UPDATE("user");

        // 动态添加更新字段
        if (request.getAvatar() != null) {
            sql.SET("avatar = #{request.avatar}");
        }

        if (request.getEmail() != null) {
            sql.SET("email = #{request.email}");
        }

        if (request.getPhone() != null) {
            sql.SET("phone = #{request.phone}");
        }

        if (request.getUsername() != null) {
            sql.SET("username = #{request.username}");
        }

        if (request.getGender() != null) {
            sql.SET("gender = #{request.genderAsInt}");
        }

        if (request.getBirthday() != null) {
            sql.SET("birthday = #{request.birthday}");
        }

        // 总是更新更新时间
        sql.SET("updated_time = #{updatedTime}");

        // WHERE条件
        sql.WHERE("id = #{userId}");
        sql.WHERE("role IN (1, 2)"); // 只允许更新管理员
        sql.WHERE("status = 1"); // 只允许更新正常状态的用户

        return sql.toString();
    }
}