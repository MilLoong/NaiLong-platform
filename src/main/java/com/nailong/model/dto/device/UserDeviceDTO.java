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
    private String deviceId;  ///< 设备ID


    @Schema(description = "设备名称")
    private String deviceName;  ///< 设备名称


    @Schema(description = "设备类型")
    private String deviceType;  ///< 设备类型


    @Schema(description = "操作系统")
    private String deviceOs;  ///< 操作系统


    @Schema(description = "浏览器")
    private String browser;  ///< 浏览器


    @Schema(description = "IP地址")
    private String ip;  ///< IP地址


    @Schema(description = "IP归属地")
    private String location;  ///< IP归属地


    @Schema(description = "User-Agent")
    private String userAgent;  ///< User-Agent


    @Schema(description = "登录时间")
    private LocalDateTime loginTime;  ///< 登录时间


    @Schema(description = "设备指纹")
    private String fingerprint;  ///< 设备指纹

}