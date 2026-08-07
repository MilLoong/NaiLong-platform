package com.nailong.controller;

import com.nailong.common.annotation.Log;
import com.nailong.common.result.Result;
import com.nailong.model.dto.device.UserDeviceDTO;
import com.nailong.model.vo.device.UserDeviceVO;
import com.nailong.service.IUserDeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * @brief 用户设备控制器
 * @details 用户设备管理接口
 * @author Nailong
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/device")
@RequiredArgsConstructor
@Tag(name = "用户设备", description = "用户设备管理接口")
public class UserDeviceController {

    private final IUserDeviceService userDeviceService;

    /**
     * @brief 记录用户登录设备
     * @param dto 设备信息请求体
     * @return 设备ID
     */
    @PostMapping("/login")
    @Operation(summary = "记录用户登录设备", description = "记录用户登录的设备信息")
    public Result<String> recordLoginDevice(@RequestBody @Valid UserDeviceDTO dto) {
        // 从认证信息中获取用户ID
        Long userId = getCurrentUserId();
        String deviceId = userDeviceService.recordLoginDevice(userId, dto);
        return Result.success("记录成功", deviceId);
    }

    /**
     * @brief 获取当前用户的所有设备
     * @return 设备列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取用户设备列表", description = "获取当前登录用户的所有设备")
    public Result<List<UserDeviceVO>> getUserDevices() {
        Long userId = getCurrentUserId();
        List<UserDeviceVO> deviceList = userDeviceService.getUserDevices(userId);
        return Result.success("查询成功", deviceList);
    }

    /**
     * @brief 获取设备详情
     * @param deviceId 设备ID
     * @return 设备详情
     */
    @GetMapping("/{deviceId}")
    @Operation(summary = "获取设备详情", description = "获取指定设备的详细信息")
    public Result<UserDeviceVO> getDeviceDetail(@PathVariable String deviceId) {
        UserDeviceVO device = userDeviceService.getDeviceDetail(deviceId);
        return Result.success("查询成功", device);
    }

    /**
     * @brief 下线设备
     * @param deviceId 设备ID
     * @return 操作结果
     */
    @PutMapping("/{deviceId}/offline")
    @Operation(summary = "下线设备", description = "将指定设备下线")
    @Log(operation = "下线设备", remark = "用户将设备下线")
    public Result<Void> offlineDevice(@PathVariable String deviceId) {
        // 验证设备是否属于当前用户
        validateDeviceOwner(deviceId);
        
        userDeviceService.offlineDevice(deviceId);
        return Result.success("下线成功", null);
    }

    /**
     * @brief 删除设备
     * @param deviceId 设备ID
     * @return 操作结果
     */
    @DeleteMapping("/{deviceId}")
    @Operation(summary = "删除设备", description = "删除指定的设备记录")
    @Log(operation = "删除设备", remark = "用户删除设备记录")
    public Result<Void> deleteDevice(@PathVariable String deviceId) {
        // 验证设备是否属于当前用户
        validateDeviceOwner(deviceId);
        
        userDeviceService.deleteDevice(deviceId);
        return Result.success("删除成功", null);
    }

    /**
     * @brief 标记设备为可信/不可信
     * @param deviceId 设备ID
     * @param trusted  可信状态（1 可信，0 不可信）
     * @return 操作结果
     */
    @PutMapping("/{deviceId}/trusted")
    @Operation(summary = "标记设备可信状态", description = "标记设备为可信或不可信")
    @Log(operation = "标记设备可信状态", remark = "用户设置设备可信状态")
    public Result<Void> markDeviceTrusted(@PathVariable String deviceId,
                                        @RequestParam Integer trusted) {
        // 验证设备是否属于当前用户
        validateDeviceOwner(deviceId);
        
        userDeviceService.markDeviceTrusted(deviceId, trusted);
        return Result.success("设置成功", null);
    }

    /**
     * @brief 登出其他设备
     * @param excludeDeviceId 排除的设备ID（可选）
     * @return 操作结果
     */
    @PostMapping("/logout-others")
    @Operation(summary = "登出其他设备", description = "登出当前用户的所有其他设备")
    @Log(operation = "登出其他设备", remark = "用户登出所有其他设备")
    public Result<Void> logoutOtherDevices(@RequestParam(required = false) String excludeDeviceId) {
        Long userId = getCurrentUserId();
        userDeviceService.logoutOtherDevices(userId, excludeDeviceId);
        return Result.success("其他设备已登出", null);
    }

    /**
     * @brief 获取在线设备数
     * @return 在线设备数量
     */
    @GetMapping("/online-count")
    @Operation(summary = "获取在线设备数", description = "获取当前用户的在线设备数量")
    public Result<Integer> countOnlineDevices() {
        Long userId = getCurrentUserId();
        Integer count = userDeviceService.countOnlineDevices(userId);
        return Result.success("查询成功", count);
    }

    /**
     * @brief 检查设备是否可信
     * @param deviceId 设备ID
     * @return 是否可信
     */
    @GetMapping("/{deviceId}/trusted")
    @Operation(summary = "检查设备是否可信", description = "检查指定设备是否是可信设备")
    public Result<Boolean> isDeviceTrusted(@PathVariable String deviceId) {
        Long userId = getCurrentUserId();
        boolean isTrusted = userDeviceService.isDeviceTrusted(userId, deviceId);
        return Result.success("查询成功", isTrusted);
    }

    /**
     * @brief 批量下线过期设备（管理员使用）
     * @param expireMinutes 过期分钟数
     * @return 操作结果
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/batch-offline")
    @Operation(summary = "批量下线过期设备", description = "批量下线长时间未活跃的设备")
    @Log(operation = "批量下线过期设备", remark = "管理员执行")
    public Result<Void> batchOfflineExpiredDevices(@RequestParam(defaultValue = "30") Integer expireMinutes) {
        userDeviceService.batchOfflineExpiredDevices(expireMinutes);
        return Result.success("操作成功", null);
    }

    /**
     * @brief 获取当前登录用户ID
     * @return 用户ID
     */
    private Long getCurrentUserId() {
        // 从Spring Security上下文中获取当前登录用户ID
        // 这里需要根据实际项目中的实现来获取用户ID
        // 示例代码，实际项目需要替换为正确的获取方式
        return (Long) org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }

    /**
     * @brief 验证设备是否属于当前用户
     * @param deviceId 设备ID
     */
    private void validateDeviceOwner(String deviceId) {
        Long userId = getCurrentUserId();
        UserDeviceVO device = userDeviceService.getDeviceDetail(deviceId);
        if (device == null || !device.getId().equals(userId)) {
            throw new com.nailong.common.exception.BusinessException(
                    com.nailong.common.enums.ResultCode.FORBIDDEN);
        }
    }
}
