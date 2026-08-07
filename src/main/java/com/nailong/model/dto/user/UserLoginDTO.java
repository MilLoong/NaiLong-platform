package com.nailong.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @brief 用户登录DTO
 * @author Nailong
 */
@Data
@Schema(description = "用户登录请求")
public class UserLoginDTO implements Serializable {
    
    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名或邮箱", example = "nailong2024")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码", example = "Password123")
    private String password;
}