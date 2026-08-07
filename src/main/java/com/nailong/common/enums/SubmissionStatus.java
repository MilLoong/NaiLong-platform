package com.nailong.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @brief 提交状态枚举
 * @author Nailong
 */
@Getter
@AllArgsConstructor
public enum SubmissionStatus {
    
    PENDING("PENDING", "待判题"),
    ACCEPTED("ACCEPTED", "通过"),
    WRONG_ANSWER("WRONG_ANSWER", "答案错误"),
    TIME_LIMIT_EXCEEDED("TIME_LIMIT_EXCEEDED", "超时"),
    MEMORY_LIMIT_EXCEEDED("MEMORY_LIMIT_EXCEEDED", "内存超限"),
    RUNTIME_ERROR("RUNTIME_ERROR", "运行错误"),
    COMPILE_ERROR("COMPILE_ERROR", "编译错误"),
    SYSTEM_ERROR("SYSTEM_ERROR", "系统错误");
    
    private final String code;
    private final String desc;
}