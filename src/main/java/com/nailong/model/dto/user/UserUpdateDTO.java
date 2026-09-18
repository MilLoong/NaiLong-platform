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
    private String nickname;  ///< 昵称

    
    @Schema(description = "头像URL")
    private String avatar;  ///< 头像URL

    
    @Schema(description = "手机号")
    private String phone;  ///< 手机号

    
    @Schema(description = "年级")
    private String grade;  ///< 年级

    
    @Schema(description = "专业")
    private String major;  ///< 专业

    
    @Schema(description = "招新方向")
    private String direction;  ///< 招新方向

}