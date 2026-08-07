-- ============================================
-- 初始化数据脚本
-- ============================================
USE nailong;

-- ============================================
-- 插入管理员账号
-- ============================================
-- 密码: Admin123 (BCrypt加密后)
INSERT INTO `user` (
    `username`, `password`, `email`, `nickname`, `role`, 
    `total_score`, `solved_count`, `status`, `create_time`, `update_time`
) VALUES (
    'admin', 
    '$2a$10$tWesC6b3gYrX.CwjIVy53uNj1MET8VtZ/NCfD1NqcTc7GFcqwp9vK', 
    'admin@nailong.com', 
    '系统管理员', 
    'ADMIN', 
    0, 
    0, 
    1, 
    NOW(), 
    NOW()
);

-- ============================================
-- 插入示例题目
-- ============================================

-- 1. 前端-简单题
INSERT INTO `problem` (
    `title`, `description`, `type`, `direction`, `difficulty`,
    `base_score`, `current_score`, `decay_rate`, `min_score`,
    `solved_count`, `submit_count`, `answer`, `hint`, `tags`,
    `status`, `sort_order`, `creator_id`, `create_time`, `update_time`
) VALUES (
    '前端基础：CSS选择器',
    '## 题目描述\n\n请回答：CSS中用于选择所有元素的选择器是什么？\n\nA. #all\nB. .all\nC. *\nD. all\n\n请输入选项字母（A/B/C/D）',
    'CHOICE',
    'frontend',
    'EASY',
    50,
    50,
    0.01,
    10,
    0,
    0,
    'C',
    '通配符选择器',
    '["CSS", "基础知识"]',
    1,
    100,
    1,
    NOW(),
    NOW()
);

-- 2. 后端-中等题
INSERT INTO `problem` (
    `title`, `description`, `type`, `direction`, `difficulty`,
    `base_score`, `current_score`, `decay_rate`, `min_score`,
    `solved_count`, `submit_count`, `answer`, `hint`, `tags`,
    `status`, `sort_order`, `creator_id`, `create_time`, `update_time`
) VALUES (
    '后端基础：HTTP状态码',
    '## 题目描述\n\n找出隐藏在下面文本中的Flag：\n\n"当服务器成功处理请求时，返回状态码200，创建新资源时返回201，未授权时返回401，服务器错误时返回500。"\n\n提示：Flag格式为 flag{数字组合}',
    'FLAG',
    'backend',
    'MEDIUM',
    100,
    100,
    0.01,
    10,
    0,
    0,
    'flag{200201401500}',
    '把所有HTTP状态码按顺序连起来',
    '["HTTP", "状态码"]',
    1,
    90,
    1,
    NOW(),
    NOW()
);

-- 3. Android-简单题
INSERT INTO `problem` (
    `title`, `description`, `type`, `direction`, `difficulty`,
    `base_score`, `current_score`, `decay_rate`, `min_score`,
    `solved_count`, `submit_count`, `answer`, `hint`, `tags`,
    `status`, `sort_order`, `creator_id`, `create_time`, `update_time`
) VALUES (
    'Android基础：四大组件',
    '## 题目描述\n\nAndroid的四大组件是：Activity、Service、BroadcastReceiver 和 ？\n\n请填写最后一个组件的名称。',
    'FLAG',
    'android',
    'EASY',
    50,
    50,
    0.01,
    10,
    0,
    0,
    'ContentProvider',
    '负责数据共享',
    '["Android", "四大组件"]',
    1,
    100,
    1,
    NOW(),
    NOW()
);

-- 4. 设计-简单题
INSERT INTO `problem` (
    `title`, `description`, `type`, `direction`, `difficulty`,
    `base_score`, `current_score`, `decay_rate`, `min_score`,
    `solved_count`, `submit_count`, `answer`, `hint`, `tags`,
    `status`, `sort_order`, `creator_id`, `create_time`, `update_time`
) VALUES (
    'UI设计：色彩理论',
    '## 题目描述\n\n红、黄、蓝被称为什么颜色？\n\nA. 二次色\nB. 三次色\nC. 原色\nD. 复合色\n\n请输入选项字母。',
    'CHOICE',
    'design',
    'EASY',
    50,
    50,
    0.01,
    10,
    0,
    0,
    'C',
    '不能由其他颜色混合而成',
    '["色彩理论", "基础知识"]',
    1,
    100,
    1,
    NOW(),
    NOW()
);

