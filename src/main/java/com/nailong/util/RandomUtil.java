package com.nailong.util;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * @brief 随机工具类
 * @details 提供生成随机数、随机字符串、随机验证码等功能
 * @author Nailong
 */
public class RandomUtil {

    private static final Random RANDOM = new SecureRandom();
    private static final String NUMBERS = "0123456789";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String SYMBOLS = "!@#$%^&*()_+-=[]{}|;:,.<>?";
    private static final String ALPHANUMERIC = NUMBERS + LOWERCASE + UPPERCASE;
    private static final String ALL_CHARS = ALPHANUMERIC + SYMBOLS;

    /**
     * @brief 生成指定范围内的随机整数
     * @param min 最小值（包含）
     * @param max 最大值（包含）
     * @return 随机整数
     */
    public static int getRandomInt(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("Min cannot be greater than Max");
        }
        return min + RANDOM.nextInt(max - min + 1);
    }

    /**
     * @brief 生成随机长整型数
     * @return 随机长整型数
     */
    public static long getRandomLong() {
        return RANDOM.nextLong();
    }

    /**
     * @brief 生成指定范围内的随机长整型数
     * @param min 最小值（包含）
     * @param max 最大值（包含）
     * @return 随机长整型数
     */
    public static long getRandomLong(long min, long max) {
        if (min > max) {
            throw new IllegalArgumentException("Min cannot be greater than Max");
        }
        return min + (long) (RANDOM.nextDouble() * (max - min + 1));
    }

    /**
     * @brief 生成随机布尔值
     * @return 随机布尔值
     */
    public static boolean getRandomBoolean() {
        return RANDOM.nextBoolean();
    }

    /**
     * @brief 生成指定长度的随机数字字符串
     * @param length 字符串长度
     * @return 随机数字字符串
     */
    public static String getRandomNumbers(int length) {
        return generateRandomString(NUMBERS, length);
    }

    /**
     * @brief 生成指定长度的随机验证码（数字）
     * @param length 验证码长度
     * @return 随机验证码
     */
    public static String generateVerificationCode(int length) {
        return getRandomNumbers(length);
    }

    /**
     * @brief 生成指定长度的随机字母字符串（小写）
     * @param length 字符串长度
     * @return 随机字母字符串
     */
    public static String getRandomLowercase(int length) {
        return generateRandomString(LOWERCASE, length);
    }

    /**
     * @brief 生成指定长度的随机字母字符串（大写）
     * @param length 字符串长度
     * @return 随机字母字符串
     */
    public static String getRandomUppercase(int length) {
        return generateRandomString(UPPERCASE, length);
    }

    /**
     * @brief 生成指定长度的随机字母数字字符串
     * @param length 字符串长度
     * @return 随机字母数字字符串
     */
    public static String getRandomAlphanumeric(int length) {
        return generateRandomString(ALPHANUMERIC, length);
    }

    /**
     * @brief 生成指定长度的随机字符串（包含特殊字符）
     * @param length 字符串长度
     * @return 随机字符串
     */
    public static String getRandomString(int length) {
        return generateRandomString(ALL_CHARS, length);
    }

    /**
     * @brief 生成安全的随机密码
     * @param length 密码长度
     * @return 随机密码
     */
    public static String generateSecurePassword(int length) {
        StringBuilder password = new StringBuilder(length);
        
        // 至少包含一个数字、一个小写字母、一个大写字母和一个特殊字符
        password.append(NUMBERS.charAt(RANDOM.nextInt(NUMBERS.length())));
        password.append(LOWERCASE.charAt(RANDOM.nextInt(LOWERCASE.length())));
        password.append(UPPERCASE.charAt(RANDOM.nextInt(UPPERCASE.length())));
        password.append(SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length())));
        
        // 填充剩余字符
        for (int i = 4; i < length; i++) {
            password.append(ALL_CHARS.charAt(RANDOM.nextInt(ALL_CHARS.length())));
        }
        
        // 打乱顺序
        return shuffleString(password.toString());
    }

    /**
     * @brief 生成UUID
     * @return UUID字符串
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * @brief 生成不带连字符的UUID
     * @return 不带连字符的UUID字符串
     */
    public static String generateUUIDWithoutHyphens() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * @brief 从列表中随机选择一个元素
     * @param list 源列表
     * @param <T> 元素类型
     * @return 随机选择的元素
     */
    public static <T> T getRandomElement(List<T> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("List cannot be null or empty");
        }
        return list.get(RANDOM.nextInt(list.size()));
    }

    /**
     * @brief 从数组中随机选择一个元素
     * @param array 源数组
     * @param <T> 元素类型
     * @return 随机选择的元素
     */
    public static <T> T getRandomElement(T[] array) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException("Array cannot be null or empty");
        }
        return array[RANDOM.nextInt(array.length)];
    }

    /**
     * @brief 从列表中随机选择指定数量的元素（不重复）
     * @param list 源列表
     * @param count 选择的元素数量
     * @param <T> 元素类型
     * @return 包含随机选择元素的列表
     */
    public static <T> List<T> getRandomElements(List<T> list, int count) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("List cannot be null or empty");
        }
        if (count <= 0 || count > list.size()) {
            throw new IllegalArgumentException("Count must be between 1 and list size");
        }
        
        List<T> copy = new ArrayList<>(list);
        Collections.shuffle(copy);
        return copy.subList(0, count);
    }

    /**
     * @brief 打乱字符串顺序
     * @param input 输入字符串
     * @return 顺序打乱后的字符串
     */
    public static String shuffleString(String input) {
        List<Character> characters = new ArrayList<>();
        for (char c : input.toCharArray()) {
            characters.add(c);
        }
        Collections.shuffle(characters);
        StringBuilder result = new StringBuilder();
        for (char c : characters) {
            result.append(c);
        }
        return result.toString();
    }

    /**
     * @brief 生成随机日期
     * @param startYear 起始年份
     * @param endYear 结束年份
     * @return 随机日期的毫秒时间戳
     */
    public static long getRandomDate(int startYear, int endYear) {
        long startMillis = getMillisFromYear(startYear);
        long endMillis = getMillisFromYear(endYear + 1);
        return startMillis + (long) (RANDOM.nextDouble() * (endMillis - startMillis));
    }

    /**
     * @brief 根据年份获取该年第一天的毫秒时间戳
     * @param year 年份
     * @return 毫秒时间戳
     */
    private static long getMillisFromYear(int year) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(year, 0, 1, 0, 0, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * @brief 生成指定概率的随机结果
     * @param probability 概率（0-1之间）
     * @return 是否满足概率条件
     */
    public static boolean randomByProbability(double probability) {
        if (probability < 0 || probability > 1) {
            throw new IllegalArgumentException("Probability must be between 0 and 1");
        }
        return RANDOM.nextDouble() < probability;
    }

    /**
     * @brief 根据指定字符集生成随机字符串
     * @param charSet 字符集
     * @param length 字符串长度
     * @return 随机字符串
     */
    private static String generateRandomString(String charSet, int length) {
        if (charSet == null || charSet.isEmpty()) {
            throw new IllegalArgumentException("Character set cannot be null or empty");
        }
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be greater than 0");
        }
        
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(charSet.charAt(RANDOM.nextInt(charSet.length())));
        }
        return sb.toString();
    }
}