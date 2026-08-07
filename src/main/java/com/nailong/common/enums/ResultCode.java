package com.nailong.common.enums;

/**
 * @brief 响应状态码枚举
 * @author Nailong
 */
public enum ResultCode {
    
    // 成功
    SUCCESS(200, "操作成功"),
    
    // 客户端错误 4xx
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权，请先登录"),
    FORBIDDEN(403, "权限不足"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    REQUEST_TIMEOUT(408, "请求超时"),
    CONFLICT(409, "数据冲突"),
    PRECONDITION_FAILED(412, "前置条件失败"),
    TOO_MANY_REQUESTS(429, "请求过于频繁，请稍后再试"),
    
    // 服务端错误 5xx
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务暂时不可用"),
    
    // 业务错误 1xxx
    USER_NOT_FOUND(1001, "用户不存在"),
    AUTHENTICATION_ERROR(1010, "身份验证错误"),
    USER_ALREADY_EXISTS(1002, "用户已存在"),
    INVALID_PASSWORD(1003, "密码错误"),
    INVALID_EMAIL(1004, "邮箱格式不正确"),
    EMAIL_ALREADY_EXISTS(1005, "邮箱已被注册"),
    INVALID_VERIFICATION_CODE(1006, "验证码错误或已过期"),
    TOKEN_EXPIRED(1007, "登录已过期，请重新登录"),
    TOKEN_INVALID(1008, "无效的Token"),
    ACCOUNT_DISABLED(1009, "账号已被禁用"),
    ACCOUNT_LOCKED(1010, "账号已被锁定"),
    DEVICE_NOT_FOUND(1011, "设备不存在"),
    
    // 题目相关 2xxx
    PROBLEM_NOT_FOUND(2001, "题目不存在"),
    PROBLEM_ALREADY_SOLVED(2002, "您已完成该题目"),
    ANSWER_INCORRECT(2003, "答案错误"),
    SUBMISSION_TOO_FREQUENT(2004, "提交过于频繁"),
    FILE_UPLOAD_FAILED(2005, "文件上传失败"),
    FILE_TOO_LARGE(2006, "文件大小超过限制"),
    INVALID_FILE_TYPE(2007, "不支持的文件类型"),
    
    // 排行榜相关 3xxx
    RANK_DATA_NOT_FOUND(3001, "排行榜数据不存在"),
    RANK_SYNC_FAILED(3002, "排行榜同步失败"),
    
    // 公告相关 4xxx
    NOTICE_NOT_FOUND(4001, "公告不存在"),
    
    // 系统相关 9xxx
    RATE_LIMIT_EXCEEDED(9001, "访问频率超限，请稍后再试"),
    IP_BLOCKED(9002, "您的IP已被封禁"),
    REPEAT_SUBMIT(9003, "请勿重复提交"),
    DATABASE_ERROR(9004, "数据库操作失败"),
    REDIS_ERROR(9005, "缓存操作失败"),
    EMAIL_SEND_FAILED(9006, "邮件发送失败"),
    SYSTEM_BUSY(9007, "系统繁忙，请稍后再试");
    
    // 字段
    private final int code;
    private final String message;
    
    // 构造方法
    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    // Getter方法
    public int getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
}