package com.nailong.common.aspect;

import com.nailong.common.annotation.Log;
import com.nailong.common.utils.IpUtil;
import com.nailong.service.IOperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @brief 操作日志切面
 * @details 记录标注了 @Log 的接口调用
 * @author Nailong
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final IOperationLogService operationLogService;

    /**
     * @brief 环绕操作日志注解方法
     * @param joinPoint 切点
     * @return 原方法返回值
     */
    @Around("@annotation(com.nailong.common.annotation.Log)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Log logAnnotation = method.getAnnotation(Log.class);

        long start = System.currentTimeMillis();
        Object result = null;
        Integer status = 1;
        String errorMsg = null;
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable ex) {
            status = 0;
            errorMsg = ex.getMessage();
            throw ex;
        } finally {
            try {
                record(joinPoint, logAnnotation, result, status, errorMsg,
                        (int) (System.currentTimeMillis() - start));
            } catch (Exception e) {
                log.warn("记录操作日志失败: {}", e.getMessage());
            }
        }
    }

    private void record(ProceedingJoinPoint joinPoint, Log logAnnotation, Object result,
                        Integer status, String errorMsg, int timeCost) {
        if (logAnnotation == null) {
            return;
        }

        Long userId = null;
        String username = "anonymous";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            username = auth.getName();
        }

        String ip = "unknown";
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            ip = IpUtil.getIpAddress(request);
        }

        String params = logAnnotation.recordParams()
                ? Arrays.toString(joinPoint.getArgs()) : null;
        String resultText = logAnnotation.recordResult() && result != null
                ? String.valueOf(result) : null;
        if (!logAnnotation.recordError()) {
            errorMsg = null;
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        operationLogService.addLog(
                userId,
                username,
                logAnnotation.operation(),
                signature.getDeclaringType().getSimpleName() + "." + signature.getName(),
                truncate(params),
                truncate(resultText),
                ip,
                IpUtil.getLocation(ip),
                timeCost,
                status,
                truncate(errorMsg)
        );
    }

    private String truncate(String value) {
        if (value == null) {
            return null;
        }
        return value.length() > 2000 ? value.substring(0, 2000) : value;
    }
}
