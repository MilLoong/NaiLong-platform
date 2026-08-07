package com.nailong.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @brief 提交记录实体类
 * @author Nailong
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("submission")
public class Submission extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 题目ID
     */
    private Long problemId;
    
    /**
     * 提交答案
     */
    private String answer;
    
    /**
     * 判题状态 (PENDING-待判题 ACCEPTED-通过 WRONG_ANSWER-答案错误 ERROR-系统错误)
     */
    private String status;
    
    /**
     * 获得分数
     */
    private Integer score;
    
    /**
     * 耗时(ms) - 编程题使用
     */
    private Integer timeCost;
    
    /**
     * 内存消耗(KB) - 编程题使用
     */
    private Integer memoryCost;
    
    /**
     * 编程语言 (Java/Python/C++)
     */
    private String language;
    
    /**
     * 提交IP
     */
    private String ip;
    
    /**
     * 备注(错误信息等)
     */
    private String remark;
}