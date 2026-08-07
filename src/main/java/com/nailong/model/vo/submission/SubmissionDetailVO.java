package com.nailong.model.vo.submission;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @brief 提交详情VO
 * @author Nailong
 */
@Data
@Schema(description = "提交记录详情")
public class SubmissionDetailVO implements Serializable {
    
    @Schema(description = "提交ID")
    private Long id;
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "题目ID")
    private Long problemId;
    
    @Schema(description = "题目标题")
    private String problemTitle;
    
    @Schema(description = "提交答案")
    private String answer;
    
    @Schema(description = "判题状态")
    private String status;
    
    @Schema(description = "获得分数")
    private Integer score;
    
    @Schema(description = "编程语言")
    private String language;
    
    @Schema(description = "耗时(ms)")
    private Integer timeCost;
    
    @Schema(description = "内存消耗(KB)")
    private Integer memoryCost;
    
    @Schema(description = "备注")
    private String remark;
    
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}