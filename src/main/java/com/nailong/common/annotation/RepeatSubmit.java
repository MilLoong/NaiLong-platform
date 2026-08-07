package com.nailong.common.annotation;

import java.lang.annotation.*;

/**
 * @brief 防重复提交注解
 * @details 标注在方法上，限制同一操作在指定间隔内不可重复提交
 * @author Nailong
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RepeatSubmit {
    
    /** 间隔时间（秒），默认 5 秒 */
    int interval() default 5;
    
    /** 提示消息 */
    String message() default "操作过于频繁，请稍后再试";
}
