package com.nailong.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.LocalDateTime;

/**
 * @brief MyBatis Plus 配置类
 * @details 配置分页插件、乐观锁插件及元数据自动填充；SqlSessionFactory 由 starter 自动装配
 * @author Nailong
 */
@Configuration
@EnableTransactionManagement
public class MybatisPlusConfig {

    /**
     * @brief MyBatis Plus 拦截器（分页、乐观锁）
     * @return 配置完成的 MybatisPlusInterceptor
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        PaginationInnerInterceptor paginationInterceptor =
                new PaginationInnerInterceptor(DbType.MYSQL);
        paginationInterceptor.setMaxLimit(500L);
        interceptor.addInnerInterceptor(paginationInterceptor);

        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }

    /**
     * @brief 元数据自动填充处理器
     * @details 插入时填充 createTime、updateTime、deleted；更新时填充 updateTime
     * @return MetaObjectHandler 实例
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {

            @Override
            public void insertFill(MetaObject metaObject) {
                this.strictInsertFill(metaObject, "createTime",
                        LocalDateTime.class, LocalDateTime.now());
                this.strictInsertFill(metaObject, "updateTime",
                        LocalDateTime.class, LocalDateTime.now());
                this.strictInsertFill(metaObject, "deleted",
                        Integer.class, 0);
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                this.strictUpdateFill(metaObject, "updateTime",
                        LocalDateTime.class, LocalDateTime.now());
            }
        };
    }
}
