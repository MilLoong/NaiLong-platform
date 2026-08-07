package com.nailong.common.utils;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @brief 邮件工具类
 * @details 提供验证码生成、邮箱格式校验及脱敏等功能
 * @author Nailong
 */
public class EmailUtil {

    /**
     * 邮箱格式正则表达式
     */
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    
    /**
     * @brief 生成随机数字验证码
     * @param length 验证码长度
     * @return 生成的验证码
     */
    public static String generateVerificationCode(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("验证码长度必须大于0");
        }
        
        String chars = "0123456789";
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            code.append(chars.charAt(index));
        }
        
        return code.toString();
    }
    
    /**
     * @brief 生成 6 位数字验证码
     * @return 6 位数字验证码
     */
    public static String generate6DigitCode() {
        return generateVerificationCode(6);
    }
    
    /**
     * @brief 验证邮箱格式是否正确
     * @param email 邮箱地址
     * @return 合法格式返回 true，否则 false
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }
    
    /**
     * @brief 获取邮箱的域名部分
     * @param email 邮箱地址
     * @return 域名部分
     */
    public static String getEmailDomain(String email) {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("无效的邮箱地址");
        }
        
        int atIndex = email.indexOf('@');
        return email.substring(atIndex + 1);
    }
    
    /**
     * @brief 隐藏邮箱的部分字符（保护隐私）
     * @param email 邮箱地址
     * @return 脱敏后的邮箱
     */
    public static String maskEmail(String email) {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("无效的邮箱地址");
        }
        
        int atIndex = email.indexOf('@');
        String username = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        
        // 根据用户名长度决定隐藏方式
        if (username.length() <= 2) {
            // 太短的用户名只显示第一个字符
            return username.charAt(0) + "*" + domain;
        } else if (username.length() <= 5) {
            // 中等长度显示首尾各一个字符
            return username.charAt(0) + "**" + username.charAt(username.length() - 1) + domain;
        } else {
            // 较长的用户名显示首尾两个字符
            int keepLength = 2;
            String masked = username.substring(0, keepLength) + 
                            "****" + 
                            username.substring(username.length() - keepLength);
            return masked + domain;
        }
    }
}
