package com.nailong;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @brief NaiLong Platform 招新平台主启动类
 * @author Nailong
 * @version 1.0.0
 */
@EnableAsync          // 开启异步任务
@EnableScheduling     // 开启定时任务
@EnableCaching        // 开启缓存机制
@SpringBootApplication
@MapperScan("com.nailong.mapper")
public class NaiLongApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(NaiLongApplication.class, args);
        System.out.println("""
            
            ========================================
            ★ NaiLong Platform Started! ★
            ========================================
            ? API文档: http://localhost:8080/api/doc.html
            ? 准备就绪，开始你的招聘之旅！
            ========================================
            
            """);
    }
}