package com.nailong.service;

import com.nailong.model.dto.device.UserDeviceDTO;
import com.nailong.model.entity.UserDevice;
import com.nailong.model.vo.device.UserDeviceVO;

import java.util.List;

/**
 * @brief 用户设备服务接口
 * @author Nailong
 */
public interface IUserDeviceService {

    /**
     * @brief 记录用户登录设备
     * @param userId 用户 ID
     * @param dto    设备信息
     * @return 设备 ID
     */
    String recordLoginDevice(Long userId, UserDeviceDTO dto);

    /**
     * @brief 更新设备最后活跃时间
     * @param deviceId 设备 ID
     */
    void updateDeviceActiveTime(String deviceId);
    
    /**
     * @brief 更新设备最后活跃时间（通过用户 ID 和设备信息）
     * @param userId    用户 ID
     * @param userAgent 用户代理字符串
     * @param ip        客户端 IP 地址
     */
    void updateDeviceActiveTime(Long userId, String userAgent, String ip);

    /**
     * @brief 获取用户所有设备
     * @param userId 用户 ID
     * @return 设备列表
     */
    List<UserDeviceVO> getUserDevices(Long userId);

    /**
     * @brief 获取设备详情
     * @param deviceId 设备 ID
     * @return 设备详情
     */
    UserDeviceVO getDeviceDetail(String deviceId);

    /**
     * @brief 下线设备
     * @param deviceId 设备 ID
     * @details 使该设备上的会话失效
     */
    void offlineDevice(String deviceId);

    /**
     * @brief 删除设备
     * @param deviceId 设备 ID
     */
    void deleteDevice(String deviceId);

    /**
     * @brief 标记设备为可信/不可信
     * @param deviceId 设备 ID
     * @param trusted  是否可信（0-否 1-是）
     */
    void markDeviceTrusted(String deviceId, Integer trusted);

    /**
     * @brief 登出用户所有其他设备
     * @param userId          用户 ID
     * @param excludeDeviceId 排除的设备 ID（当前设备）
     */
    void logoutOtherDevices(Long userId, String excludeDeviceId);

    /**
     * @brief 统计用户在线设备数
     * @param userId 用户 ID
     * @return 在线设备数
     */
    Integer countOnlineDevices(Long userId);

    /**
     * @brief 检查设备是否可信
     * @param userId   用户 ID
     * @param deviceId 设备 ID
     * @return 是否可信
     */
    boolean isDeviceTrusted(Long userId, String deviceId);

    /**
     * @brief 批量下线过期设备
     * @param expireMinutes 过期分钟数
     * @details 将超过指定时间未活跃的设备标记为离线
     */
    void batchOfflineExpiredDevices(Integer expireMinutes);
}
