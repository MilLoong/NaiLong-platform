package com.nailong.common.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @brief JWT 工具类
 * @details 负责 Access/Refresh Token 的签发、解析与校验
 * @author Nailong
 */
@Slf4j
@Component
public class JwtUtil {
    
    @Value("${nailong.jwt.secret}")
    private String secret;
    
    @Value("${nailong.jwt.access-token-expire}")
    private Long accessTokenExpire;
    
    @Value("${nailong.jwt.refresh-token-expire}")
    private Long refreshTokenExpire;
    
    @Value("${nailong.jwt.issuer}")
    private String issuer;
    
    /**
     * @brief 生成访问令牌
     * @param userId   用户 ID
     * @param username 用户名
     * @param role     角色
     * @return Access Token 字符串
     */
    public String generateAccessToken(Long userId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);
        claims.put("type", "access");
        
        return createToken(claims, accessTokenExpire);
    }
    
    /**
     * @brief 生成刷新令牌
     * @param userId 用户 ID
     * @return Refresh Token 字符串
     */
    public String generateRefreshToken(Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("type", "refresh");
        
        return createToken(claims, refreshTokenExpire);
    }
    
    /**
     * @brief 根据声明创建 Token
     * @param claims     自定义声明
     * @param expireTime 过期时间（秒）
     * @return JWT 字符串
     */
    private String createToken(Map<String, Object> claims, Long expireTime) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expireTime * 1000);
        
        return Jwts.builder()
                .setClaims(claims)
                .setIssuer(issuer)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * @brief 解析 Token
     * @param token JWT 字符串
     * @return 声明载荷
     * @throws RuntimeException Token 无效、过期或签名校验失败时抛出
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("Token已过期: {}", e.getMessage());
            throw new RuntimeException("Token已过期");
        } catch (UnsupportedJwtException e) {
            log.error("不支持的Token: {}", e.getMessage());
            throw new RuntimeException("不支持的Token");
        } catch (MalformedJwtException e) {
            log.error("Token格式错误: {}", e.getMessage());
            throw new RuntimeException("Token格式错误");
        } catch (SecurityException e) {
            log.error("Token签名验证失败: {}", e.getMessage());
            throw new RuntimeException("Token签名验证失败");
        } catch (IllegalArgumentException e) {
            log.error("Token参数异常: {}", e.getMessage());
            throw new RuntimeException("Token参数异常");
        }
    }
    
    /**
     * @brief 从 Token 中获取用户 ID
     * @param token JWT 字符串
     * @return 用户 ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }
    
    /**
     * @brief 从 Token 中获取用户名
     * @param token JWT 字符串
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("username", String.class);
    }
    
    /**
     * @brief 从 Token 中获取角色
     * @param token JWT 字符串
     * @return 角色标识
     */
    public String getRoleFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("role", String.class);
    }
    
    /**
     * @brief 校验 Token 是否有效
     * @param token JWT 字符串
     * @return 有效返回 true，否则 false
     */
    public boolean validateToken(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                return false;
            }
            Claims claims = parseToken(token);
            return !isTokenExpired(claims);
        } catch (Exception e) {
            log.debug("Token验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * @brief 判断是否为 Access Token
     * @param token JWT 字符串
     * @return 类型为 access 时返回 true
     */
    public boolean isAccessToken(String token) {
        return "access".equals(getTokenType(token));
    }

    /**
     * @brief 判断是否为 Refresh Token
     * @param token JWT 字符串
     * @return 类型为 refresh 时返回 true
     */
    public boolean isRefreshToken(String token) {
        return "refresh".equals(getTokenType(token));
    }

    /**
     * @brief 获取 Token 类型声明
     * @param token JWT 字符串
     * @return type 声明值，不存在时返回 null
     */
    public String getTokenType(String token) {
        Claims claims = parseToken(token);
        return claims.get("type", String.class);
    }
    
    /**
     * @brief 判断 Token 是否已过期
     * @param claims Token 声明
     * @return 已过期返回 true
     */
    private boolean isTokenExpired(Claims claims) {
        Date expiration = claims.getExpiration();
        return expiration.before(new Date());
    }
    
    /**
     * @brief 获取签名密钥
     * @return HMAC 密钥
     */
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * @brief 使用 Refresh Token 签发新的 Refresh Token
     * @param refreshToken 原刷新令牌
     * @return 新的 Refresh Token
     */
    public String refreshToken(String refreshToken) {
        Claims claims = parseToken(refreshToken);
        Long userId = claims.get("userId", Long.class);
        return generateRefreshToken(userId);
    }
}
