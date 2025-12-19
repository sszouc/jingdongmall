// src/main/java/com/jingdong/mall/common/utils/JwtUtil.java
package com.jingdong.mall.common.utils;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.model.entity.TokenBlacklist;
import com.jingdong.mall.model.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

/**
 * JWT工具类
 */
@Slf4j
@Component
public class JwtUtil {


    @Autowired
    private TokenBlacklistMapper tokenBlacklistMapper;

    // Jwt密钥存储在环境变量中
    @Value("${jwt.secret}")
    private String secret;

    /**
     * -- GETTER --
     * 获取过期时间
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
    public Integer getUserRoleFromToken(String token) {
        Claims claims = parseTokenClaims(token);
        return claims.get("role", Integer.class);
    }

    /**
     * 计算token的SHA256哈希值
     */
    public String calculateTokenHash(String token) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(token.getBytes(StandardCharsets.UTF_8));

        // 将字节数组转换为十六进制字符串
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }

    /**
     * 把token加入黑名单
     * @param token  token本身
     * @param reason 加入黑名单的原因，LOGOUT登出、PWD_CHANGE密码修改、BAN被封禁
     */
    public void blackListToken(String token, String reason) throws NoSuchAlgorithmException {

        String tokenHash = calculateTokenHash(token);

        // 检查token是否已经在黑名单中
        if (tokenBlacklistMapper.existsByTokenHash(tokenHash) > 0) {
            log.warn("Token already in blacklist: hash={}", tokenHash);
            return ;
        }


        String userId = getUserIdFromToken(token);
        TokenBlacklist blacklistRecord = new TokenBlacklist();
        blacklistRecord.setUserId(Long.parseLong(userId));
        blacklistRecord.setTokenHash(tokenHash);
        blacklistRecord.setExpiresAt(LocalDateTime.now().plusDays(30));

        blacklistRecord.setReason(reason);

        blacklistRecord.setCreatedTime(LocalDateTime.now());
        // 保存到数据库
        int result = tokenBlacklistMapper.insert(blacklistRecord);

        if (result > 0) {
            log.info("Token added to blacklist: userId={}, reason=LOGOUT", userId);
        }
    }


}