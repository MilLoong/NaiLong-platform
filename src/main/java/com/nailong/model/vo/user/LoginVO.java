package com.nailong.model.vo.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @brief 登录响应VO
 * @author Nailong
 */
@Data
@Schema(description = "登录响应")
public class LoginVO implements Serializable {
    
    @Schema(description = "访问令牌")
    private String accessToken;  ///< 访问令牌

    
    @Schema(description = "刷新令牌")
    private String refreshToken;  ///< 刷新令牌

    
    @Schema(description = "Token类型", example = "Bearer")
    private String tokenType = "Bearer";  ///< Token类型

    
    @Schema(description = "过期时间(秒)", example = "7200")
    private Long expiresIn;
    
    @Schema(description = "用户信息")
    private UserInfoVO userInfo;  ///< 用户信息

}