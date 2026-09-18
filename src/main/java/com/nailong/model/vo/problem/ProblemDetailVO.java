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
    private Long id;  ///< 题目ID

    
    @Schema(description = "题目标题")
    private String title;  ///< 题目标题

    
    @Schema(description = "题目描述")
    private String description;  ///< 题目描述

    
    @Schema(description = "题目类型")
    private String type;  ///< 题目类型

    
    @Schema(description = "所属方向")
    private String direction;  ///< 所属方向

    
    @Schema(description = "难度")
    private String difficulty;  ///< 难度

    
    @Schema(description = "当前分数")
    private Integer currentScore;  ///< 当前分数

    
    @Schema(description = "解题人数")
    private Integer solvedCount;  ///< 解题人数

    
    @Schema(description = "提交次数")
    private Integer submitCount;  ///< 提交次数

    
    @Schema(description = "附件URL")
    private String attachmentUrl;  ///< 附件URL

    
    @Schema(description = "提示信息")
    private String hint;  ///< 提示信息

    
    @Schema(description = "标签")
    private String tags;  ///< 标签

    
    @Schema(description = "是否已解决")
    private Boolean solved;  ///< 是否已解决

    
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;  ///< 创建时间

}