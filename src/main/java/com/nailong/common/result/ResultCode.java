package com.nailong.common.result;

/**
 * @brief 业务响应状态码枚举
 * @author Nailong
 */
public enum ResultCode {

    // 成功状态码
    SUCCESS(200, "操作成功"),
    
    // 参数错误
    PARAM_ERROR(400, "参数错误"),
    PARAM_VALIDATE_ERROR(401, "参数校验失败"),
    
    // 未授权
    UNAUTHORIZED(402, "未授权"),
    TOKEN_EXPIRED(403, "令牌过期"),
    
    // 权限错误
    FORBIDDEN(404, "没有访问权限"),
    
    // 业务错误
    USER_NOT_FOUND(500, "用户不存在"),
    USER_ALREADY_EXISTS(501, "用户已存在"),
    USER_STATUS_ERROR(502, "用户状态异常"),
    PASSWORD_ERROR(503, "密码错误"),
    EMAIL_ALREADY_EXISTS(504, "邮箱已被使用"),
    PHONE_ALREADY_EXISTS(505, "手机号已被使用"),
    CODE_ERROR(506, "验证码错误"),
    CODE_EXPIRED(507, "验证码过期"),
    
    // 系统错误
    SYSTEM_ERROR(900, "系统内部错误"),
    DATABASE_ERROR(901, "数据库错误"),
    NETWORK_ERROR(902, "网络错误"),
    FILE_ERROR(903, "文件处理错误"),
    
    // 题目相关错误
    PROBLEM_NOT_FOUND(1000, "题目不存在"),
    PROBLEM_TYPE_ERROR(1001, "题目类型错误"),
    ANSWER_ERROR(1002, "答案错误"),
    
    // 提交相关错误
    SUBMISSION_LIMIT(1003, "提交频率过高"),
    ALREADY_SOLVED(1004, "该题目已解决"),
    
    // 公告相关错误
    NOTICE_NOT_FOUND(1100, "公告不存在"),
    
    // 设备相关错误
    DEVICE_NOT_FOUND(1200, "设备不存在"),
    
    // 限流相关错误
    RATE_LIMITED(1300, "请求过于频繁，请稍后再试"),
    REPEAT_SUBMIT(1301, "请勿重复提交"),
    
    // 缓存相关错误
    CACHE_ERROR(1400, "缓存操作失败");
    
    private final int code;
    private final String message;
    
    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
}