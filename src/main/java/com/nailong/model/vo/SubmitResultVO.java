package com.nailong.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @brief 提交结果VO
 * @author Nailong
 */
@Data
@Schema(description = "提交结果响应")
public class SubmitResultVO implements Serializable {
    
    @Schema(description = "是否正确")
    private Boolean correct;
    
    @Schema(description = "状态")
    private String status;
    
    @Schema(description = "获得分数")
    private Integer score;
    
    @Schema(description = "提示信息")
    private String message;
    
    @Schema(description = "提交ID")
    private Long submissionId;
}