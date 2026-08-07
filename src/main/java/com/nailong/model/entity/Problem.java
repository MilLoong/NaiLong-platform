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
    
    /**
     * 题目标题
     */
    private String title;
    
    /**
     * 题目描述
     */
    private String description;
    
    /**
     * 题目类型 (CHOICE-选择题 FLAG-Flag题 FILE-附件题 CODE-编程题)
     */
    private String type;
    
    /**
     * 所属方向 (frontend/backend/android/design/operations)
     */
    private String direction;
    
    /**
     * 难度 (EASY/MEDIUM/HARD)
     */
    private String difficulty;
    
    /**
     * 基础分数
     */
    private Integer baseScore;
    
    /**
     * 当前分数(衰减后)
     */
    private Integer currentScore;
    
    /**
     * 衰减率
     */
    private BigDecimal decayRate;
    
    /**
     * 最低分数
     */
    private Integer minScore;
    
    /**
     * 解题人数
     */
    private Integer solvedCount;
    
    /**
     * 提交次数
     */
    private Integer submitCount;
    
    /**
     * 答案(选择题/Flag题)
     */
    private String answer;
    
    /**
     * 附件URL
     */
    private String attachmentUrl;
    
    /**
     * 提示信息
     */
    private String hint;
    
    /**
     * 标签(JSON数组)
     */
    private String tags;
    
    /**
     * 状态 (0-隐藏 1-发布)
     */
    private Integer status;
    
    /**
     * 排序权重
     */
    private Integer sortOrder;
    
    /**
     * 创建者ID
     */
    private Long creatorId;
}