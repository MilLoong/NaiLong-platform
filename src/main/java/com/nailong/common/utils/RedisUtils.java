package com.nailong.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @brief Redis 工具类
 * @details 使用内存缓存模拟 Redis，避免连接问题
 * @author Nailong
 */
@Slf4j
@Component
public class RedisUtils {

    // 使用ConcurrentHashMap模拟Redis存储
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    private static class CacheEntry {
        private Object value;
        private long expireTime;

        public CacheEntry(Object value, long expireTime) {
            this.value = value;
            this.expireTime = expireTime;
        }

        public boolean isExpired() {
            return expireTime > 0 && System.currentTimeMillis() > expireTime;
        }

        public Object getValue() {
            return value;
        }
    }

    // 清理过期缓存的辅助方法
    private void removeExpired(String key) {
        CacheEntry entry = cache.get(key);
        if (entry != null && entry.isExpired()) {
            cache.remove(key);
        }
    }

    // ================ String类型操作 ================

    /**
     * @brief 设置缓存（永不过期）
     * @param key   缓存键
     * @param value 缓存值
     */
    public void set(String key, Object value) {
        cache.put(key, new CacheEntry(value, 0)); // 永不过期
    }

    /**
     * @brief 设置缓存并指定过期时间
     * @param key     缓存键
     * @param value   缓存值
     * @param timeout 过期时长
     * @param unit    时间单位
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        long expireTime = unit.toMillis(timeout) + System.currentTimeMillis();
        cache.put(key, new CacheEntry(value, expireTime));
    }

    /**
     * @brief 获取缓存
     * @param key   缓存键
     * @param clazz 期望的值类型
     * @param <T>   值泛型类型
     * @return 缓存值，不存在或类型不匹配时返回 null
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        removeExpired(key);
        CacheEntry entry = cache.get(key);
        if (entry == null) {
            return null;
        }
        Object value = entry.getValue();
        if (value == null) {
            return null;
        }
        if (clazz.isInstance(value)) {
            return (T) value;
        } else {
            log.warn("Cache value type mismatch for key '{}': expected {}, got {}", 
                     key, clazz.getName(), value.getClass().getName());
            return null;
        }
    }

    /**
     * @brief 获取 String 类型缓存
     * @param key 缓存键
     * @return 字符串值，不存在时返回 null
     */
    public String getString(String key) {
        return get(key, String.class);
    }

    /**
     * @brief 设置 String 类型缓存（永不过期）
     * @param key   缓存键
     * @param value 字符串值
     */
    public void setString(String key, String value) {
        set(key, value);
    }

    /**
     * @brief 设置 String 类型缓存并指定过期时间
     * @param key     缓存键
     * @param value   字符串值
     * @param timeout 过期时长
     * @param unit    时间单位
     */
    public void setString(String key, String value, long timeout, TimeUnit unit) {
        set(key, value, timeout, unit);
    }

    /**
     * @brief 递增缓存值
     * @param key   缓存键
     * @param delta 递增量
     * @return 递增后的值
     */
    public long increment(String key, long delta) {
        removeExpired(key);
        Long current = get(key, Long.class);
        if (current == null) {
            current = 0L;
        }
        long newValue = current + delta;
        set(key, newValue);
        return newValue;
    }

    // ================ Hash类型操作 ================

    /**
     * @brief 获取 Hash 字段值
     * @param key     缓存键
     * @param hashKey Hash 字段键
     * @param clazz   期望的值类型
     * @param <T>     值泛型类型
     * @return 字段值，不存在或类型不匹配时返回 null
     */
    @SuppressWarnings("unchecked")
    public <T> T hGet(String key, String hashKey, Class<T> clazz) {
        removeExpired(key);
        Map<String, Object> hashMap = get(key, Map.class);
        if (hashMap == null) {
            return null;
        }
        Object value = hashMap.get(hashKey);
        if (value == null) {
            return null;
        }
        if (clazz.isInstance(value)) {
            return (T) value;
        } else {
            log.warn("Cache hash value type mismatch for key '{}:{}, expected {}, got {}", 
                     key, hashKey, clazz.getName(), value.getClass().getName());
            return null;
        }
    }

