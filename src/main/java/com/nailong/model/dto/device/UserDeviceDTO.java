package com.nailong.model.dto.device;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @brief 用户设备DTO
 * @author Nailong
 */
@Data
@Schema(description = "用户设备请求")
public class UserDeviceDTO {

    @Schema(description = "设备ID")
    private String deviceId;

    @Schema(description = "设备名称")
    private String deviceName;

    @Schema(description = "设备类型")
    private String deviceType;

    @Schema(description = "操作系统")
    private String deviceOs;

    @Schema(description = "浏览器")
    private String browser;

    @Schema(description = "IP地址")
    private String ip;

    @Schema(description = "IP归属地")
    private String location;

    @Schema(description = "User-Agent")
    private String userAgent;

    @Schema(description = "登录时间")
    private LocalDateTime loginTime;

    @Schema(description = "设备指纹")
    private String fingerprint;
}