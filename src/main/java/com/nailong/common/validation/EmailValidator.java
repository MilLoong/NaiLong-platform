package com.nailong.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * @brief 邮箱格式校验器
 * @details 实现 ValidEmail 注解的校验逻辑
 * @author Nailong
 */
public class EmailValidator implements ConstraintValidator<ValidEmail, String> {
    
    private static final String EMAIL_PATTERN = 
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    
    /**
     * @brief 初始化校验器
     * @param constraintAnnotation 注解实例
     */
    @Override
    public void initialize(ValidEmail constraintAnnotation) {
    }
    
    /**
     * @brief 校验邮箱格式
     * @param email   待校验邮箱
     * @param context 校验上下文
     * @return 格式合法返回 true，否则 false
     */
    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return pattern.matcher(email).matches();
    }
}
