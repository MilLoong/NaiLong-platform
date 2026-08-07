package com.nailong.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * @brief 用户注册DTO
 * @author Nailong
 */
@Data
@Schema(description = "用户注册请求")
public class UserRegisterDTO implements Serializable {
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度为4-20个字符")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    @Schema(description = "用户名", example = "nailong2024")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 20, message = "密码长度为8-20个字符")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,20}$",
            message = "密码需包含大小写字母和数字，长度8-20")
    @Schema(description = "密码", example = "Password123")
    private String password;
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱", example = "user@example.com")
    private String email;
    
    @NotBlank(message = "验证码不能为空")
    @Size(min = 6, max = 6, message = "验证码为6位数字")
    @Schema(description = "邮箱验证码", example = "123456")
    private String code;
    
    @Schema(description = "昵称", example = "凌睿新人")
    private String nickname;
    
    @Schema(description = "学号", example = "2024001")
    private String studentId;
    
    @Schema(description = "真实姓名", example = "张三")
    private String realName;
    
    @Schema(description = "年级", example = "2024")
    private String grade;
    
    @Schema(description = "专业", example = "计算机科学与技术")
    private String major;
    
    @Schema(description = "招新方向", example = "backend")
    private String direction;
}