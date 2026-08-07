package com.nailong.common.utils;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

/**
 * @brief 随机工具类
 * @details 提供随机数、随机字符串等生成功能
 * @author Nailong
 */
public class RandomUtil {
    
    private static final Random RANDOM = new SecureRandom();
    private static final String DIGITS = "0123456789";
    private static final String LETTERS_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String LETTERS_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String SPECIAL_CHARS = "!@#$%^&*()_+-=[]{}|;:,.<>?";
    private static final String ALL_CHARS = DIGITS + LETTERS_LOWER + LETTERS_UPPER + SPECIAL_CHARS;
    private static final String ALL_CHARS_NO_SPECIAL = DIGITS + LETTERS_LOWER + LETTERS_UPPER;
    
    /**
     * @brief 生成指定范围内的随机整数
     * @param min 最小值（含）
     * @param max 最大值（含）
     * @return 随机整数
     */
    public static int randomInt(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("最小值不能大于最大值");
        }
        return RANDOM.nextInt(max - min + 1) + min;
    }
    
    /**
     * @brief 生成随机整数
     * @return 随机整数
     */
    public static int randomInt() {
        return RANDOM.nextInt();
    }
    
    /**
     * @brief 生成随机长整数
     * @return 随机长整数
     */
    public static long randomLong() {
        return RANDOM.nextLong();
    }
    
    /**
     * @brief 生成指定范围内的随机长整数
     * @param min 最小值（含）
     * @param max 最大值（含）
     * @return 随机长整数
     */
    public static long randomLong(long min, long max) {
        if (min > max) {
            throw new IllegalArgumentException("最小值不能大于最大值");
        }
        return min + (long) (RANDOM.nextDouble() * (max - min + 1));
    }
    
    /**
     * @brief 生成随机浮点数
     * @return 0.0 到 1.0 之间的随机浮点数
     */
    public static double randomDouble() {
        return RANDOM.nextDouble();
    }
    
    /**
     * @brief 生成指定范围内的随机浮点数
     * @param min 最小值（含）
     * @param max 最大值（不含）
     * @return 随机浮点数
     */
    public static double randomDouble(double min, double max) {
        if (min > max) {
            throw new IllegalArgumentException("最小值不能大于最大值");
        }
        return min + RANDOM.nextDouble() * (max - min);
    }
    
    /**
     * @brief 生成随机布尔值
     * @return 随机布尔值
     */
    public static boolean randomBoolean() {
        return RANDOM.nextBoolean();
    }
    
    /**
     * @brief 生成指定长度的随机数字字符串
     * @param length 字符串长度
     * @return 随机数字字符串
     */
    public static String randomDigits(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("长度必须大于0");
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        }
        return sb.toString();
    }
    
    /**
     * @brief 生成指定长度的随机小写字母字符串
     * @param length 字符串长度
     * @return 随机小写字母字符串
     */
    public static String randomLettersLower(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("长度必须大于0");
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(LETTERS_LOWER.charAt(RANDOM.nextInt(LETTERS_LOWER.length())));
        }
        return sb.toString();
    }
    
    /**
     * @brief 生成指定长度的随机大写字母字符串
     * @param length 字符串长度
     * @return 随机大写字母字符串
     */
    public static String randomLettersUpper(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("长度必须大于0");
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(LETTERS_UPPER.charAt(RANDOM.nextInt(LETTERS_UPPER.length())));
        }
        return sb.toString();
    }
    
    /**
     * @brief 生成指定长度的随机字母字符串（大小写混合）
     * @param length 字符串长度
     * @return 随机字母字符串
     */
    public static String randomLetters(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("长度必须大于0");
        }
        StringBuilder sb = new StringBuilder(length);
        String letters = LETTERS_LOWER + LETTERS_UPPER;
        for (int i = 0; i < length; i++) {
            sb.append(letters.charAt(RANDOM.nextInt(letters.length())));
        }
        return sb.toString();
    }
    
    /**
     * @brief 生成指定长度的随机字符串（字母和数字混合，无特殊字符）
     * @param length 字符串长度
     * @return 随机字符串
     */
    public static String randomString(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("长度必须大于0");
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALL_CHARS_NO_SPECIAL.charAt(RANDOM.nextInt(ALL_CHARS_NO_SPECIAL.length())));
        }
        return sb.toString();
    }
    
    /**
     * @brief 生成指定长度的随机字符串（包含特殊字符）
     * @param length 字符串长度
     * @return 随机复杂字符串
     */
    public static String randomComplexString(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("长度必须大于0");
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALL_CHARS.charAt(RANDOM.nextInt(ALL_CHARS.length())));
        }
        return sb.toString();
    }
    
    /**
     * @brief 生成随机 UUID 字符串（无连字符）
     * @return UUID 字符串
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * @brief 生成随机数字验证码
     * @param length 验证码长度
     * @return 数字验证码
     */
    public static String generateCode(int length) {
        return randomDigits(length);
    }
    
    /**
     * @brief 从数组中随机选择一个元素
     * @param array 候选数组
     * @param <T>   元素类型
     * @return 随机选中的元素
     */
    public static <T> T randomElement(T[] array) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException("数组不能为空");
        }
        return array[RANDOM.nextInt(array.length)];
    }
    
    /**
     * @brief 随机打乱数组顺序
     * @param array 待打乱的数组
     * @param <T>   元素类型
     */
    public static <T> void shuffleArray(T[] array) {
        if (array == null || array.length <= 1) {
            return;
        }
        for (int i = array.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            // 交换元素
            T temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }
    
    /**
     * @brief 生成随机颜色代码（16 进制）
     * @return 颜色代码，如 #a1b2c3
     */
    public static String randomColorCode() {
        return String.format("#%02x%02x%02x", 
                RANDOM.nextInt(256), 
                RANDOM.nextInt(256), 
                RANDOM.nextInt(256));
    }
    
    /**
     * @brief 生成指定范围内的随机时间戳
     * @param startTime 开始时间戳（毫秒）
     * @param endTime   结束时间戳（毫秒）
     * @return 随机时间戳
     */
    public static long randomTimestamp(long startTime, long endTime) {
        if (startTime > endTime) {
            throw new IllegalArgumentException("开始时间不能大于结束时间");
        }
        return randomLong(startTime, endTime);
    }
    
    /**
     * @brief 获取安全随机数生成器
     * @return SecureRandom 实例
     */
    public static SecureRandom getSecureRandom() {
        return (SecureRandom) RANDOM;
    }
}
