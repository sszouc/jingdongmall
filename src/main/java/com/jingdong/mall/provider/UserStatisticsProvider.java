package com.jingdong.mall.provider;

import org.apache.ibatis.jdbc.SQL;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 用户统计动态SQL提供者
 */
public class UserStatisticsProvider {

    /**
     * 构建用户统计SQL
     */
    public String getUserStatistics(String startTime, String endTime) {
        SQL sql = new SQL();

        // 总用户数
        sql.SELECT("COUNT(*) as total_users");

        // 活跃用户数（status=1）
        sql.SELECT("SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) as active_users");

        // 被封禁用户数（status=0）
        sql.SELECT("SUM(CASE WHEN status = 0 THEN 1 ELSE 0 END) as disabled_users");

        // 今日新注册用户数
        sql.SELECT("SUM(CASE WHEN DATE(created_time) = CURDATE() THEN 1 ELSE 0 END) as new_users_today");

        // 本月新注册用户数
        sql.SELECT("SUM(CASE WHEN DATE(created_time) >= DATE_FORMAT(CURDATE(), '%Y-%m-01') THEN 1 ELSE 0 END) as new_users_this_month");

        // 性别分布
        sql.SELECT("SUM(CASE WHEN gender = 1 THEN 1 ELSE 0 END) as male_count");
        sql.SELECT("SUM(CASE WHEN gender = 2 THEN 1 ELSE 0 END) as female_count");
        sql.SELECT("SUM(CASE WHEN gender = 0 OR gender IS NULL THEN 1 ELSE 0 END) as unknown_count");

        // 年龄分布
        sql.SELECT("SUM(CASE WHEN birthday IS NOT NULL AND TIMESTAMPDIFF(YEAR, birthday, CURDATE()) < 18 THEN 1 ELSE 0 END) as under_18");
        sql.SELECT("SUM(CASE WHEN birthday IS NOT NULL AND TIMESTAMPDIFF(YEAR, birthday, CURDATE()) BETWEEN 18 AND 25 THEN 1 ELSE 0 END) as age_18_to_25");
        sql.SELECT("SUM(CASE WHEN birthday IS NOT NULL AND TIMESTAMPDIFF(YEAR, birthday, CURDATE()) BETWEEN 26 AND 35 THEN 1 ELSE 0 END) as age_26_to_35");
        sql.SELECT("SUM(CASE WHEN birthday IS NOT NULL AND TIMESTAMPDIFF(YEAR, birthday, CURDATE()) BETWEEN 36 AND 45 THEN 1 ELSE 0 END) as age_36_to_45");
        sql.SELECT("SUM(CASE WHEN birthday IS NOT NULL AND TIMESTAMPDIFF(YEAR, birthday, CURDATE()) > 45 THEN 1 ELSE 0 END) as over_45");

        sql.FROM("user");

        // 添加时间范围筛选（如果有）
        if (startTime != null && !startTime.trim().isEmpty()) {
            sql.WHERE("DATE(created_time) >= #{startTime}");
        }
        if (endTime != null && !endTime.trim().isEmpty()) {
            sql.WHERE("DATE(created_time) <= #{endTime}");
        }

        return sql.toString();
    }
}