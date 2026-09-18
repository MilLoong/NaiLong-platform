package com.nailong.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @brief 权限校验注解
 * @details 用于标注需要进行权限校验的方法
 * @author Nailong
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {
    
    String role() default "";  ///< 要求的用户角色
    boolean admin() default false;  ///< 是否需要管理员权限
    boolean login() default true;  ///< 是否需要登录
}
