package com.nailong.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Duration;

/**
 * @brief 本地缓存配置
 * @details 配置 Caffeine 本地缓存与 CacheManager，供 Spring Cache 使用
 * @author Nailong
 */
@Configuration
@EnableCaching
public class CacheConfig {
    
    /**
     * @brief 创建 Caffeine 本地缓存实例
     * @return 最大 1000 条、写入后 30 分钟过期的 Cache
     */
    @Bean
    public Cache<String, Object> caffeineCache() {
        return Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(Duration.ofMinutes(30))
                .recordStats()
                .build();
    }
    
    /**
     * @brief 创建 Caffeine 缓存管理器
     * @return 主缓存管理器 CaffeineCacheManager
     */
    @Bean("caffeineCacheManager")
    @Primary
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(Duration.ofMinutes(30))
                .recordStats());
        return cacheManager;
    }
}
