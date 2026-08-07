package com.nailong.model.dto.problem;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @brief 提交答案DTO
 * @author Nailong
 */
@Data
@Schema(description = "提交答案请求")
public class SubmitAnswerDTO implements Serializable {
    
    @NotNull(message = "题目ID不能为空")
    @Schema(description = "题目ID", example = "1")
    private Long problemId;
    
    @NotBlank(message = "答案不能为空")
    @Schema(description = "答案内容", example = "flag{hello_world}")
    private String answer;
    
    @Schema(description = "编程语言(编程题使用)", example = "Java")
    private String language;
}