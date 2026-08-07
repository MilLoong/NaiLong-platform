package com.nailong.common.exception;

import com.nailong.common.enums.ResultCode;
import com.nailong.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

/**
 * @brief 全局异常处理器
 * @author Nailong
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @brief 处理业务异常
     * @param e       业务异常
     * @param request HTTP 请求
     * @return 统一错误响应
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("业务异常: {} - {}", request.getRequestURI(), e.getMessage());
        return Result.build(e.getCode(), e.getMessage(), null);
    }

    /**
     * @brief 处理参数校验异常
     * @param e 校验异常
     * @return 统一错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数校验失败: {}", message);
        return Result.build(ResultCode.BAD_REQUEST.getCode(), message, null);
    }

    /**
     * @brief 处理参数绑定异常
     * @param e 绑定异常
     * @return 统一错误响应
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数绑定失败: {}", message);
        return Result.build(ResultCode.BAD_REQUEST.getCode(), message, null);
    }

    /**
     * @brief 处理认证异常
     * @param e 认证异常
     * @return 401 未授权响应
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleAuthenticationException(AuthenticationException e) {
        log.warn("认证失败: {}", e.getMessage());
        return Result.build(ResultCode.UNAUTHORIZED.getCode(), "认证失败，请重新登录", null);
    }

    /**
     * @brief 处理权限不足异常
     * @param e 访问拒绝异常
     * @return 403 禁止访问响应
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("权限不足: {}", e.getMessage());
        return Result.build(ResultCode.FORBIDDEN.getCode(), "权限不足，拒绝访问", null);
    }

    /**
     * @brief 处理密码错误异常
     * @param e 凭证异常
     * @return 401 未授权响应
     */
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleBadCredentialsException(BadCredentialsException e) {
        log.warn("密码错误: {}", e.getMessage());
        return Result.build(ResultCode.INVALID_PASSWORD.getCode(), "用户名或密码错误", null);
    }

    /**
     * @brief 处理运行时异常
     * @param e       运行时异常
     * @param request HTTP 请求
     * @return 500 内部错误响应
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.error("运行时异常: {} - {}", request.getRequestURI(), e.getMessage(), e);
        return Result.build(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "系统内部错误，请联系管理员", null);
    }

    /**
     * @brief 处理其他未捕获异常
     * @param e       异常
     * @param request HTTP 请求
     * @return 500 内部错误响应
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常: {} - {}", request.getRequestURI(), e.getMessage(), e);
        return Result.build(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "系统异常，请联系管理员", null);
    }
}