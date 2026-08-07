package com.nailong.common.aspect;

import com.nailong.common.annotation.RateLimit;
import com.nailong.common.constant.RedisKey;
import com.nailong.common.enums.ResultCode;
import com.nailong.common.exception.BusinessException;
import com.nailong.common.utils.IpUtil;
import com.nailong.security.filter.IpBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @brief 限流切面
 * @details 基于 Redis + Lua 实现按 IP / 用户维度限流，超限可临时拉黑 IP
 * @author Nailong
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final StringRedisTemplate redisTemplate;
    private final IpBlacklistService ipBlacklistService;

    private static final String LUA_SCRIPT =
            "local current = redis.call('incr', KEYS[1]) " +
            "if tonumber(current) == 1 then " +
            "  redis.call('expire', KEYS[1], ARGV[1]) " +
            "end " +
            "return current";

    /**
     * @brief 环绕限流注解方法
     * @param joinPoint 切点
     * @return 原方法返回值
     */
    @Around("@annotation(com.nailong.common.annotation.RateLimit)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);
        if (rateLimit == null) {
            return joinPoint.proceed();
        }

        String dimension = resolveDimension(rateLimit.type());
        String key = RedisKey.RATE_LIMIT + dimension + ":" + method.getDeclaringClass().getSimpleName()
                + "." + method.getName();

        DefaultRedisScript<Long> script = new DefaultRedisScript<>(LUA_SCRIPT, Long.class);
        Long count = redisTemplate.execute(script, Collections.singletonList(key),
                String.valueOf(rateLimit.timeout()));

        if (count != null && count > rateLimit.qps()) {
            log.warn("触发限流: key={}, count={}, qps={}", key, count, rateLimit.qps());
            maybeBlacklistIp(dimension);
            throw new BusinessException(ResultCode.RATE_LIMIT_EXCEEDED);
        }
        return joinPoint.proceed();
    }

    private void maybeBlacklistIp(String dimension) {
        if (dimension == null || !dimension.startsWith("ip:")) {
            return;
        }
        String ip = dimension.substring(3);
        if ("unknown".equals(ip) || ip.isBlank()) {
            return;
        }
        ipBlacklistService.addToBlacklist(ip, 15, TimeUnit.MINUTES);
    }

    private String resolveDimension(String type) {
        if ("USER".equalsIgnoreCase(type)) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && auth.getPrincipal() != null) {
                return "user:" + auth.getPrincipal();
            }
        }
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            return "ip:" + IpUtil.getIpAddress(request);
        }
        return "ip:unknown";
    }
}