    /**
     * @brief 获取整个 Hash 表
     * @param key   缓存键
     * @param clazz 期望的值类型
     * @param <T>   值泛型类型
     * @return Hash 表，不存在时返回空 Map
     */
    @SuppressWarnings("unchecked")
    public <T> Map<String, T> hGetAll(String key, Class<T> clazz) {
        removeExpired(key);
        Map<String, Object> rawMap = get(key, Map.class);
        if (rawMap == null || rawMap.isEmpty()) {
            return new HashMap<>();
        }
        
        Map<String, T> typedMap = new HashMap<>(rawMap.size());
        for (Map.Entry<String, Object> entry : rawMap.entrySet()) {
            Object valueObj = entry.getValue();
            
            if (valueObj != null) {
                if (clazz.isInstance(valueObj)) {
                    typedMap.put(entry.getKey(), (T) valueObj);
                } else {
                    log.warn("Cache hash value type mismatch for key '{}:{}': expected {}, got {}", 
                             key, entry.getKey(), clazz.getName(), valueObj.getClass().getName());
                }
            }
        }
        
        return typedMap;
    }

    // ================ List类型操作 ================

    /**
     * @brief 从 List 右侧插入元素
     * @param key   缓存键
     * @param value 要插入的元素
     */
    @SuppressWarnings("unchecked")
    public void lRightPush(String key, Object value) {
        removeExpired(key);
        List<Object> list = get(key, List.class);
        if (list == null) {
            list = new ArrayList<>();
            set(key, list);
        }
        list.add(value);
    }

    /**
     * @brief 获取 List 指定范围内的元素
     * @param key   缓存键
     * @param start 起始索引（含）
     * @param end   结束索引（含）
     * @param clazz 期望的元素类型
     * @param <T>   元素泛型类型
     * @return 范围内的元素列表
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> lRange(String key, long start, long end, Class<T> clazz) {
        removeExpired(key);
        List<Object> rawList = get(key, List.class);
        if (rawList == null || rawList.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 调整范围
        int actualStart = (int) Math.max(0, start);
        int actualEnd = (int) Math.min(rawList.size() - 1, end);
        
        if (actualStart > actualEnd) {
            return new ArrayList<>();
        }
        
        List<T> typedList = new ArrayList<>(actualEnd - actualStart + 1);
        for (int i = actualStart; i <= actualEnd; i++) {
            Object item = rawList.get(i);
            if (item != null) {
                if (clazz.isInstance(item)) {
                    typedList.add((T) item);
                } else {
                    log.warn("Cache list item type mismatch for key '{}': expected {}, got {}", 
                             key, clazz.getName(), item.getClass().getName());
                }
            }
        }
        
        return typedList;
    }

    // ================ Set类型操作 ================

    /**
     * @brief 获取 Set 所有元素
     * @param key   缓存键
     * @param clazz 期望的元素类型
     * @param <T>   元素泛型类型
     * @return Set 元素集合，不存在时返回空 Set
     */
    @SuppressWarnings("unchecked")
    public <T> Set<T> sMembers(String key, Class<T> clazz) {
        removeExpired(key);
        Set<Object> rawSet = get(key, Set.class);
        if (rawSet == null || rawSet.isEmpty()) {
            return new HashSet<>();
        }
        
        Set<T> typedSet = new HashSet<>(rawSet.size());
        for (Object item : rawSet) {
            if (item != null) {
                if (clazz.isInstance(item)) {
                    typedSet.add((T) item);
                } else {
                    log.warn("Cache set item type mismatch for key '{}': expected {}, got {}", 
                             key, clazz.getName(), item.getClass().getName());
                }
            }
        }
        
        return typedSet;
    }

    /**
     * @brief 判断元素是否在 Set 中
     * @param key   缓存键
     * @param value 待检查的元素
     * @return 存在返回 true，否则 false
     */
    @SuppressWarnings("unchecked")
    public boolean sIsMember(String key, Object value) {
        removeExpired(key);
        Set<Object> set = get(key, Set.class);
        return set != null && set.contains(value);
    }

    // ================ ZSet类型操作 ================

    /**
     * @brief 添加 ZSet 元素
     * @param key   缓存键
     * @param value 元素值
     * @param score 分数
     */
    @SuppressWarnings("unchecked")
    public void zAdd(String key, Object value, double score) {
        removeExpired(key);
        Map<Object, Double> zset = get(key, Map.class);
        if (zset == null) {
            zset = new HashMap<>();
            set(key, zset);
        }
        zset.put(value, score);
    }

