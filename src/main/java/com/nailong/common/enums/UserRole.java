package com.nailong.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @brief 用户角色枚举
 * @author Nailong
 */
@Getter
@AllArgsConstructor
public enum UserRole {
    
    USER("USER", "普通用户"),
    ADMIN("ADMIN", "管理员");
    
    private final String code;
    private final String desc;
}