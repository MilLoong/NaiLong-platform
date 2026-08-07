package com.nailong.service;

import java.util.concurrent.TimeUnit;

/**
 * @brief 缓存服务接口
 * @author Nailong
 * @details 提供通用的 Redis 缓存操作方法
 */
public interface ICacheService {
    
    /**
     * @brief 设置缓存
     * @param key      缓存键
     * @param value    缓存值
     * @param timeout  超时时间
     * @param timeUnit 时间单位
     */
    void set(String key, Object value, long timeout, TimeUnit timeUnit);
    
    /**
     * @brief 设置缓存（默认超时时间为 1 小时）
     * @param key   缓存键
     * @param value 缓存值
     */
    void set(String key, Object value);
    
    /**
     * @brief 设置缓存（仅当键不存在时设置）
     * @param key      缓存键
     * @param value    缓存值
     * @param timeout  超时时间
     * @param timeUnit 时间单位
     * @return 是否设置成功
     */
    boolean setIfAbsent(String key, Object value, long timeout, TimeUnit timeUnit);
    
    /**
     * @brief 获取缓存
     * @param key   缓存键
     * @param clazz 值类型
     * @param <T>   泛型参数
     * @return 缓存值，不存在时返回 null
     */
    <T> T get(String key, Class<T> clazz);
    
    /**
     * @brief 删除缓存
     * @param key 缓存键
     */
    void delete(String key);
    
    /**
     * @brief 批量删除缓存
     * @param keys 缓存键数组
     */
    void deleteBatch(String... keys);
    
    /**
     * @brief 删除前缀匹配的缓存
     * @param prefix 缓存键前缀
     */
    void deleteByPrefix(String prefix);
    
    /**
     * @brief 缓存是否存在
     * @param key 缓存键
     * @return 是否存在
     */
    boolean exists(String key);
    
    /**
     * @brief 增加计数器
     * @param key   缓存键
     * @param delta 增量
     * @return 增加后的值
     */
    Long increment(String key, long delta);
    
    /**
     * @brief 减少计数器
     * @param key   缓存键
     * @param delta 减量
     * @return 减少后的值
     */
    Long decrement(String key, long delta);
    
    /**
     * @brief 设置过期时间
     * @param key      缓存键
     * @param timeout  超时时间
     * @param timeUnit 时间单位
     */
    void expire(String key, long timeout, TimeUnit timeUnit);
}
