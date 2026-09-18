package com.nailong.model.vo.submission;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @brief 提交列表VO
 * @author Nailong
 */
@Data
@Schema(description = "提交记录列表项")
public class SubmissionListVO implements Serializable {
    
    @Schema(description = "提交ID")
    private Long id;  ///< 提交ID

    
    @Schema(description = "题目ID")
    private Long problemId;  ///< 题目ID

    
    @Schema(description = "题目标题")
    private String problemTitle;  ///< 题目标题

    
    @Schema(description = "判题状态")
    private String status;  ///< 判题状态

    
    @Schema(description = "获得分数")
    private Integer score;  ///< 获得分数

    
    @Schema(description = "编程语言")
    private String language;  ///< 编程语言

    
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;  ///< 创建时间

}