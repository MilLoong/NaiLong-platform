package com.nailong.service.impl;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nailong.service.ICacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @brief 缓存服务实现
 * @details 基于 Redis 提供通用缓存读写、计数与过期管理
 * @author Nailong
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CacheServiceImpl implements ICacheService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final long DEFAULT_EXPIRE_TIME = 3600;

    /**
     * @brief 设置缓存（指定过期时间）
     * @param key      缓存键
     * @param value    缓存值（序列化为 JSON）
     * @param timeout  过期时长
     * @param timeUnit 时间单位
     */
    @Override
    public void set(String key, Object value, long timeout, TimeUnit timeUnit) {
        try {
            String valueStr = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, valueStr, timeout, timeUnit);
        } catch (Exception e) {
            log.error("设置缓存失败: key={}, error={}", key, e.getMessage());
            throw new RuntimeException("设置缓存失败", e);
        }
    }

    /**
     * @brief 设置缓存（默认 1 小时过期）
     * @param key   缓存键
     * @param value 缓存值
     */
    @Override
    public void set(String key, Object value) {
        set(key, value, DEFAULT_EXPIRE_TIME, TimeUnit.SECONDS);
    }

    /**
     * @brief 仅当键不存在时设置缓存
     * @param key      缓存键
     * @param value    缓存值
     * @param timeout  过期时长
     * @param timeUnit 时间单位
     * @return 是否设置成功
     */
    @Override
    public boolean setIfAbsent(String key, Object value, long timeout, TimeUnit timeUnit) {
        try {
            String valueStr = objectMapper.writeValueAsString(value);
            return redisTemplate.opsForValue().setIfAbsent(key, valueStr, timeout, timeUnit);
        } catch (Exception e) {
            log.error("设置缓存（仅当键不存在时）失败: key={}, error={}", key, e.getMessage());
            return false;
        }
    }

    /**
     * @brief 获取缓存并反序列化
     * @param key   缓存键
     * @param clazz 目标类型
     * @return 缓存对象，不存在或反序列化失败时返回 null
     * @details 反序列化失败时删除该键，避免重复读取脏数据
     */
    @Override
    public <T> T get(String key, Class<T> clazz) {
        try {
            String valueStr = redisTemplate.opsForValue().get(key);
            if (StrUtil.isEmpty(valueStr)) {
                return null;
            }
            return objectMapper.readValue(valueStr, clazz);
        } catch (Exception e) {
            log.error("获取缓存失败: key={}, error={}", key, e.getMessage());
            redisTemplate.delete(key);
            return null;
        }
    }

    /**
     * @brief 删除单个缓存键
     * @param key 缓存键
     */
    @Override
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("删除缓存失败: key={}, error={}", key, e.getMessage());
        }
    }

    /**
     * @brief 批量删除缓存键
     * @param keys 缓存键数组
     */
    @Override
    public void deleteBatch(String... keys) {
        if (keys == null || keys.length == 0) {
            return;
        }
        try {
            Set<String> keySet = new HashSet<>();
            for (String key : keys) {
                if (StrUtil.isNotEmpty(key)) {
                    keySet.add(key);
                }
            }
            if (!keySet.isEmpty()) {
                redisTemplate.delete(keySet);
            }
        } catch (Exception e) {
            log.error("批量删除缓存失败: error={}", e.getMessage());
        }
    }

    /**
     * @brief 按前缀删除缓存
     * @param prefix 键前缀
     */
    @Override
    public void deleteByPrefix(String prefix) {
        if (StrUtil.isEmpty(prefix)) {
            return;
        }
        try {
            Set<String> keys = redisTemplate.keys(prefix + "*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        } catch (Exception e) {
            log.error("删除前缀匹配的缓存失败: prefix={}, error={}", prefix, e.getMessage());
        }
    }

    /**
     * @brief 检查缓存键是否存在
     * @param key 缓存键
     * @return 是否存在
     */
    @Override
    public boolean exists(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("检查缓存是否存在失败: key={}, error={}", key, e.getMessage());
            return false;
        }
    }

    /**
     * @brief 递增计数器
     * @param key   缓存键
     * @param delta 增量
     * @return 递增后的值
     */
    @Override
    public Long increment(String key, long delta) {
        try {
            return redisTemplate.opsForValue().increment(key, delta);
        } catch (Exception e) {
            log.error("增加计数器失败: key={}, delta={}, error={}", key, delta, e.getMessage());
            throw new RuntimeException("增加计数器失败", e);
        }
    }

    /**
     * @brief 递减计数器
     * @param key   缓存键
     * @param delta 减量
     * @return 递减后的值
     */
    @Override
    public Long decrement(String key, long delta) {
        try {
            return redisTemplate.opsForValue().decrement(key, delta);
        } catch (Exception e) {
            log.error("减少计数器失败: key={}, delta={}, error={}", key, delta, e.getMessage());
            throw new RuntimeException("减少计数器失败", e);
        }
    }

    /**
     * @brief 设置缓存过期时间
     * @param key      缓存键
     * @param timeout  过期时长
     * @param timeUnit 时间单位
     */
    @Override
    public void expire(String key, long timeout, TimeUnit timeUnit) {
        try {
            redisTemplate.expire(key, timeout, timeUnit);
        } catch (Exception e) {
            log.error("设置缓存过期时间失败: key={}, error={}", key, e.getMessage());
        }
    }
}
