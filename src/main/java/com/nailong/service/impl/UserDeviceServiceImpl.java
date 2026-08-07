package com.nailong.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nailong.common.enums.ResultCode;
import com.nailong.common.exception.BusinessException;
import com.nailong.mapper.UserDeviceMapper;
import com.nailong.model.dto.device.UserDeviceDTO;
import com.nailong.model.entity.UserDevice;
import com.nailong.model.vo.device.UserDeviceVO;
import com.nailong.service.IUserDeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @brief 用户设备服务实现
 * @details 管理登录设备记录、在线状态与可信设备标记
 * @author Nailong
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDeviceServiceImpl extends ServiceImpl<UserDeviceMapper, UserDevice> implements IUserDeviceService {

    private final UserDeviceMapper userDeviceMapper;

    /**
     * @brief 记录登录设备
     * @param userId 用户 ID
     * @param dto    设备信息
     * @return 设备 ID
     * @details 设备不存在则新建，已存在则更新活跃时间
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String recordLoginDevice(Long userId, UserDeviceDTO dto) {
        if (userId == null || dto == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }

        String deviceId = dto.getDeviceId();
        if (deviceId == null) {
            String userAgent = dto.getUserAgent();
            String ip = dto.getIp();
            deviceId = "device_" + (userAgent != null ? userAgent.substring(0, Math.min(20, userAgent.length())) : "unknown")
                    + "_" + (ip != null ? ip : "unknown")
                    + "_" + System.currentTimeMillis();
            dto.setDeviceId(deviceId);
        }

        UserDevice device = userDeviceMapper.selectByUserIdAndDeviceId(userId, deviceId);

        if (device == null) {
            device = new UserDevice();
            BeanUtil.copyProperties(dto, device);
            if (dto.getDeviceOs() != null) {
                device.setOs(dto.getDeviceOs());
            }
            device.setUserId(userId);
            device.setLoginTime(LocalDateTime.now());
            device.setLastActiveTime(LocalDateTime.now());
            device.setStatus(1);
            device.setTrusted(0);

            userDeviceMapper.insert(device);
            log.info("创建设备记录成功: userId={}, deviceId={}", userId, deviceId);
        } else {
            BeanUtil.copyProperties(dto, device);
            if (dto.getDeviceOs() != null) {
                device.setOs(dto.getDeviceOs());
            }
            device.setLastActiveTime(LocalDateTime.now());
            device.setStatus(1);

            userDeviceMapper.updateById(device);
            log.info("更新设备记录成功: userId={}, deviceId={}", userId, deviceId);
        }

        return deviceId;
    }

    /**
     * @brief 更新设备最后活跃时间
     * @param deviceId 设备 ID
     */
    @Override
    public void updateDeviceActiveTime(String deviceId) {
        if (deviceId == null) {
            return;
        }

        UserDevice device = userDeviceMapper.selectByDeviceId(deviceId);
        if (device != null) {
            userDeviceMapper.updateLastActiveTime(device.getId(), LocalDateTime.now());
        }
    }

    /**
     * @brief 根据 User-Agent 与 IP 更新设备活跃时间
     * @param userId    用户 ID
     * @param userAgent 浏览器 User-Agent
     * @param ip        客户端 IP
     * @details 通过 User-Agent 前缀与 IP 匹配已有设备记录
     */
    @Override
    public void updateDeviceActiveTime(Long userId, String userAgent, String ip) {
        if (userId == null || userAgent == null || ip == null) {
            return;
        }

        List<UserDevice> deviceList = userDeviceMapper.selectByUserId(userId);
        if (CollectionUtils.isEmpty(deviceList)) {
            return;
        }

        for (UserDevice device : deviceList) {
            if (device.getUserAgent() != null && device.getUserAgent().contains(userAgent.substring(0, Math.min(30, userAgent.length()))) &&
                device.getIp() != null && device.getIp().equals(ip)) {
                userDeviceMapper.updateLastActiveTime(device.getId(), LocalDateTime.now());
                log.info("更新设备活跃时间成功: userId={}, deviceId={}", userId, device.getDeviceId());
                return;
            }
        }

        log.debug("未找到匹配的设备: userId={}, userAgent={}, ip={}", userId, userAgent, ip);
    }

    /**
     * @brief 获取用户设备列表
     * @param userId 用户 ID
     * @return 设备 VO 列表
     * @details 最后活跃时间在 5 分钟内视为在线
     */
    @Override
    public List<UserDeviceVO> getUserDevices(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }

        List<UserDevice> deviceList = userDeviceMapper.selectByUserId(userId);
        if (CollectionUtils.isEmpty(deviceList)) {
            return new ArrayList<>();
        }

        return deviceList.stream()
                .map(device -> {
                    UserDeviceVO vo = BeanUtil.copyProperties(device, UserDeviceVO.class);
                    boolean online = device.getLastActiveTime() != null &&
                            ChronoUnit.MINUTES.between(device.getLastActiveTime(), LocalDateTime.now()) <= 5;
                    vo.setStatus(online ? 1 : 0);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    /**
     * @brief 获取设备详情
     * @param deviceId 设备 ID
     * @return 设备 VO
     */
    @Override
    public UserDeviceVO getDeviceDetail(String deviceId) {
        if (deviceId == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }

        UserDevice device = userDeviceMapper.selectByDeviceId(deviceId);
        if (device == null) {
            throw new BusinessException(ResultCode.DEVICE_NOT_FOUND);
        }

        UserDeviceVO vo = BeanUtil.copyProperties(device, UserDeviceVO.class);
        boolean online = device.getLastActiveTime() != null &&
                ChronoUnit.MINUTES.between(device.getLastActiveTime(), LocalDateTime.now()) <= 5;
        vo.setStatus(online ? 1 : 0);
        return vo;
    }

    /**
     * @brief 设备下线
     * @param deviceId 设备 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void offlineDevice(String deviceId) {
        if (deviceId == null) {
            return;
        }

        UserDevice device = userDeviceMapper.selectByDeviceId(deviceId);
        if (device != null) {
            userDeviceMapper.updateStatus(device.getId(), 0);
            log.info("设备下线成功: deviceId={}", deviceId);
        }
    }

    /**
     * @brief 删除设备记录
     * @param deviceId 设备 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDevice(String deviceId) {
        if (deviceId == null) {
            return;
        }

        UserDevice device = userDeviceMapper.selectByDeviceId(deviceId);
        if (device != null) {
            userDeviceMapper.deleteById(device.getId());
            log.info("删除设备成功: deviceId={}", deviceId);
        }
    }

    /**
     * @brief 标记设备可信状态
     * @param deviceId 设备 ID
     * @param trusted  是否可信（1 可信，0 不可信）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markDeviceTrusted(String deviceId, Integer trusted) {
        if (deviceId == null || trusted == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }

        UserDevice device = userDeviceMapper.selectByDeviceId(deviceId);
        if (device != null) {
            userDeviceMapper.updateTrusted(device.getId(), trusted);
            log.info("标记设备可信状态成功: deviceId={}, trusted={}", deviceId, trusted);
        }
    }

    /**
     * @brief 登出用户其他设备
     * @param userId          用户 ID
     * @param excludeDeviceId 保留在线的设备 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void logoutOtherDevices(Long userId, String excludeDeviceId) {
        if (userId == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }

        userDeviceMapper.logoutOtherDevices(userId, excludeDeviceId);
        log.info("登出用户其他设备成功: userId={}", userId);
    }

    /**
     * @brief 统计用户在线设备数
     * @param userId 用户 ID
     * @return 在线设备数量
     * @details 最后活跃时间在 5 分钟内视为在线
     */
    @Override
    public Integer countOnlineDevices(Long userId) {
        if (userId == null) {
            return 0;
        }

        LambdaQueryWrapper<UserDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDevice::getUserId, userId)
                .ge(UserDevice::getLastActiveTime, LocalDateTime.now().minusMinutes(5))
                .eq(UserDevice::getDeleted, 0);

        return userDeviceMapper.selectCount(wrapper).intValue();
    }

    /**
     * @brief 检查设备是否可信
     * @param userId   用户 ID
     * @param deviceId 设备 ID
     * @return 是否可信
     */
    @Override
    public boolean isDeviceTrusted(Long userId, String deviceId) {
        if (userId == null || deviceId == null) {
            return false;
        }

        UserDevice device = userDeviceMapper.selectByUserIdAndDeviceId(userId, deviceId);
        return device != null && device.getTrusted() != null && device.getTrusted() == 1;
    }

    /**
     * @brief 批量下线过期设备
     * @param expireMinutes 过期阈值（分钟），默认 30
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchOfflineExpiredDevices(Integer expireMinutes) {
        if (expireMinutes == null || expireMinutes <= 0) {
            expireMinutes = 30;
        }

        LambdaQueryWrapper<UserDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(UserDevice::getLastActiveTime, LocalDateTime.now().minusMinutes(expireMinutes))
                .eq(UserDevice::getStatus, 1)
                .eq(UserDevice::getDeleted, 0);

        List<UserDevice> deviceList = userDeviceMapper.selectList(wrapper);
        if (!CollectionUtils.isEmpty(deviceList)) {
            for (UserDevice device : deviceList) {
                userDeviceMapper.updateStatus(device.getId(), 0);
            }
            log.info("批量下线过期设备成功，共下线{}台设备", deviceList.size());
        }
    }
}
