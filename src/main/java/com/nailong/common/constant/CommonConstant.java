package com.nailong.common.constant;

/**
 * @brief 通用常量类
 * @author Nailong
 */
public class CommonConstant {

    /**
     * 字符编码相关常量
     */
    public static final String UTF_8 = "UTF-8";
    public static final String GBK = "GBK";
    
    /**
     * HTTP请求相关常量
     */
    public static final String HTTP_METHOD_GET = "GET";
    public static final String HTTP_METHOD_POST = "POST";
    public static final String HTTP_METHOD_PUT = "PUT";
    public static final String HTTP_METHOD_DELETE = "DELETE";
    
    /**
     * HTTP请求头相关常量
     */
    public static final String HEADER_USER_AGENT = "User-Agent";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_X_REQUESTED_WITH = "X-Requested-With";
    public static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";
    
    /**
     * 内容类型相关常量
     */
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";
    public static final String CONTENT_TYPE_MULTIPART = "multipart/form-data";
    
    /**
     * 时间格式相关常量
     */
    public static final String DATE_FORMAT_YMD = "yyyy-MM-dd";
    public static final String DATE_FORMAT_YMD_HMS = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_ISO = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    
    /**
     * 分页相关常量
     */
    public static final Integer DEFAULT_PAGE_NUM = 1;
    public static final Integer DEFAULT_PAGE_SIZE = 10;
    public static final Integer MAX_PAGE_SIZE = 100;
    
    /**
     * 用户相关常量
     */
    public static final String USER_DEFAULT_AVATAR = "default_avatar.png";
    public static final String USER_DEFAULT_NICKNAME = "用户";
    
    /**
     * 状态相关常量
     */
    public static final Integer STATUS_ENABLE = 1;
    public static final Integer STATUS_DISABLE = 0;
    
    /**
     * 性别相关常量
     */
    public static final Integer GENDER_UNKNOWN = 0;
    public static final Integer GENDER_MALE = 1;
    public static final Integer GENDER_FEMALE = 2;
    
    /**
     * 逻辑删除相关常量
     */
    public static final Integer DELETED_NO = 0;
    public static final Integer DELETED_YES = 1;
    
    /**
     * 符号相关常量
     */
    public static final String SYMBOL_COMMA = ",";
    public static final String SYMBOL_COLON = ":";
    public static final String SYMBOL_SEMICOLON = ";";
    public static final String SYMBOL_UNDERSCORE = "_";
    public static final String SYMBOL_SLASH = "/";
    public static final String SYMBOL_DOT = ".";
    public static final String SYMBOL_DASH = "-";
    public static final String SYMBOL_QUESTION_MARK = "?";
    public static final String SYMBOL_EQUALS = "=";
    public static final String SYMBOL_AMPERSAND = "&";
    
    /**
     * 路径相关常量
     */
    public static final String PATH_SEPARATOR = "/";
    public static final String PATH_TEMP = "/temp";
    public static final String PATH_UPLOAD = "/upload";
    public static final String PATH_STATIC = "/static";
    
    /**
     * 其他常用常量
     */
    public static final String EMPTY_STRING = "";
    public static final String SPACE = " ";
    public static final String NULL_STRING = "null";
    public static final String UNKNOWN = "unknown";
    
    /**
     * 缓存相关常量
     */
    public static final Long CACHE_EXPIRE_DEFAULT = 3600L; // 默认缓存过期时间（秒）
    public static final Long CACHE_EXPIRE_SHORT = 60L; // 短期缓存过期时间（秒）
    public static final Long CACHE_EXPIRE_LONG = 86400L; // 长期缓存过期时间（秒）
    
    /**
     * 正则表达式相关常量
     */
    public static final String REGEX_EMAIL = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
    public static final String REGEX_PHONE = "^1[3-9]\\d{9}$";
    public static final String REGEX_IP = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
    public static final String REGEX_URL = "^((https|http|ftp|rtsp|mms)?:\\/\\/)[^\\s]";
    
    /**
     * 题目相关常量
     */
    public static final String PROBLEM_TYPE_SINGLE = "single";
    public static final String PROBLEM_TYPE_MULTIPLE = "multiple";
    public static final String PROBLEM_TYPE_JUDGMENT = "judgment";
    public static final String PROBLEM_TYPE_FILL = "fill";
    public static final String PROBLEM_TYPE_PROGRAM = "program";
    
    /**
     * 难度等级相关常量
     */
    public static final String DIFFICULTY_EASY = "easy";
    public static final String DIFFICULTY_MEDIUM = "medium";
    public static final String DIFFICULTY_HARD = "hard";
    
    /**
     * 方向相关常量
     */
    public static final String DIRECTION_FRONTEND = "frontend";
    public static final String DIRECTION_BACKEND = "backend";
    public static final String DIRECTION_FULLSTACK = "fullstack";
    public static final String DIRECTION_MOBILE = "mobile";
    public static final String DIRECTION_AI = "ai";
}
