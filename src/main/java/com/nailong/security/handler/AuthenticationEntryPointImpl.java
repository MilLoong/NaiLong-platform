package com.nailong.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nailong.common.enums.ResultCode;
import com.nailong.common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @brief 认证失败处理器
 * @details 处理未登录或 Token 过期的情况，返回 401 JSON 响应
 * @author Nailong
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    
    private final ObjectMapper objectMapper;
    
    /**
     * @brief 处理认证失败
     * @param request       HTTP 请求
     * @param response      HTTP 响应
     * @param authException 认证异常
     * @throws IOException 写入响应失败时抛出
     */
    @Override
    public void commence(HttpServletRequest request,
                        HttpServletResponse response,
                        AuthenticationException authException) throws IOException {
        
        log.warn("认证失败: URI={}, Message={}", 
                request.getRequestURI(), authException.getMessage());
        
        Result<Void> result = Result.fail(
            ResultCode.UNAUTHORIZED.getCode(),
            ResultCode.UNAUTHORIZED.getMessage()
        );
        
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
