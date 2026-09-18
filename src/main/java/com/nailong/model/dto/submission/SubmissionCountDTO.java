package com.nailong.model.dto.submission;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @brief 提交统计DTO
 * @details 用于返回题目提交的统计信息
 * @author Nailong
 */
@Data
@Schema(description = "题目提交统计信息")
public class SubmissionCountDTO implements Serializable {

    @Schema(description = "总提交次数")
    private Integer totalSubmissions;  ///< 总提交次数


    @Schema(description = "成功提交次数")
    private Integer successSubmissions;  ///< 成功提交次数

    
    @Schema(description = "通过率")
    private Double passRate;  ///< 通过率

    
    @Schema(description = "题目ID")
    private Long problemId;  ///< 题目ID

    
    @Schema(description = "题目标题")
    private String problemTitle;  ///< 题目标题

}