package com.nailong.model.dto.problem;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @brief 题目查询DTO
 * @author Nailong
 */
@Data
@Schema(description = "题目查询请求")
public class ProblemQueryDTO implements Serializable {
    
    @Schema(description = "题目类型")
    private String type;
    
    @Schema(description = "所属方向")
    private String direction;
    
    @Schema(description = "难度")
    private String difficulty;
    
    @Schema(description = "关键词搜索")
    private String keyword;
    
    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;
    
    @Schema(description = "每页数量", example = "20")
    private Integer pageSize = 20;
}