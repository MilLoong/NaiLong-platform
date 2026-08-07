package com.nailong.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nailong.common.enums.ResultCode;
import com.nailong.common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @brief 权限不足处理器
 * @details 处理已认证但权限不足的情况，返回 403 JSON 响应
 * @author Nailong
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AccessDeniedHandlerImpl implements org.springframework.security.web.access.AccessDeniedHandler {
    
    private final ObjectMapper objectMapper;
    
    /**
     * @brief 处理权限不足
     * @param request               HTTP 请求
     * @param response              HTTP 响应
     * @param accessDeniedException 权限拒绝异常
     * @throws IOException 写入响应失败时抛出
     */
    @Override
    public void handle(HttpServletRequest request,
                      HttpServletResponse response,
                      org.springframework.security.access.AccessDeniedException accessDeniedException) 
            throws IOException {
        
        log.warn("权限不足: URI={}, Message={}", 
                request.getRequestURI(), accessDeniedException.getMessage());
        
        Result<Void> result = Result.fail(
            ResultCode.FORBIDDEN.getCode(),
            ResultCode.FORBIDDEN.getMessage()
        );
        
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
