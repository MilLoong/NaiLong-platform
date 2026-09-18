package com.nailong.model.dto.problem;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @brief 创建题目DTO
 * @author Nailong
 */
@Data
@Schema(description = "创建题目请求")
public class ProblemCreateDTO implements Serializable {
    
    @NotBlank(message = "题目标题不能为空")
    @Schema(description = "题目标题", example = "实现一个简单的计算器")
    private String title;  ///< 题目标题

    
    @NotBlank(message = "题目描述不能为空")
    @Schema(description = "题目描述")
    private String description;  ///< 题目描述

    
    @NotBlank(message = "题目类型不能为空")
    @Schema(description = "题目类型(CHOICE/FLAG/FILE/CODE)", example = "FLAG")
    private String type;
    
    @NotBlank(message = "所属方向不能为空")
    @Schema(description = "所属方向", example = "backend")
    private String direction;  ///< 所属方向

    
    @NotBlank(message = "难度不能为空")
    @Schema(description = "难度(EASY/MEDIUM/HARD)", example = "MEDIUM")
    private String difficulty;
    
    @NotNull(message = "基础分数不能为空")
    @Schema(description = "基础分数", example = "100")
    private Integer baseScore;  ///< 基础分数

    
    @Schema(description = "答案(选择题/Flag题)")
    private String answer;
    
    @Schema(description = "附件URL")
    private String attachmentUrl;  ///< 附件URL

    
    @Schema(description = "提示信息")
    private String hint;  ///< 提示信息

    
    @Schema(description = "标签(逗号分隔)", example = "算法,数据结构")
    private String tags;
    
    @Schema(description = "排序权重", example = "0")
    private Integer sortOrder;  ///< 排序权重

}