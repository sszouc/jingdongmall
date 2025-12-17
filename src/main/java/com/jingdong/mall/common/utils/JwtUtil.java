// src/main/java/com/jingdong/mall/common/utils/JwtUtil.java
package com.jingdong.mall.common.utils;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.model.entity.User;
import com.jingdong.mall.service.UserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

/**
 * JWT工具类
 */
@Slf4j
@Component
public class JwtUtil {

    // Jwt密钥存储在环境变量中
    @Value("${jwt.secret}")
    private String secret;

    /**
     * -- GETTER --
     *  获取过期时间
     */
    @Getter
    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成JWT令牌
     */
    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("role", user.getRole())
                .issuedAt(new java.util.Date())
                .expiration(new java.util.Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    /**
     * 解析JWT令牌的Claims
     */
    private Claims parseTokenClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("Token已过期: {}, 过期时间: {}",
                    e.getClaims().getSubject(),
                    e.getClaims().getExpiration());
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED);
        } catch (MalformedJwtException e) {
            log.warn("Token格式错误: {}", e.getMessage());
            throw new BusinessException(ErrorCode.TOKEN_INVALID_FORMAT);
        } catch (io.jsonwebtoken.security.SecurityException e) {
            log.warn("Token签名无效: {}", e.getMessage());
            throw new BusinessException(ErrorCode.TOKEN_INVALID_SIGNATURE);
        } catch (JwtException e) {
            log.warn("Token验证失败: {}", e.getMessage());
            throw new BusinessException(ErrorCode.TOKEN_ERROR);
        }
    }

    /**
     * 从令牌中获取用户ID
     */
    public String getUserIdFromToken(String token) {
        Claims claims = parseTokenClaims(token);
        return claims.getSubject();
    }

    /**
     * 从令牌中获取用户角色
     */
    public String getUserRoleFromToken(String token) {
        Claims claims = parseTokenClaims(token);
        return claims.get("role", String.class);
    }

}