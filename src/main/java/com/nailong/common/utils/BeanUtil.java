package com.nailong.common.utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @brief Bean 工具类
 * @details 用于对象属性复制及空值判断
 * @author Nailong
 */
public class BeanUtil {

    /**
     * @brief 将源对象的属性值复制到新创建的目标对象中
     * @param source      源对象
     * @param targetClass 目标对象的类
     * @param <T>         目标对象的类型
     * @return 复制后的目标对象，源对象为 null 时返回 null
     */
    public static <T> T copyProperties(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }

        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("对象属性复制失败", e);
        }
    }

    /**
     * @brief 将源对象的属性值复制到目标对象中
     * @param source 源对象
     * @param target 目标对象
     */
    public static void copyProperties(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }

        try {
            for (Field sourceField : getAllFields(source.getClass())) {
                sourceField.setAccessible(true);
                Object value = sourceField.get(source);
                if (value == null) {
                    continue;
                }

                for (Field targetField : getAllFields(target.getClass())) {
                    if (sourceField.getName().equals(targetField.getName()) &&
                            sourceField.getType().equals(targetField.getType())) {
                        targetField.setAccessible(true);
                        targetField.set(target, value);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("对象属性复制失败", e);
        }
    }

    /**
     * @brief 将源对象的属性值复制到目标对象中，忽略指定字段
     * @param source       源对象
     * @param target       目标对象
     * @param ignoreFields 忽略的字段名列表
     */
    public static void copyPropertiesIgnoreFields(Object source, Object target, String... ignoreFields) {
        if (source == null || target == null) {
            return;
        }

        Set<String> ignoreFieldSet = new HashSet<>(Arrays.asList(ignoreFields));

        try {
            for (Field sourceField : getAllFields(source.getClass())) {
                if (ignoreFieldSet.contains(sourceField.getName())) {
                    continue;
                }

                sourceField.setAccessible(true);
                Object value = sourceField.get(source);
                if (value == null) {
                    continue;
                }

                for (Field targetField : getAllFields(target.getClass())) {
                    if (sourceField.getName().equals(targetField.getName()) &&
                            sourceField.getType().equals(targetField.getType())) {
                        targetField.setAccessible(true);
                        targetField.set(target, value);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("对象属性复制失败", e);
        }
    }

    /**
     * @brief 获取类及其父类声明的全部字段
     * @param clazz 类型
     * @return 字段列表
     */
    private static Field[] getAllFields(Class<?> clazz) {
        java.util.List<Field> fields = new java.util.ArrayList<>();
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            fields.addAll(Arrays.asList(current.getDeclaredFields()));
            current = current.getSuperclass();
        }
        return fields.toArray(new Field[0]);
    }

    /**
     * @brief 检查对象是否为空
     * @param obj 要检查的对象
     * @return 为空返回 true，否则 false
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof String) {
            return ((String) obj).trim().isEmpty();
        }
        if (obj instanceof Iterable) {
            return !((Iterable<?>) obj).iterator().hasNext();
        }
        if (obj.getClass().isArray()) {
            return java.lang.reflect.Array.getLength(obj) == 0;
        }
        return false;
    }
}
