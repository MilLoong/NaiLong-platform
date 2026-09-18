package com.nailong.model.dto.notice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * @brief 公告创建请求参数
 * @author Nailong
 */
@Data
@Schema(description = "公告创建请求参数")
public class NoticeCreateDTO {

    @Schema(description = "公告标题")
    @NotBlank(message = "公告标题不能为空")
    @Size(max = 200, message = "公告标题不能超过200个字符")
    private String title;  ///< 公告标题


    @Schema(description = "公告内容")
    @NotBlank(message = "公告内容不能为空")
    private String content;  ///< 公告内容


    @Schema(description = "公告类型(NORMAL/IMPORTANT/URGENT)", defaultValue = "NORMAL")
    private String type = "NORMAL";

    @Schema(description = "是否置顶(0-否 1-是)", defaultValue = "0")
    private Integer top = 0;

    @Schema(description = "状态(0-隐藏 1-发布)", defaultValue = "1")
    private Integer status = 1;
}
