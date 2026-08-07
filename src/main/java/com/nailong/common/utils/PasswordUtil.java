package com.nailong.common.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @brief 密码加密工具类
 * @details 提供 BCrypt 加密、校验及密码强度验证
 * @author Nailong
 */
public class PasswordUtil {
    
    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();
    
    /**
     * @brief 加密明文密码
     * @param rawPassword 明文密码
     * @return BCrypt 加密后的密码
     */
    public static String encode(String rawPassword) {
        return ENCODER.encode(rawPassword);
    }
    
    /**
     * @brief 验证明文密码与加密密码是否匹配
     * @param rawPassword     明文密码
     * @param encodedPassword 加密后的密码
     * @return 匹配返回 true，否则 false
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return ENCODER.matches(rawPassword, encodedPassword);
    }
    
    /**
     * @brief 生成随机密码
     * @param length 密码长度
     * @return 随机密码字符串
     */
    public static String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        java.util.Random random = new java.util.Random();
        
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return sb.toString();
    }
    
    /**
     * @brief 验证密码强度
     * @details 规则：至少 8 位，包含大小写字母和数字
     * @param password 待验证的密码
     * @return 符合强度要求返回 true，否则 false
     */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
        }
        
        return hasUpper && hasLower && hasDigit;
    }
}
