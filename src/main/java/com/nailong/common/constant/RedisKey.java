package com.nailong.common.constant;

/**
 * @brief Redis Key 常量类
 * @author Nailong
 */
public class RedisKey {
    
    /**
     * 邮箱验证码前缀: email:code:{type}:{email}
     */
    public static final String EMAIL_CODE = "email:code:";
    
    /**
     * 用户Token前缀: user:token:{userId}
     */
    public static final String USER_TOKEN = "user:token:";
    
    /**
     * 排行榜前缀: rank:{type}
     * 例如: rank:all, rank:frontend, rank:backend
     */
    public static final String RANK = "rank:";
    
    /**
     * 用户 Refresh Token: user:refresh:{userId}
     */
    public static final String USER_REFRESH_TOKEN = "user:refresh:";
    
    /**
     * 用户信息缓存: user:info:{userId}
     */
    public static final String USER_INFO = "user:info:";
    
    /**
     * 题目信息缓存: problem:info:{problemId}
     */
    public static final String PROBLEM_INFO = "problem:info:";
    
    /**
     * 题目列表缓存: problem:list:{direction}:{difficulty}
     */
    public static final String PROBLEM_LIST = "problem:list:";
    
    /**
     * 用户提交记录: submission:{userId}:{problemId}
     */
    public static final String SUBMISSION = "submission:";
    
    /**
     * 限流Key: rate:limit:{ip}:{api}
     */
    public static final String RATE_LIMIT = "rate:limit:";
    
    /**
     * IP黑名单: ip:blacklist
     */
    public static final String IP_BLACKLIST = "ip:blacklist";
    
    /**
     * 防重复提交: repeat:submit:{userId}:{method}
     */
    public static final String REPEAT_SUBMIT = "repeat:submit:";
    
    /**
     * 分布式锁: lock:{resource}
     */
    public static final String LOCK = "lock:";
    
    /**
     * 公告列表缓存: notice:list
     */
    public static final String NOTICE_LIST = "notice:list";
    
    /**
     * 用户设备信息: device:info:{userId}:{deviceId}
     */
    public static final String DEVICE_INFO = "device:info:";
    
    /**
     * 用户设备列表: device:list:{userId}
     */
    public static final String DEVICE_LIST = "device:list:";
    
    /**
     * 操作日志统计: log:stats:{type}:{date}
     */
    public static final String LOG_STATS = "log:stats:";
}