package com.nailong.security.filter;

import com.nailong.common.constant.RedisKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @brief IP 黑名单服务
 * @details 提供 IP 黑名单的添加、查询与移除（基于 Redis）
 * @author Nailong
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IpBlacklistService {

    private final StringRedisTemplate redisTemplate;

    /**
     * @brief 添加 IP 到黑名单
     * @param ip       IP 地址
     * @param duration 黑名单持续时间
     * @param unit     时间单位
     */
    public void addToBlacklist(String ip, long duration, TimeUnit unit) {
        String key = RedisKey.IP_BLACKLIST;
        redisTemplate.opsForSet().add(key, ip);
        redisTemplate.expire(key, duration, unit);
        log.warn("IP已加入黑名单: ip={}", ip);
    }

    /**
     * @brief 检查 IP 是否在黑名单中
     * @param ip IP 地址
     * @return 在黑名单中返回 true，否则 false
     */
    public boolean isBlacklisted(String ip) {
        Boolean result = redisTemplate.opsForSet().isMember(RedisKey.IP_BLACKLIST, ip);
        return Boolean.TRUE.equals(result);
    }

    /**
     * @brief 从黑名单移除 IP
     * @param ip IP 地址
     */
    public void removeFromBlacklist(String ip) {
        redisTemplate.opsForSet().remove(RedisKey.IP_BLACKLIST, ip);
        log.info("IP已从黑名单移除: ip={}", ip);
    }
}
