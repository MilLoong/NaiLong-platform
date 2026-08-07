package com.nailong.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @brief 认证相关异常类
 * @details 用于处理用户认证过程中的各种异常情况
 * @author Nailong
 */
@Getter
public class AuthException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus status;

    /**
     * @brief 构造认证异常
     * @param message   错误消息
     * @param errorCode 错误代码
     * @param status    HTTP 状态码
     */
    public AuthException(String message, String errorCode, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }

    /**
     * @brief 构造认证异常（默认 UNAUTHORIZED 状态）
     * @param message   错误消息
     * @param errorCode 错误代码
     */
    public AuthException(String message, String errorCode) {
        this(message, errorCode, HttpStatus.UNAUTHORIZED);
    }

    /**
     * @brief 构造认证异常（使用默认错误代码）
     * @param message 错误消息
     */
    public AuthException(String message) {
        this(message, "AUTH_ERROR", HttpStatus.UNAUTHORIZED);
    }

    /**
     * @brief 创建认证失败异常
     * @param message 错误消息
     * @return AuthException 实例
     */
    public static AuthException authenticationFailed(String message) {
        return new AuthException(message, "AUTH_FAILED", HttpStatus.UNAUTHORIZED);
    }

    /**
     * @brief 创建无效令牌异常
     * @param message 错误消息
     * @return AuthException 实例
     */
    public static AuthException invalidToken(String message) {
        return new AuthException(message, "INVALID_TOKEN", HttpStatus.UNAUTHORIZED);
    }

    /**
     * @brief 创建令牌过期异常
     * @return AuthException 实例
     */
    public static AuthException tokenExpired() {
        return new AuthException("Token has expired", "TOKEN_EXPIRED", HttpStatus.UNAUTHORIZED);
    }

    /**
     * @brief 创建权限不足异常
     * @return AuthException 实例
     */
    public static AuthException insufficientPermissions() {
        return new AuthException("Insufficient permissions", "INSUFFICIENT_PERMISSIONS", HttpStatus.FORBIDDEN);
    }

    /**
     * @brief 创建用户未激活异常
     * @return AuthException 实例
     */
    public static AuthException userNotActivated() {
        return new AuthException("User account is not activated", "USER_NOT_ACTIVATED", HttpStatus.FORBIDDEN);
    }

    /**
     * @brief 创建用户已禁用异常
     * @return AuthException 实例
     */
    public static AuthException userDisabled() {
        return new AuthException("User account is disabled", "USER_DISABLED", HttpStatus.FORBIDDEN);
    }
}