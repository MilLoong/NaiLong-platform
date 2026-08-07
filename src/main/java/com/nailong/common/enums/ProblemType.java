package com.nailong.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @brief 题目类型枚举
 * @author Nailong
 */
@Getter
@AllArgsConstructor
public enum ProblemType {
    
    CHOICE("CHOICE", "选择题"),
    FLAG("FLAG", "Flag题"),
    FILE("FILE", "附件题"),
    CODE("CODE", "编程题");
    
    private final String code;
    private final String desc;
}