package com.nailong.common.exception;

import com.nailong.common.enums.ResultCode;

/**
 * @brief 认证异常类
 * @details 用于处理身份验证相关的异常
 * @author Nailong
 */
public class AuthException extends RuntimeException {
    
    private final int errorCode;  ///< 错误码
    /**
     * @brief 构造认证异常（默认未授权错误码）
     * @param msg 错误消息
     */
    public AuthException(String msg) {
        super(msg);
        this.errorCode = ResultCode.UNAUTHORIZED.getCode();
    }
    
    /**
     * @brief 构造认证异常（指定错误码）
     * @param msg       错误消息
     * @param errorCode 错误码
     */
    public AuthException(String msg, int errorCode) {
        super(msg);
        this.errorCode = errorCode;
    }
    
    /**
     * @brief 构造认证异常（含原因）
     * @param msg   错误消息
     * @param cause 异常原因
     */
    public AuthException(String msg, Throwable cause) {
        super(msg, cause);
        this.errorCode = ResultCode.AUTHENTICATION_ERROR.getCode();
    }
    
    /**
     * @brief 构造认证异常（指定错误码与原因）
     * @param msg       错误消息
     * @param errorCode 错误码
     * @param cause     异常原因
     */
    public AuthException(String msg, int errorCode, Throwable cause) {
        super(msg, cause);
        this.errorCode = errorCode;
    }
    
    /**
     * @brief 根据 ResultCode 构造认证异常
     * @param resultCode 结果码枚举
     */
    public AuthException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.errorCode = resultCode.getCode();
    }
    
    /**
     * @brief 根据 ResultCode 构造认证异常（含原因）
     * @param resultCode 结果码枚举
     * @param cause      异常原因
     */
    public AuthException(ResultCode resultCode, Throwable cause) {
        super(resultCode.getMessage(), cause);
        this.errorCode = resultCode.getCode();
    }
    
    /**
     * @brief 获取错误码
     * @return 错误码
     */
    public int getErrorCode() {
        return errorCode;
    }
}
