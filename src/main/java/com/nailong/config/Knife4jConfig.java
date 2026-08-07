package com.nailong.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.ExternalDocumentation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @brief Knife4j API 文档配置类
 * @details 仅非生产环境启用，避免线上暴露接口表面
 * @author Nailong
 */
@Configuration
@Profile("!prod")
class Knife4jConfig {

    /**
     * @brief 创建 OpenAPI 文档 Bean
     * @return 配置完成的 OpenAPI 实例
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("NaiLong Platform API")
                .description("NaiLong-platform 招新平台后端接口文档")
                .version("v1.0.0")
                .contact(new Contact()
                    .name("NaiLong Team")
                    .email("contact@nailong.com")))
            .externalDocs(new ExternalDocumentation()
                .description("项目GitHub地址")
                .url("https://github.com/nailong/NaiLong-platform"));
    }
}
