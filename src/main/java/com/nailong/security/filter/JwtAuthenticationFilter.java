package com.nailong.security.filter;

import com.nailong.common.constant.RedisKey;
import com.nailong.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * @brief JWT 认证过滤器
 * @details 校验 Access Token 类型与 Redis 会话后写入 SecurityContext
 * @author Nailong
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String HEADER_NAME = "Authorization";

    /**
     * @brief 执行 JWT 认证过滤
     * @param request     HTTP 请求
     * @param response    HTTP 响应
     * @param filterChain 过滤器链
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = getTokenFromRequest(request);

        if (StringUtils.hasText(token)) {
            try {
                if (jwtUtil.validateToken(token) && jwtUtil.isAccessToken(token)) {
                    Claims claims = jwtUtil.parseToken(token);
                    Long userId = claims.get("userId", Long.class);
                    String role = claims.get("role", String.class);
                    String username = claims.get("username", String.class);

                    String cached = redisTemplate.opsForValue().get(RedisKey.USER_TOKEN + userId);
                    if (!StringUtils.hasText(cached) || !cached.equals(token)) {
                        log.warn("JWT 会话无效或已登出: userId={}", userId);
                        SecurityContextHolder.clearContext();
                    } else {
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userId,
                                        null,
                                        Collections.singletonList(
                                                new SimpleGrantedAuthority("ROLE_" + role)
                                        )
                                );
                        authentication.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        request.setAttribute("userId", userId);
                        request.setAttribute("username", username);
                        request.setAttribute("role", role);
                        log.debug("JWT认证成功: userId={}", userId);
                    }
                } else {
                    SecurityContextHolder.clearContext();
                }
            } catch (Exception e) {
                log.warn("JWT认证失败: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * @brief 从请求头中提取 Token
     * @param request HTTP 请求
     * @return Token 字符串，不存在时返回 null
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HEADER_NAME);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}
