package com.nailong.common.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * @brief 统一响应结果类
 * @author Nailong
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 状态码
     */
    private Integer code;
    
    /**
     * 返回消息
     */
    private String message;
    
    /**
     * 返回数据
     */
    private T data;
    
    /**
     * 时间戳
     */
    private Long timestamp;
    
    public Result() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public Result(Integer code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
    
    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * @brief 成功响应（无数据）
     * @return 状态码 200 的成功结果
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功");
    }
    
    /**
     * @brief 成功响应（带数据）
     * @param data 响应数据
     * @return 状态码 200 的成功结果
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }
    
    /**
     * @brief 成功响应（自定义消息）
     * @param message 响应消息
     * @param data    响应数据
     * @return 状态码 200 的成功结果
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }
    
    /**
     * @brief 失败响应
     * @param message 错误消息
     * @return 状态码 500 的失败结果
     */
    public static <T> Result<T> fail(String message) {
        return new Result<>(500, message);
    }
    
    /**
     * @brief 失败响应（指定状态码）
     * @param code    状态码
     * @param message 错误消息
     * @return 自定义状态码的失败结果
     */
    public static <T> Result<T> fail(Integer code, String message) {
        return new Result<>(code, message);
    }
    
    /**
     * @brief 自定义响应
     * @param code    状态码
     * @param message 响应消息
     * @param data    响应数据
     * @return 自定义 Result 实例
     */
    public static <T> Result<T> build(Integer code, String message, T data) {
        return new Result<>(code, message, data);
    }
    
    /**
     * @brief 判断响应是否成功
     * @return 状态码为 200 时返回 true
     */
    public boolean isSuccess() {
        return this.code != null && this.code == 200;
    }
}