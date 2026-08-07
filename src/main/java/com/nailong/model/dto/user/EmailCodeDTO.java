package com.nailong.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @brief 发送邮箱验证码DTO
 * @author Nailong
 */
@Data
@Schema(description = "发送验证码请求")
public class EmailCodeDTO implements Serializable {
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱地址", example = "user@example.com")
    private String email;
    
    @NotBlank(message = "验证码类型不能为空")
    @Schema(description = "验证码类型(register/reset)", example = "register")
    private String type;
}