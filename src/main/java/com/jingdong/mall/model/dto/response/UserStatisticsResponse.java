package com.jingdong.mall.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户统计响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatisticsResponse {

    /**
     * 总用户数
     */
    private Integer totalUsers;

    /**
     * 活跃用户数（未封禁）
     */
    private Integer activeUsers;

    /**
     * 今日新注册用户数
     */
    private Integer newUsersToday;

    /**
     * 本月新注册用户数
     */
    private Integer newUsersThisMonth;

    /**
     * 被封禁用户数
     */
    private Integer disabledUsers;

    /**
     * 性别分布
     */
    private GenderDistribution genderDistribution;

    /**
     * 年龄分布
     */
    private AgeDistribution ageDistribution;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GenderDistribution {
        private Integer male;     // 男
        private Integer female;   // 女
        private Integer unknown;  // 未知
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AgeDistribution {
        private Integer under18;  // 18岁以下
        private Integer age18to25; // 18-25岁
        private Integer age26to35; // 26-35岁
        private Integer age36to45; // 36-45岁
        private Integer over45;   // 45岁以上
    }
}