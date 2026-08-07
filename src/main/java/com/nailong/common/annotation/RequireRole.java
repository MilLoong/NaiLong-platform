package com.nailong.common.annotation;

import java.lang.annotation.*;

/**
 * @brief 角色权限注解
 * @details 用于标记需要特定角色才能访问的方法
 * @author Nailong
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRole {
    
    /** 需要的角色，默认为用户角色 */
    String[] value() default {"USER"};
    
    /** 权限验证失败时的提示消息 */
    String message() default "权限不足，需要管理员角色";
}
