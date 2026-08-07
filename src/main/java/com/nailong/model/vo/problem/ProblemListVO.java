package com.nailong.model.vo.problem;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @brief 题目列表VO
 * @author Nailong
 */
@Data
@Schema(description = "题目列表项")
public class ProblemListVO implements Serializable {
    
    @Schema(description = "题目ID")
    private Long id;
    
    @Schema(description = "题目标题")
    private String title;
    
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
    
    @Schema(description = "标签")
    private String tags;
    
    @Schema(description = "是否已解决")
    private Boolean solved;
}