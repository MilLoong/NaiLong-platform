package com.nailong.common.exception;

import com.nailong.common.enums.ResultCode;
import lombok.Getter;

/**
 * @brief 业务异常类
 * @author Nailong
 */
@Getter
public class BusinessException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 错误码
     */
    private final Integer code;
    
    /**
     * 错误信息
     */
    private final String message;
    
    /**
     * @brief 构造业务异常（默认 500 状态码）
     * @param message 错误信息
     */
    public BusinessException(String message) {
        super(message);
        this.code = 500;
        this.message = message;
    }
    
    /**
     * @brief 构造业务异常（指定状态码）
     * @param code    错误码
     * @param message 错误信息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
    
    /**
     * @brief 根据 ResultCode 构造业务异常
     * @param resultCode 结果码枚举
     */
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }
    
    /**
     * @brief 根据 ResultCode 构造业务异常（自定义消息）
     * @param resultCode    结果码枚举
     * @param customMessage 自定义错误信息
     */
    public BusinessException(ResultCode resultCode, String customMessage) {
        super(customMessage);
        this.code = resultCode.getCode();
        this.message = customMessage;
    }
}