    /**
     * @brief 获取 ZSet 指定范围的元素（升序）
     * @param key   缓存键
     * @param start 起始索引（含）
     * @param end   结束索引（含）
     * @param clazz 期望的元素类型
     * @param <T>   元素泛型类型
     * @return 范围内的元素集合
     */
    @SuppressWarnings("unchecked")
    public <T> Set<T> zRange(String key, long start, long end, Class<T> clazz) {
        return getSortedSetElements(key, start, end, clazz, true);
    }

    /**
     * @brief 获取 ZSet 指定范围的元素（降序）
     * @param key   缓存键
     * @param start 起始索引（含）
     * @param end   结束索引（含）
     * @param clazz 期望的元素类型
     * @param <T>   元素泛型类型
     * @return 范围内的元素集合
     */
    @SuppressWarnings("unchecked")
    public <T> Set<T> zReverseRange(String key, long start, long end, Class<T> clazz) {
        return getSortedSetElements(key, start, end, clazz, false);
    }

    @SuppressWarnings("unchecked")
    private <T> Set<T> getSortedSetElements(String key, long start, long end, Class<T> clazz, boolean ascending) {
        removeExpired(key);
        Map<Object, Double> zset = get(key, Map.class);
        if (zset == null || zset.isEmpty()) {
            return new HashSet<>();
        }
        
        // 将Map转换为List并排序
        List<Map.Entry<Object, Double>> sortedEntries = new ArrayList<>(zset.entrySet());
        sortedEntries.sort((e1, e2) -> {
            int result = Double.compare(e1.getValue(), e2.getValue());
            return ascending ? result : -result;
        });
        
        // 调整范围
        int actualStart = (int) Math.max(0, start);
        int actualEnd = (int) Math.min(sortedEntries.size() - 1, end);
        
        if (actualStart > actualEnd) {
            return new HashSet<>();
        }
        
        Set<T> typedSet = new HashSet<>(actualEnd - actualStart + 1);
        for (int i = actualStart; i <= actualEnd; i++) {
            Object item = sortedEntries.get(i).getKey();
            if (item != null) {
                if (clazz.isInstance(item)) {
                    typedSet.add((T) item);
                } else {
                    log.warn("Cache zset item type mismatch for key '{}': expected {}, got {}", 
                             key, clazz.getName(), item.getClass().getName());
                }
            }
        }
        
        return typedSet;
    }

    // ================ 通用操作 ================

    /**
     * @brief 判断 key 是否存在
     * @param key 缓存键
     * @return 存在返回 true，否则 false
     */
    public boolean hasKey(String key) {
        removeExpired(key);
        return cache.containsKey(key);
    }

    /**
     * @brief 删除 key
     * @param key 缓存键
     * @return 删除成功返回 true，否则 false
     */
    public boolean delete(String key) {
        return cache.remove(key) != null;
    }

    /**
     * @brief 设置 key 的过期时间
     * @param key     缓存键
     * @param timeout 过期时长
     * @param unit    时间单位
     * @return 设置成功返回 true，key 不存在时返回 false
     */
    public boolean expire(String key, long timeout, TimeUnit unit) {
        removeExpired(key);
        CacheEntry entry = cache.get(key);
        if (entry != null) {
            long expireTime = unit.toMillis(timeout) + System.currentTimeMillis();
            cache.put(key, new CacheEntry(entry.getValue(), expireTime));
            return true;
        }
        return false;
    }

    /**
     * @brief 获取 key 的剩余过期时间
     * @param key  缓存键
     * @param unit 时间单位
     * @return 剩余过期时间，永不过期返回 null，已过期返回 0
     */
    public Long getExpire(String key, TimeUnit unit) {
        removeExpired(key);
        CacheEntry entry = cache.get(key);
        if (entry == null || entry.expireTime == 0) {
            return null; // 永不过期
        }
        long remainingMs = entry.expireTime - System.currentTimeMillis();
        return remainingMs > 0 ? unit.convert(remainingMs, TimeUnit.MILLISECONDS) : 0L;
    }

    /**
     * @brief 模糊查询 key
     * @param pattern 匹配模式，支持 * 通配符
     * @return 匹配的 key 集合
     */
    public Set<String> keys(String pattern) {
        // 简单的模式匹配，仅支持*通配符
        String regex = pattern.replace("*", ".*");
        Set<String> result = new HashSet<>();
        
        for (String key : cache.keySet()) {
            removeExpired(key); // 检查并移除过期的key
            if (key.matches(regex) && cache.containsKey(key)) { // 再次检查key是否还存在
                result.add(key);
            }
        }
        
        return result;
    }
}
