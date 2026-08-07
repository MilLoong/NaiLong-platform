package com.nailong.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @brief Spring 工具类
 * @details 用于在非 Spring 管理的类中获取 Spring 容器中的 Bean
 * @author Nailong
 */
@Component
public class SpringUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    /**
     * @brief 设置 ApplicationContext
     * @param applicationContext Spring 应用上下文
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringUtil.applicationContext == null) {
            SpringUtil.applicationContext = applicationContext;
        }
    }

    /**
     * @brief 获取 ApplicationContext
     * @return Spring 应用上下文
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * @brief 通过 Bean 名称获取 Bean
     * @param name Bean 名称
     * @return Bean 实例
     */
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    /**
     * @brief 通过类型获取 Bean
     * @param clazz Bean 类型
     * @param <T>   Bean 泛型类型
     * @return Bean 实例
     */
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    /**
     * @brief 通过名称和类型获取 Bean
     * @param name  Bean 名称
     * @param clazz Bean 类型
     * @param <T>   Bean 泛型类型
     * @return Bean 实例
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }
}
