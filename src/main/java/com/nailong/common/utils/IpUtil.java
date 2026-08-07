package com.nailong.common.utils;

import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @brief IP 工具类
 * @details 提供客户端 IP 获取、内网判断及归属地查询
 * @author Nailong
 */
@Slf4j
public class IpUtil {

    private static final String UNKNOWN = "unknown";

    /**
     * @brief 获取客户端真实 IP 地址
     * @param request HTTP 请求
     * @return 客户端 IP 地址
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 处理多级代理的情况
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

    /**
     * @brief 获取客户端真实 IP 地址（简化方法名）
     * @param request HTTP 请求
     * @return 客户端 IP 地址
     */
    public static String getIp(HttpServletRequest request) {
        return getIpAddress(request);
    }

    /**
     * @brief 判断是否为内网 IP
     * @param ip IP 地址
     * @return 内网 IP 返回 true，否则 false
     */
    public static boolean isInternalIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }

        return ip.startsWith("127.") || 
               ip.startsWith("10.") || 
               ip.startsWith("192.168.") || 
               ip.matches("172\\.(1[6-9]|2[0-9]|3[0-1])\\..*");
    }

    /**
     * @brief 获取 IP 地址的归属地
     * @details 实际项目中可调用第三方 API 获取真实归属地，当前返回默认值
     * @param ip IP 地址
     * @return 归属地描述
     */
    public static String getLocation(String ip) {
        if (ip == null || ip.isEmpty()) {
            return "未知";
        }

        // 简单的内网IP判断
        if (isInternalIp(ip)) {
            return "内网IP";
        }

        // 这里可以调用第三方API获取真实归属地
        // 例如：ip-api.com, taobao IP库等
        // 目前返回默认值
        return "未知位置";
    }
}
