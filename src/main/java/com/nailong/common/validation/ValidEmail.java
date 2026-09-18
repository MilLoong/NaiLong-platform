package com.nailong.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * @brief 邮箱格式校验注解
 * @details 配合 EmailValidator 校验字段或参数的邮箱格式
 * @author Nailong
 */
@Documented
@Constraint(validatedBy = EmailValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEmail {
    
    String message() default "邮箱格式不正确";  ///< 校验失败时的提示信息
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
