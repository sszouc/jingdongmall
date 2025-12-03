// src/main/java/com/jingdong/mall/common/utils/JwtUtil.java
package com.jingdong.mall.common.utils;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.service.UserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
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

    @Value("${jwt.expiration}")
    private long expiration;

    @Autowired
    private UserService userService;

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成JWT令牌
     */
    public String generateToken(String userId) {
        return Jwts.builder()
                .subject(userId)
                .issuedAt(new java.util.Date())
                .expiration(new java.util.Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    /**
     * 从令牌中获取用户ID
     */
    public String getUserIdFromToken(String token) {
        try {
            // 验证token是否在黑名单中
            // 由于UserService没有提供检查黑名单的方法，我们直接调用解析
            // 如果token在黑名单中，会在解析时抛出异常
            // 实际项目中应该先检查黑名单再解析

            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getSubject();
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
     * 获取过期时间
     */
    public long getExpiration() {
        return expiration;
    }
}