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
    
    private Long userId;  ///< 用户ID
    private Long problemId;  ///< 题目ID
    private String answer;  ///< 提交答案
    private String status;  ///< 判题状态 (PENDING-待判题 ACCEPTED-通过 WRONG_ANSWER-答案错误 ERROR-系统错误)
    private Integer score;  ///< 获得分数
    private Integer timeCost;  ///< 耗时(ms) - 编程题使用
    private Integer memoryCost;  ///< 内存消耗(KB) - 编程题使用
    private String language;  ///< 编程语言 (Java/Python/C++)
    private String ip;  ///< 提交IP
    private String remark;  ///< 备注(错误信息等)
}