-- 5. 运营-中等题
INSERT INTO `problem` (
    `title`, `description`, `type`, `direction`, `difficulty`,
    `base_score`, `current_score`, `decay_rate`, `min_score`,
    `solved_count`, `submit_count`, `answer`, `hint`, `tags`,
    `status`, `sort_order`, `creator_id`, `create_time`, `update_time`
) VALUES (
    '运营基础：用户增长',
    '## 题目描述\n\n在AARRR模型中，五个阶段按顺序分别是：\n获取用户(Acquisition)、提高活跃度(Activation)、提高留存率(Retention)、获取收入(Revenue) 和 ？\n\n请填写最后一个阶段的英文单词。',
    'FLAG',
    'operations',
    'MEDIUM',
    100,
    100,
    0.01,
    10,
    0,
    0,
    'Referral',
    '用户推荐',
    '["AARRR模型", "用户增长"]',
    1,
    90,
    1,
    NOW(),
    NOW()
);

-- 6. 编程题示例（困难）
INSERT INTO `problem` (
    `title`, `description`, `type`, `direction`, `difficulty`,
    `base_score`, `current_score`, `decay_rate`, `min_score`,
    `solved_count`, `submit_count`, `hint`, `tags`,
    `status`, `sort_order`, `creator_id`, `create_time`, `update_time`
) VALUES (
    '算法：两数之和',
    '## 题目描述\n\n给定一个整数数组 nums 和一个整数目标值 target，请你在该数组中找出和为目标值 target 的那两个整数，并返回它们的数组下标。\n\n## 示例\n\n输入：nums = [2,7,11,15], target = 9\n输出：[0,1]\n解释：因为 nums[0] + nums[1] == 9，返回 [0, 1]。\n\n## 限制\n\n- 2 <= nums.length <= 104\n- -109 <= nums[i] <= 109\n- -109 <= target <= 109\n- 只会存在一个有效答案',
    'CODE',
    'backend',
    'HARD',
    200,
    200,
    0.01,
    20,
    0,
    0,
    '使用哈希表可以降低时间复杂度',
    '["算法", "哈希表", "数组"]',
    1,
    80,
    1,
    NOW(),
    NOW()
);

-- ============================================
-- 插入示例公告
-- ============================================
INSERT INTO `notice` (
    `title`, `content`, `type`, `status`, `top`, 
    `view_count`, `publisher_id`, `publish_time`, 
    `create_time`, `update_time`
) VALUES (
    '欢迎参加凌睿工作室2024招新！',
    '## 欢迎来到凌睿工作室招新平台！\n\n### 招新流程\n\n1. 注册账号并完善个人信息\n2. 选择感兴趣的方向\n3. 完成对应方向的题目\n4. 等待面试通知\n\n### 注意事项\n\n- 请认真对待每一道题目\n- 鼓励独立思考，拒绝抄袭\n- 遇到问题可以联系管理员\n\n祝大家答题顺利！💪',
    'IMPORTANT',
    1,
    1,
    0,
    1,
    NOW(),
    NOW(),
    NOW()
);

INSERT INTO `notice` (
    `title`, `content`, `type`, `status`, `top`, 
    `view_count`, `publisher_id`, `publish_time`, 
    `create_time`, `update_time`
) VALUES (
    '题目更新通知',
    '平台新增了10道题目，涵盖前端、后端、Android、设计、运营等多个方向。\n\n新题目已发布，欢迎大家挑战！🎯',
    'NORMAL',
    1,
    0,
    0,
    1,
    NOW(),
    NOW(),
    NOW()
);

-- ============================================
-- 数据统计
-- ============================================
SELECT '数据初始化完成！' AS status;
SELECT COUNT(*) AS user_count FROM `user`;
SELECT COUNT(*) AS problem_count FROM `problem`;
SELECT COUNT(*) AS notice_count FROM `notice`;