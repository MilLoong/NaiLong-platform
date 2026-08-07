package com.nailong.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.TimeZone;

/**
 * @brief Spring MVC 配置类
 * @details 配置静态资源映射、Jackson 消息转换器时区及拦截器扩展点
 * @author Nailong
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    /**
     * 配置跨域请求
     * 注意：CORS配置已移至CorsConfig.java中，以避免与SecurityConfig冲突
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // CORS配置已在CorsConfig.java中统一配置，此处不再重复配置
        // 避免与SecurityConfig中使用的CorsConfigurationSource产生冲突

    }
    
    /**
     * 配置静态资源处理
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置Swagger文档的静态资源访问
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/")
                .resourceChain(false);
        
        // 配置Knife4j文档的静态资源访问
        registry.addResourceHandler("/doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        
        // 配置静态资源访问
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
        
        // 配置文件上传目录的静态资源访问
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./src/main/resources/static/uploads/");
    }
    
    /**
     * 配置消息转换器
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 配置Jackson消息转换器，设置时区和日期格式
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                MappingJackson2HttpMessageConverter jacksonConverter = 
                        (MappingJackson2HttpMessageConverter) converter;
                jacksonConverter.getObjectMapper().setTimeZone(TimeZone.getTimeZone("GMT+8"));
                // 日期格式已在application.yml中配置
            }
        }
    }
    
    /**
     * 配置格式化器
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        // 可以在这里添加自定义的格式化器，比如日期格式化器
        // 目前使用默认的格式化器即可
    }
    
    /**
     * 配置拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 可以在这里添加自定义的拦截器
        // 例如：登录拦截器、日志拦截器等
        // registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/**").excludePathPatterns("/api/auth/**");
    }
    
    /**
     * 配置参数解析器
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        // 可以在这里添加自定义的参数解析器
        // 例如：当前登录用户参数解析器等
    }
}
