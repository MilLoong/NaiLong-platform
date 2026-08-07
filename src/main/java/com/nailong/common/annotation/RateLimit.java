package com.nailong.common.annotation;

import java.lang.annotation.*;

/**
 * @brief 限流注解
 * @details 使用 Redis + Lua 脚本实现分布式限流
 * @author Nailong
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {
    
    /** 限流 QPS（每秒请求数） */
    int qps() default 10;
    
    /** 限流时间窗口（秒） */
    int timeout() default 1;
    
    /** 限流维度（IP/USER） */
    String type() default "IP";
}
