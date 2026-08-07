package com.nailong.controller;

import com.nailong.common.annotation.RateLimit;
import com.nailong.common.result.Result;
import com.nailong.model.dto.user.UserUpdateDTO;
import com.nailong.model.vo.user.UserInfoVO;
import com.nailong.model.vo.user.UserProfileVO;
import com.nailong.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @brief 用户控制器
 * @details 用户相关 API
 * @author Nailong
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "用户管理", description = "用户相关API")
public class UserController {

    private final IUserService userService;

    @Autowired
    public UserController(IUserService userService) {
        this.userService = userService;
    }

    /**
     * @brief 获取当前登录用户信息
     * @param userId 当前用户ID
     * @return 用户信息
     */
    @GetMapping("/current")
    @RateLimit(qps = 10)
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    @PreAuthorize("isAuthenticated()")
    public Result<UserInfoVO> getCurrentUserInfo(@RequestAttribute("userId") Long userId) {
        UserInfoVO userInfo = userService.getCurrentUserInfo(userId);
        return Result.success("获取用户信息成功", userInfo);
    }

    /**
     * @brief 获取用户资料
     * @param userId 用户ID
     * @return 用户公开资料
     */
    @GetMapping("/{userId}")
    @RateLimit(qps = 20)
    @Operation(summary = "获取用户资料", description = "获取指定用户的公开资料")
    public Result<UserProfileVO> getUserProfile(@PathVariable Long userId) {
        UserProfileVO profile = userService.getUserProfile(userId);
        return Result.success("获取用户资料成功", profile);
    }

    /**
     * @brief 更新用户资料
     * @param dto    用户更新请求体
     * @param userId 当前用户ID
     * @return 操作结果
     */
    @PutMapping
    @RateLimit(qps = 5)
    @Operation(summary = "更新用户资料", description = "更新当前登录用户的个人资料")
    @PreAuthorize("isAuthenticated()")
    public Result<Void> updateUserProfile(@Validated @RequestBody UserUpdateDTO dto,
                                          @RequestAttribute("userId") Long userId) {
        userService.updateUserProfile(dto, userId);
        return Result.success("用户资料更新成功", null);
    }

    /**
     * @brief 更新用户角色（管理员）
     * @param userId 用户ID
     * @param role   新角色
     * @return 操作结果
     */
    @PutMapping("/{userId}/role")
    @RateLimit(qps = 3)
    @Operation(summary = "更新用户角色", description = "管理员更新指定用户的角色")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateUserRole(@PathVariable Long userId, @RequestParam String role) {
        userService.updateUserRole(userId, role);
        return Result.success("用户角色更新成功", null);
    }

    /**
     * @brief 更新用户状态（管理员）
     * @param userId 用户ID
     * @param status 新状态
     * @return 操作结果
     */
    @PutMapping("/{userId}/status")
    @RateLimit(qps = 3)
    @Operation(summary = "更新用户状态", description = "管理员启用/禁用指定用户账号")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateUserStatus(@PathVariable Long userId, @RequestParam Integer status) {
        userService.updateUserStatus(userId, status);
        return Result.success("用户状态更新成功", null);
    }

    /**
     * @brief 重置用户密码（管理员）
     * @param userId      用户ID
     * @param newPassword 新密码
     * @return 操作结果
     */
    @PutMapping("/{userId}/reset-password")
    @RateLimit(qps = 3)
    @Operation(summary = "重置用户密码", description = "管理员重置指定用户的密码")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> resetUserPassword(@PathVariable Long userId, @RequestParam String newPassword) {
        userService.resetUserPassword(userId, newPassword);
        return Result.success("密码重置成功", null);
    }
}
