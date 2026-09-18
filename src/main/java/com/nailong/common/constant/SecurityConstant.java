package com.nailong.common.constant;

/**
 * @brief 安全相关常量类
 * @author Nailong
 */
public class SecurityConstant {

    public static final String JWT_HEADER = "Authorization";  ///< JWT相关常量
    public static final String JWT_PREFIX = "Bearer ";
    public static final String JWT_TOKEN_TYPE = "token_type";
    public static final String JWT_ACCESS_TOKEN = "access_token";
    public static final String JWT_REFRESH_TOKEN = "refresh_token";
    public static final String JWT_EXPIRES_IN = "expires_in";
    
    public static final String JWT_CLAIM_USER_ID = "userId";  ///< JWT声明相关常量
    public static final String JWT_CLAIM_USERNAME = "username";
    public static final String JWT_CLAIM_ROLE = "role";
    public static final String JWT_CLAIM_ISS = "iss";
    public static final String JWT_CLAIM_AUD = "aud";
    public static final String JWT_CLAIM_IAT = "iat";
    public static final String JWT_CLAIM_EXP = "exp";
    
    public static final String ROLE_ADMIN = "ADMIN";  ///< 角色相关常量
    public static final String ROLE_USER = "USER";
    public static final String ROLE_GUEST = "GUEST";
    public static final String ROLE_PREFIX = "ROLE_";
    
    public static final String PERMISSION_PREFIX = "PERM_";  ///< 权限相关常量
    public static final String PERMISSION_ADMIN = "PERM_ADMIN";
    public static final String PERMISSION_USER_MANAGE = "PERM_USER_MANAGE";
    public static final String PERMISSION_PROBLEM_MANAGE = "PERM_PROBLEM_MANAGE";
    public static final String PERMISSION_RANK_VIEW = "PERM_RANK_VIEW";
    public static final String PERMISSION_SUBMISSION_CREATE = "PERM_SUBMISSION_CREATE";
    public static final String PERMISSION_NOTICE_MANAGE = "PERM_NOTICE_MANAGE";
    public static final String PERMISSION_LOG_VIEW = "PERM_LOG_VIEW";
    
    public static final String ANONYMOUS_USER = "anonymousUser";  ///< Spring Security相关常量
    public static final String AUTHENTICATION_NOT_FOUND = "Authentication object not found in SecurityContext";
    
    public static final Integer PASSWORD_MIN_LENGTH = 8;  ///< 密码相关常量
    public static final Integer PASSWORD_MAX_LENGTH = 20;
    public static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,20}$";
    
    public static final Integer CODE_LENGTH = 6;  ///< 验证码相关常量
    public static final Integer CODE_EXPIRE = 5 * 60; // 5分钟
    
    public static final String LOGIN_FAIL_MSG = "用户名或密码错误";  ///< 登录相关常量
    public static final String ACCOUNT_LOCKED_MSG = "账号已被锁定，请联系管理员";
    public static final String ACCOUNT_DISABLED_MSG = "账号已禁用，请联系管理员";
    public static final Integer MAX_LOGIN_ATTEMPTS = 5;
    public static final Integer ACCOUNT_LOCK_DURATION = 30 * 60; // 30分钟
    
    public static final String SESSION_USER_KEY = "currentUser";  ///< 会话相关常量
    public static final String SESSION_ID_KEY = "sessionId";
    
    public static final String SECURITY_CONFIG_PREFIX = "nailong.security.";  ///< 安全配置相关常量
    public static final String CORS_ALLOW_ORIGINS = "*";
    public static final String CORS_ALLOW_METHODS = "GET,POST,PUT,DELETE,OPTIONS";
    public static final String CORS_ALLOW_HEADERS = "Authorization,Content-Type,X-Requested-With,Accept,Origin";
    
    public static final String[] WHITE_LIST_PATHS = {
        "/auth/register",
        "/auth/login",
        "/auth/send-code",
        "/auth/reset-password",
        "/auth/refresh",
        "/doc.html",
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/swagger-resources/**",
        "/webjars/**",
        "/favicon.ico"
    };  ///< 白名单相关常量
    public static final String[] SENSITIVE_OPERATIONS = {
        "user:delete",
        "role:grant",
        "system:config",
        "log:clear"
    };  ///< 敏感操作相关常量
    public static final String ENCRYPT_ALGORITHM = "AES";  ///< 加密相关常量
    public static final String ENCRYPT_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    public static final String HASH_ALGORITHM_SHA256 = "SHA-256";
    public static final String HASH_ALGORITHM_MD5 = "MD5";
    
    public static final String REDIS_PREFIX_LOGIN_ATTEMPTS = "security:login:attempts:";  ///< Redis安全相关键前缀
    public static final String REDIS_PREFIX_ACCOUNT_LOCK = "security:account:lock:";
    public static final String REDIS_PREFIX_CAPTCHA = "security:captcha:";
    
    public static final String CSRF_COOKIE_NAME = "XSRF-TOKEN";  ///< CSRF相关常量
    public static final String CSRF_HEADER_NAME = "X-XSRF-TOKEN";
    
    public static final String IP_UNKNOWN = "unknown";  ///< 其他安全相关常量
    public static final Integer MAX_SESSION_COUNT = 5;
}
