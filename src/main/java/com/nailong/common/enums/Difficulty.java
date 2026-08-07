package com.nailong.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @brief 题目难度枚举
 * @author Nailong
 */
@Getter
@AllArgsConstructor
enum Difficulty {
    
    EASY("EASY", "简单"),
    MEDIUM("MEDIUM", "中等"),
    HARD("HARD", "困难");
    
    private final String code;
    private final String desc;
}