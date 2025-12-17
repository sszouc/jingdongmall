// src/main/java/com/jingdong/mall/mapper/TokenBlacklistMapper.java
package com.jingdong.mall.common.utils;

import com.jingdong.mall.model.entity.TokenBlacklist;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;

@Mapper
public interface TokenBlacklistMapper {

    @Insert("INSERT INTO token_blacklist (user_id, token_hash, expires_at, reason, created_time) " +
            "VALUES (#{userId}, #{tokenHash}, #{expiresAt}, #{reason}, #{createdTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TokenBlacklist tokenBlacklist);

    @Select("SELECT COUNT(*) FROM token_blacklist WHERE token_hash = #{tokenHash}")
    int existsByTokenHash(String tokenHash);

    @Delete("DELETE FROM token_blacklist WHERE expires_at < #{now}")
    int deleteExpiredTokens(LocalDateTime now);

    @Select("SELECT * FROM token_blacklist WHERE token_hash = #{tokenHash}")
    TokenBlacklist selectByTokenHash(String tokenHash);
}