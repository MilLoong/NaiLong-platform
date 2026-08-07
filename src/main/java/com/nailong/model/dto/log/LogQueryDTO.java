package com.nailong.model.dto.log;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @brief 操作日志查询DTO
 * @author Nailong
 */
@Data
@Schema(description = "操作日志查询请求")
public class LogQueryDTO {

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "操作类型")
    private String operation;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "当前页", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;
}