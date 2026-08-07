package com.nailong.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @brief Redisson 配置类
 * @details 通过 application 配置创建 RedissonClient，支持分布式锁与 Redis 操作
 * @author Nailong
 */
@Configuration
public class RedissonConfig {

    @Value("${redisson.address:redis://127.0.0.1:6379}")
    private String redisAddress;
    
    @Value("${redisson.password:}")
    private String redisPassword;
    
    /**
     * @brief 创建 RedissonClient Bean
     * @details 使用配置文件中的地址与密码连接 Redis
     * @return RedissonClient 实例
     */
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        var server = config.useSingleServer()
              .setAddress(redisAddress)
              .setDatabase(0);
        if (redisPassword != null && !redisPassword.isBlank()) {
            server.setPassword(redisPassword);
        }
        return Redisson.create(config);
    }
}