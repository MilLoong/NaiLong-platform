package com.nailong.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


import java.io.Serializable;

/**
 * @brief 更新用户信息DTO
 * @author Nailong
 */
@Data
@Schema(description = "更新用户信息请求")
public class UserUpdateDTO implements Serializable {
    
    @Schema(description = "昵称")
    private String nickname;
    
    @Schema(description = "头像URL")
    private String avatar;
    
    @Schema(description = "手机号")
    private String phone;
    
    @Schema(description = "年级")
    private String grade;
    
    @Schema(description = "专业")
    private String major;
    
    @Schema(description = "招新方向")
    private String direction;
}