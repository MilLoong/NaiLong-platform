package com.nailong.common.aspect;

import com.nailong.common.annotation.RepeatSubmit;
import com.nailong.common.constant.RedisKey;
import com.nailong.common.enums.ResultCode;
import com.nailong.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @brief 防重复提交切面
 * @author Nailong
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RepeatSubmitAspect {

    private final StringRedisTemplate redisTemplate;

    /**
     * @brief 环绕防重复提交注解方法
     * @param joinPoint 切点
     * @return 原方法返回值
     */
    @Around("@annotation(com.nailong.common.annotation.RepeatSubmit)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RepeatSubmit annotation = method.getAnnotation(RepeatSubmit.class);
        if (annotation == null) {
            return joinPoint.proceed();
        }

        String user = "anonymous";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getName() != null) {
            user = auth.getName();
        }

        String key = RedisKey.REPEAT_SUBMIT + user + ":"
                + method.getDeclaringClass().getSimpleName() + "." + method.getName();
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(key, "1", annotation.interval(), TimeUnit.SECONDS);
        if (!Boolean.TRUE.equals(success)) {
            throw new BusinessException(ResultCode.REPEAT_SUBMIT.getCode(), annotation.message());
        }
        return joinPoint.proceed();
    }
}
