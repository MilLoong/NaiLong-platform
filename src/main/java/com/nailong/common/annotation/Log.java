package com.nailong.common.annotation;

import java.lang.annotation.*;

/**
 * @brief 操作日志注解
 * @details 用于标记需要记录操作日志的方法
 * @author Nailong
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    String operation() default "";  ///< 操作类型
    String remark() default "";  ///< 操作备注
    boolean recordParams() default true;  ///< 是否记录请求参数
    boolean recordResult() default true;  ///< 是否记录返回结果
    boolean recordError() default true;  ///< 是否记录异常信息
}
