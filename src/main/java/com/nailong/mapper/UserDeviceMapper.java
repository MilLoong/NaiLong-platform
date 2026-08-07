package com.nailong.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nailong.model.entity.UserDevice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @brief 用户设备Mapper接口
 * @author Nailong
 */
@Mapper
public interface UserDeviceMapper extends BaseMapper<UserDevice> {

    /**
     * @brief 根据用户ID查询设备列表
     * @param userId 用户ID
     * @return 设备列表
     */
    List<UserDevice> selectByUserId(@Param("userId") Long userId);

    /**
     * @brief 根据设备ID查询设备
     * @param deviceId 设备ID
     * @return 设备信息
     */
    UserDevice selectByDeviceId(@Param("deviceId") String deviceId);

    /**
     * @brief 根据用户ID和设备ID查询设备
     * @param userId   用户ID
     * @param deviceId 设备ID
     * @return 设备信息
     */
    UserDevice selectByUserIdAndDeviceId(@Param("userId") Long userId,
                                        @Param("deviceId") String deviceId);

    /**
     * @brief 更新设备最后活跃时间
     * @param id             设备记录ID
     * @param lastActiveTime 最后活跃时间
     * @return 影响的行数
     */
    int updateLastActiveTime(@Param("id") Long id, @Param("lastActiveTime") LocalDateTime lastActiveTime);

    /**
     * @brief 更新设备在线状态
     * @param id     设备记录ID
     * @param status 状态（0-离线，1-在线）
     * @return 影响的行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * @brief 标记设备为可信或不可信
     * @param id      设备记录ID
     * @param trusted 是否可信（0-否，1-是）
     * @return 影响的行数
     */
    int updateTrusted(@Param("id") Long id, @Param("trusted") Integer trusted);

    /**
     * @brief 登出用户所有设备（排除当前设备）
     * @param userId          用户ID
     * @param excludeDeviceId 排除的设备ID
     * @return 影响的行数
     */
    int logoutOtherDevices(@Param("userId") Long userId,
                          @Param("excludeDeviceId") String excludeDeviceId);

    /**
     * @brief 统计用户在线设备数
     * @param userId 用户ID
     * @return 在线设备数量
     */
    Long countOnlineDevices(@Param("userId") Long userId);
}