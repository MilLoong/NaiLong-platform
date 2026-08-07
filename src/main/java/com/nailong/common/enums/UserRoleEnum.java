package com.nailong.common.enums;

/**
 * @brief 用户角色枚举（含编码）
 * @author Nailong
 */
public enum UserRoleEnum {
    
    /**
     * 普通用户
     */
    USER(0, "user"),
    
    /**
     * 管理员
     */
    ADMIN(1, "admin"),
    
    /**
     * 超级管理员
     */
    SUPER_ADMIN(2, "superadmin");
    
    private final int code;
    private final String role;
    
    UserRoleEnum(int code, String role) {
        this.code = code;
        this.role = role;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getRole() {
        return role;
    }
    
    /**
     * @brief 根据角色名获取枚举值
     * @param role 角色名
     * @return 匹配的枚举，未找到则返回 USER
     */
    public static UserRoleEnum getByRole(String role) {
        for (UserRoleEnum value : UserRoleEnum.values()) {
            if (value.role.equals(role)) {
                return value;
            }
        }
        return USER;
    }
    
    /**
     * @brief 根据编码获取枚举值
     * @param code 角色编码
     * @return 匹配的枚举，未找到则返回 USER
     */
    public static UserRoleEnum getByCode(int code) {
        for (UserRoleEnum value : UserRoleEnum.values()) {
            if (value.code == code) {
                return value;
            }
        }
        return USER;
    }
}