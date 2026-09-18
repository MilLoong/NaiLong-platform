package com.nailong.model.vo.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @brief 用户资料VO
 * @author Nailong
 */
@Data
@Schema(description = "用户资料信息")
public class UserProfileVO implements Serializable {

    @Schema(description = "用户ID")
    private Long id;  ///< 用户ID


    @Schema(description = "用户名")
    private String username;  ///< 用户名


    @Schema(description = "昵称")
    private String nickname;  ///< 昵称


    @Schema(description = "头像URL")
    private String avatar;  ///< 头像URL


    @Schema(description = "学号")
    private String studentId;  ///< 学号


    @Schema(description = "真实姓名")
    private String realName;  ///< 真实姓名


    @Schema(description = "年级")
    private String grade;  ///< 年级


    @Schema(description = "专业")
    private String major;  ///< 专业


    @Schema(description = "招新方向")
    private String direction;  ///< 招新方向


    @Schema(description = "总积分")
    private Integer totalScore;  ///< 总积分


    @Schema(description = "解题数量")
    private Integer solvedCount;  ///< 解题数量


    @Schema(description = "注册时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;  ///< 注册时间

}