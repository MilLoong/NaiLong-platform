package com.nailong.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @brief CORS 跨域配置类
 * @details 从 nailong.cors.* 读取允许来源，避免生产环境全开
 * @author Nailong
 */
@Configuration
public class CorsConfig {

    @Value("${nailong.cors.allowed-origins:*}")
    private String allowedOrigins;

    @Value("${nailong.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String allowedMethods;

    @Value("${nailong.cors.allowed-headers:*}")
    private String allowedHeaders;

    @Value("${nailong.cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Value("${nailong.cors.max-age:3600}")
    private long maxAge;

    /**
     * @brief 创建 CORS 配置源
     * @return 注册到 /** 路径的 CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        List<String> origins = split(allowedOrigins);
        boolean wildcard = origins.stream().anyMatch(o -> "*".equals(o));
        if (wildcard) {
            // 通配来源时使用 OriginPattern，并关闭 credentials 以防 CSRF 放大
            configuration.addAllowedOriginPattern("*");
            configuration.setAllowCredentials(false);
        } else {
            configuration.setAllowedOrigins(origins);
            configuration.setAllowCredentials(allowCredentials);
        }

        configuration.setAllowedMethods(split(allowedMethods));
        List<String> headers = split(allowedHeaders);
        if (headers.size() == 1 && "*".equals(headers.get(0))) {
            configuration.addAllowedHeader("*");
        } else {
            configuration.setAllowedHeaders(headers);
        }
        configuration.setMaxAge(maxAge);
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private List<String> split(String value) {
        if (!StringUtils.hasText(value)) {
            return List.of();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());
    }
}
