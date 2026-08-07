package com.nailong.model.vo.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @brief 用户信息VO
 * @author Nailong
 */
@Data
@Schema(description = "用户信息")
public class UserInfoVO implements Serializable {
    
    @Schema(description = "用户ID")
    private Long id;
    
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "邮箱")
    private String email;
    
    @Schema(description = "昵称")
    private String nickname;
    
    @Schema(description = "头像URL")
    private String avatar;
    
    @Schema(description = "学号")
    private String studentId;
    
    @Schema(description = "真实姓名")
    private String realName;
    
    @Schema(description = "年级")
    private String grade;
    
    @Schema(description = "专业")
    private String major;
    
    @Schema(description = "招新方向")
    private String direction;
    
    @Schema(description = "角色")
    private String role;
    
    @Schema(description = "总积分")
    private Integer totalScore;
    
    @Schema(description = "解题数量")
    private Integer solvedCount;
    
    @Schema(description = "最后登录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime lastLoginTime;
    
    @Schema(description = "注册时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}