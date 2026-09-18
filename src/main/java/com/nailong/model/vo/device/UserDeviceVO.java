package com.nailong.model.vo.device;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @brief 用户设备VO
 * @author Nailong
 */
@Data
@Schema(description = "用户设备响应")
public class UserDeviceVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "设备ID")
    private Long id;  ///< 设备ID


    @Schema(description = "设备标识")
    private String deviceId;  ///< 设备标识


    @Schema(description = "设备名称")
    private String deviceName;  ///< 设备名称


    @Schema(description = "设备类型")
    private String deviceType;  ///< 设备类型


    @Schema(description = "操作系统")
    private String os;  ///< 操作系统


    @Schema(description = "浏览器")
    private String browser;  ///< 浏览器


    @Schema(description = "IP地址")
    private String ip;  ///< IP地址


    @Schema(description = "IP归属地")
    private String location;  ///< IP归属地


    @Schema(description = "登录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime loginTime;  ///< 登录时间


    @Schema(description = "最后活跃时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime lastActiveTime;  ///< 最后活跃时间


    @Schema(description = "状态(0-离线 1-在线)")
    private Integer status;

    @Schema(description = "是否可信设备(0-否 1-是)")
    private Integer trusted;

    @Schema(description = "是否当前设备")
    private Boolean currentDevice = false;  ///< 是否当前设备

}