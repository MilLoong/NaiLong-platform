package com.nailong.model.dto.problem;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @brief 更新题目DTO
 * @author Nailong
 */
@Data
@Schema(description = "更新题目请求")
public class ProblemUpdateDTO implements Serializable {
    
    @NotNull(message = "题目ID不能为空")
    @Schema(description = "题目ID")
    private Long id;  ///< 题目ID

    
    @Schema(description = "题目标题")
    private String title;  ///< 题目标题

    
    @Schema(description = "题目描述")
    private String description;  ///< 题目描述

    
    @Schema(description = "难度")
    private String difficulty;  ///< 难度

    
    @Schema(description = "答案")
    private String answer;  ///< 答案

    
    @Schema(description = "附件URL")
    private String attachmentUrl;  ///< 附件URL

    
    @Schema(description = "提示信息")
    private String hint;  ///< 提示信息

    
    @Schema(description = "标签")
    private String tags;  ///< 标签

    
    @Schema(description = "状态(0-隐藏 1-发布)")
    private Integer status;
    
    @Schema(description = "排序权重")
    private Integer sortOrder;  ///< 排序权重

}