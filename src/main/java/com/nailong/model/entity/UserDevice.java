package com.nailong.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @brief 用户设备实体类
 * @details 用于记录用户的登录设备信息，支持多设备管理和安全验证
 * @author Nailong
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_device")
public class UserDevice extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 设备ID (唯一标识)
     */
    private String deviceId;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备类型 (PC/MOBILE/TABLET)
     */
    private String deviceType;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * IP地址
     */
    private String ip;

    /**
     * IP归属地
     */
    private String location;

    /**
     * 登录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime loginTime;

    /**
     * 最后活跃时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime lastActiveTime;

    /**
     * 状态 (0-离线 1-在线)
     */
    private Integer status;

    /**
     * 是否可信设备 (0-否 1-是)
     */
    private Integer trusted;

    /**
     * 设备指纹信息
     */
    private String fingerprint;
    
    /**
     * User-Agent信息
     */
    private String userAgent;
}
