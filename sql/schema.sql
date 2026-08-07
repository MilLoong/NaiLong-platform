-- 创建数据库
CREATE DATABASE IF NOT EXISTS nailong CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE nailong;

-- ============================================
-- 用户表
-- ============================================
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
    `email` VARCHAR(100) NOT NULL COMMENT '邮箱',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `student_id` VARCHAR(20) DEFAULT NULL COMMENT '学号',
    `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    `grade` VARCHAR(20) DEFAULT NULL COMMENT '年级',
    `major` VARCHAR(100) DEFAULT NULL COMMENT '专业',
    `direction` VARCHAR(20) DEFAULT NULL COMMENT '招新方向(frontend/backend/android/design/operations)',
    `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色(USER/ADMIN)',
    `total_score` INT NOT NULL DEFAULT 0 COMMENT '总积分',
    `solved_count` INT NOT NULL DEFAULT 0 COMMENT '解题数量',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '账号状态(0-禁用 1-正常)',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip` VARCHAR(50) DEFAULT NULL COMMENT '最后登录IP',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0-未删除 1-已删除)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_direction` (`direction`),
    KEY `idx_total_score` (`total_score` DESC),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================
-- 用户设备信息表
-- ============================================
CREATE TABLE `user_device` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '设备ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `device_type` VARCHAR(50) DEFAULT NULL COMMENT '设备类型',
    `device_os` VARCHAR(50) DEFAULT NULL COMMENT '操作系统',
    `browser` VARCHAR(100) DEFAULT NULL COMMENT '浏览器',
    `ip` VARCHAR(50) NOT NULL COMMENT 'IP地址',
    `location` VARCHAR(100) DEFAULT NULL COMMENT 'IP归属地',
    `login_time` DATETIME NOT NULL COMMENT '登录时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_ip` (`ip`),
    KEY `idx_login_time` (`login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户设备信息表';

-- ============================================
-- 题目表
-- ============================================
CREATE TABLE `problem` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '题目ID',
    `title` VARCHAR(200) NOT NULL COMMENT '题目标题',
    `description` TEXT NOT NULL COMMENT '题目描述',
    `type` VARCHAR(20) NOT NULL COMMENT '题目类型(CHOICE/FLAG/FILE/CODE)',
    `direction` VARCHAR(20) NOT NULL COMMENT '所属方向',
    `difficulty` VARCHAR(20) NOT NULL COMMENT '难度(EASY/MEDIUM/HARD)',
    `base_score` INT NOT NULL DEFAULT 100 COMMENT '基础分数',
    `current_score` INT NOT NULL DEFAULT 100 COMMENT '当前分数(衰减后)',
    `decay_rate` DECIMAL(5,4) NOT NULL DEFAULT 0.0100 COMMENT '衰减率',
    `min_score` INT NOT NULL DEFAULT 10 COMMENT '最低分数',
    `solved_count` INT NOT NULL DEFAULT 0 COMMENT '解题人数',
    `submit_count` INT NOT NULL DEFAULT 0 COMMENT '提交次数',
    `answer` TEXT DEFAULT NULL COMMENT '答案(选择题/Flag题)',
    `attachment_url` VARCHAR(500) DEFAULT NULL COMMENT '附件URL',
    `hint` TEXT DEFAULT NULL COMMENT '提示信息',
    `tags` VARCHAR(200) DEFAULT NULL COMMENT '标签(JSON数组)',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0-隐藏 1-发布)',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序权重',
    `creator_id` BIGINT NOT NULL COMMENT '创建者ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_type` (`type`),
    KEY `idx_direction` (`direction`),
    KEY `idx_difficulty` (`difficulty`),
    KEY `idx_status` (`status`),
    KEY `idx_solved_count` (`solved_count`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题目表';

-- ============================================
-- 提交记录表
-- ============================================
CREATE TABLE `submission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '提交ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `problem_id` BIGINT NOT NULL COMMENT '题目ID',
    `answer` TEXT NOT NULL COMMENT '提交答案',
    `status` VARCHAR(20) NOT NULL COMMENT '判题状态(PENDING/ACCEPTED/WRONG_ANSWER/ERROR)',
    `score` INT NOT NULL DEFAULT 0 COMMENT '获得分数',
    `time_cost` INT DEFAULT NULL COMMENT '耗时(ms)',
    `memory_cost` INT DEFAULT NULL COMMENT '内存消耗(KB)',
    `language` VARCHAR(20) DEFAULT NULL COMMENT '编程语言(用于编程题)',
    `ip` VARCHAR(50) DEFAULT NULL COMMENT '提交IP',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注(错误信息等)',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_problem` (`user_id`, `problem_id`, `deleted`),
    KEY `idx_problem_id` (`problem_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='提交记录表';

-- ============================================
-- 公告表
-- ============================================
CREATE TABLE `notice` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '公告ID',
    `title` VARCHAR(200) NOT NULL COMMENT '公告标题',
    `content` TEXT NOT NULL COMMENT '公告内容',
    `type` VARCHAR(20) NOT NULL DEFAULT 'NORMAL' COMMENT '公告类型(NORMAL/IMPORTANT/URGENT)',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0-隐藏 1-发布)',
    `top` TINYINT NOT NULL DEFAULT 0 COMMENT '是否置顶(0-否 1-是)',
    `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览次数',
    `publisher_id` BIGINT NOT NULL COMMENT '发布者ID',
    `publish_time` DATETIME DEFAULT NULL COMMENT '发布时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_type` (`type`),
    KEY `idx_status` (`status`),
    KEY `idx_top` (`top`),
    KEY `idx_publish_time` (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告表';

-- ============================================
-- 操作日志表
-- ============================================
CREATE TABLE `operation_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `user_id` BIGINT DEFAULT NULL COMMENT '操作用户ID',
    `username` VARCHAR(50) DEFAULT NULL COMMENT '用户名',
    `operation` VARCHAR(100) NOT NULL COMMENT '操作类型',
    `method` VARCHAR(200) DEFAULT NULL COMMENT '请求方法',
    `params` TEXT DEFAULT NULL COMMENT '请求参数',
    `result` TEXT DEFAULT NULL COMMENT '返回结果',
    `ip` VARCHAR(50) DEFAULT NULL COMMENT '操作IP',
    `location` VARCHAR(100) DEFAULT NULL COMMENT 'IP归属地',
    `time_cost` INT DEFAULT NULL COMMENT '耗时(ms)',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0-失败 1-成功)',
    `error_msg` TEXT DEFAULT NULL COMMENT '错误信息',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_operation` (`operation`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- ============================================
-- 索引优化说明
-- ============================================
-- 1. user表: 主要查询场景是排行榜(total_score DESC)和方向筛选
-- 2. submission表: 唯一索引防止重复提交，联合索引(user_id, problem_id)
-- 3. problem表: 多维度筛选需要组合索引
-- 4. 所有表都使用逻辑删除，索引包含deleted字段