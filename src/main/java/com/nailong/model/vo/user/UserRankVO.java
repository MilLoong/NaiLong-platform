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
    private Long userId;  ///< 用户ID

    
    @Schema(description = "用户名")
    private String username;  ///< 用户名

    
    @Schema(description = "昵称")
    private String nickname;  ///< 昵称

    
    @Schema(description = "头像")
    private String avatar;  ///< 头像

    
    @Schema(description = "年级")
    private String grade;  ///< 年级

    
    @Schema(description = "方向")
    private String direction;  ///< 方向

    
    @Schema(description = "总积分")
    private Integer totalScore;  ///< 总积分

    
    @Schema(description = "解题数")
    private Integer solvedCount;  ///< 解题数

    
    @Schema(description = "排名")
    private Long rank;  ///< 排名

}