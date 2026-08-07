package com.nailong.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @brief 招新方向枚举
 * @author Nailong
 */
@Getter
@AllArgsConstructor
enum Direction {
    
    FRONTEND("frontend", "前端开发"),
    BACKEND("backend", "后端开发"),
    ANDROID("android", "Android开发"),
    DESIGN("design", "UI设计"),
    OPERATIONS("operations", "运营");
    
    private final String code;
    private final String desc;
}