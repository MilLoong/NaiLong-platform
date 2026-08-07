package com.nailong.model.vo.log;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @brief 操作日志VO
 * @author Nailong
 */
@Data
@Schema(description = "操作日志响应")
public class OperationLogVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "日志ID")
    private Long id;

    @Schema(description = "操作用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "操作类型")
    private String operation;

    @Schema(description = "请求方法")
    private String method;

    @Schema(description = "请求参数")
    private String params;

    @Schema(description = "返回结果")
    private String result;

    @Schema(description = "操作IP")
    private String ip;

    @Schema(description = "IP归属地")
    private String location;

    @Schema(description = "耗时(ms)")
    private Integer timeCost;

    @Schema(description = "状态(0-失败 1-成功)")
    private Integer status;

    @Schema(description = "错误信息")
    private String errorMsg;

    @Schema(description = "操作时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}