// src/main/java/com/jingdong/mall/model/entity/User.java
package com.jingdong.mall.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（加密存储）
     */
    private String password;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 性别：0保密，1男，2女
     */
    private Integer gender;

    /**
     * 生日
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    /**
     * 状态（1正常，0禁用）
     */
    private Integer status;

    /**
     * 角色（0普通用户，1管理员）
     */
    private Integer role;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    // 常量定义
    public static class Status {
        public static final int DISABLED = 0;
        public static final int ENABLED = 1;
    }

    public static class Role {
        public static final int USER = 0;
        public static final int ADMIN = 1;
        public static final int SUPER_ADMIN = 2;
    }

    // 性别常量
    public static class Gender {
        public static final int UNKNOWN = 0;  // 保密/未知
        public static final int MALE = 1;     // 男
        public static final int FEMALE = 2;   // 女
    }
}