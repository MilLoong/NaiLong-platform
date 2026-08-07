package com.nailong.model.vo.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @brief 用户排名VO
 * @author Nailong
 */
@Data
@Schema(description = "用户排名信息")
public class UserRankVO implements Serializable {
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "昵称")
    private String nickname;
    
    @Schema(description = "头像")
    private String avatar;
    
    @Schema(description = "年级")
    private String grade;
    
    @Schema(description = "方向")
    private String direction;
    
    @Schema(description = "总积分")
    private Integer totalScore;
    
    @Schema(description = "解题数")
    private Integer solvedCount;
    
    @Schema(description = "排名")
    private Long rank;
}