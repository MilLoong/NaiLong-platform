package com.nailong.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * @brief 重置密码DTO
 * @author Nailong
 */
@Data
@Schema(description = "重置密码请求")
public class PasswordResetDTO implements Serializable {
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱", example = "user@example.com")
    private String email;
    
    @NotBlank(message = "验证码不能为空")
    @Schema(description = "验证码", example = "123456")
    private String code;
    
    @NotBlank(message = "新密码不能为空")
    @Size(min = 8, max = 20, message = "密码长度为8-20个字符")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,20}$",
            message = "密码需包含大小写字母和数字，长度8-20")
    @Schema(description = "新密码", example = "NewPassword123")
    private String newPassword;
}