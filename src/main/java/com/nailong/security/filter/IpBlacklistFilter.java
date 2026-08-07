package com.nailong.security.filter;

import com.nailong.common.utils.IpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nailong.common.enums.ResultCode;
import com.nailong.common.result.Result;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @brief IP 黑名单过滤器
 * @details 拦截已加入黑名单的 IP 请求
 * @author Nailong
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
@RequiredArgsConstructor
public class IpBlacklistFilter extends OncePerRequestFilter {

    private final IpBlacklistService ipBlacklistService;
    private final ObjectMapper objectMapper;

    /**
     * @brief 执行 IP 黑名单校验
     * @param request     HTTP 请求
     * @param response    HTTP 响应
     * @param filterChain 过滤器链
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String ip = IpUtil.getIpAddress(request);
        if (ip != null && ipBlacklistService.isBlacklisted(ip)) {
            log.warn("拦截黑名单 IP 请求: ip={}, uri={}", ip, request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(objectMapper.writeValueAsString(
                    Result.fail(ResultCode.IP_BLOCKED.getCode(), ResultCode.IP_BLOCKED.getMessage())));
            return;
        }
        filterChain.doFilter(request, response);
    }
}
