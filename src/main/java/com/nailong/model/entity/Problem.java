package com.nailong.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @brief 题目实体类
 * @author Nailong
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("problem")
public class Problem extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    private String title;  ///< 题目标题
    private String description;  ///< 题目描述
    private String type;  ///< 题目类型 (CHOICE-选择题 FLAG-Flag题 FILE-附件题 CODE-编程题)
    private String direction;  ///< 所属方向 (frontend/backend/android/design/operations)
    private String difficulty;  ///< 难度 (EASY/MEDIUM/HARD)
    private Integer baseScore;  ///< 基础分数
    private Integer currentScore;  ///< 当前分数(衰减后)
    private BigDecimal decayRate;  ///< 衰减率
    private Integer minScore;  ///< 最低分数
    private Integer solvedCount;  ///< 解题人数
    private Integer submitCount;  ///< 提交次数
    private String answer;  ///< 答案(选择题/Flag题)
    private String attachmentUrl;  ///< 附件URL
    private String hint;  ///< 提示信息
    private String tags;  ///< 标签(JSON数组)
    private Integer status;  ///< 状态 (0-隐藏 1-发布)
    private Integer sortOrder;  ///< 排序权重
    private Long creatorId;  ///< 创建者ID
}