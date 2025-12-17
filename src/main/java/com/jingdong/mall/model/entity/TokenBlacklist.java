// src/main/java/com/jingdong/mall/model/entity/TokenBlacklist.java
package com.jingdong.mall.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Token黑名单实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenBlacklist {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private long userId;

    /**
     * 令牌哈希（完整token的SHA256）
     */
    private String tokenHash;

    /**
     * 令牌过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiresAt;

    /**
     * 加入黑名单原因：LOGOUT登出、PWD_CHANGE密码修改、BAN被封禁
     */
    private String reason;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    // 常量定义
    public static class Reason {
        public static final String LOGOUT = "LOGOUT";
        public static final String PWD_CHANGE = "PWD_CHANGE";
        public static final String BAN = "BAN";
    }
}