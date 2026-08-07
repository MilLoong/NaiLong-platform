package com.nailong.common.aspect;

import com.nailong.common.annotation.AuthCheck;
import com.nailong.common.enums.ResultCode;
import com.nailong.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @brief 权限校验切面
 * @details 使 @AuthCheck 注解真正生效，校验登录态与角色
 * @author Nailong
 */
@Slf4j
@Aspect
@Component
public class AuthCheckAspect {

    /**
     * @brief 环绕权限校验注解方法
     * @param joinPoint 切点
     * @return 原方法返回值
     */
    @Around("@annotation(com.nailong.common.annotation.AuthCheck)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        AuthCheck authCheck = method.getAnnotation(AuthCheck.class);
        if (authCheck == null) {
            return joinPoint.proceed();
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean authenticated = authentication != null
                && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String
                && "anonymousUser".equals(authentication.getPrincipal()));

        if (authCheck.login() && !authenticated) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        if (authCheck.admin() || "admin".equalsIgnoreCase(authCheck.role())
                || "ADMIN".equalsIgnoreCase(authCheck.role())) {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(a -> "ROLE_ADMIN".equals(a) || "ADMIN".equals(a));
            if (!isAdmin) {
                throw new BusinessException(ResultCode.FORBIDDEN);
            }
        } else if (authCheck.role() != null && !authCheck.role().isBlank()) {
            String required = "ROLE_" + authCheck.role().toUpperCase();
            boolean matched = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(a -> a.equalsIgnoreCase(required)
                            || a.equalsIgnoreCase(authCheck.role()));
            if (!matched) {
                throw new BusinessException(ResultCode.FORBIDDEN);
            }
        }

        return joinPoint.proceed();
    }
}
