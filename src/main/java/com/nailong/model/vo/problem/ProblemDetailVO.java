package com.nailong.model.vo.problem;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @brief 题目详情VO
 * @author Nailong
 */
@Data
@Schema(description = "题目详情")
public class ProblemDetailVO implements Serializable {
    
    @Schema(description = "题目ID")
    private Long id;
    
    @Schema(description = "题目标题")
    private String title;
    
    @Schema(description = "题目描述")
    private String description;
    
    @Schema(description = "题目类型")
    private String type;
    
    @Schema(description = "所属方向")
    private String direction;
    
    @Schema(description = "难度")
    private String difficulty;
    
    @Schema(description = "当前分数")
    private Integer currentScore;
    
    @Schema(description = "解题人数")
    private Integer solvedCount;
    
    @Schema(description = "提交次数")
    private Integer submitCount;
    
    @Schema(description = "附件URL")
    private String attachmentUrl;
    
    @Schema(description = "提示信息")
    private String hint;
    
    @Schema(description = "标签")
    private String tags;
    
    @Schema(description = "是否已解决")
    private Boolean solved;
    
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}