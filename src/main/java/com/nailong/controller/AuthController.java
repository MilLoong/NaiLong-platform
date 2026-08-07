package com.nailong.controller;

import com.nailong.common.annotation.RateLimit;
import com.nailong.common.result.Result;
import com.nailong.common.utils.IpUtil;
import com.nailong.model.dto.user.*;
import com.nailong.model.vo.user.LoginVO;
import com.nailong.service.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @brief 认证控制器
 * @details 提供注册、登录、验证码、登出等 HTTP 接口
 * @author Nailong
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户注册、登录、登出等认证相关接口")
public class AuthController {
    
    private final IAuthService authService;
    
    /**
     * @brief 用户注册
     * @param dto 注册请求体
     * @return 操作结果
     */
    @PostMapping("/register")
    @RateLimit(qps = 5)
    @Operation(summary = "用户注册", description = "通过邮箱验证码注册新用户")
    public Result<Void> register(@Validated @RequestBody UserRegisterDTO dto) {
        authService.register(dto);
        return Result.success("注册成功", null);
    }
    
    /**
     * @brief 用户登录
     * @param dto     登录请求体
     * @param request HTTP 请求（用于提取客户端 IP）
     * @return 登录结果（Token 与用户信息）
     */
    @PostMapping("/login")
    @RateLimit(qps = 10)
    @Operation(summary = "用户登录", description = "使用用户名或邮箱登录")
    public Result<LoginVO> login(@Validated @RequestBody UserLoginDTO dto,
                                  HttpServletRequest request) {
        String ip = IpUtil.getIpAddress(request);
        LoginVO vo = authService.login(dto, ip);
        return Result.success("登录成功", vo);
    }
    
    /**
     * @brief 发送邮箱验证码
     * @param dto 邮箱与验证码类型
     * @return 操作结果
     */
    @PostMapping("/send-code")
    @RateLimit(qps = 1)
    @Operation(summary = "发送邮箱验证码", description = "发送注册或重置密码的验证码")
    public Result<Void> sendEmailCode(@Validated @RequestBody EmailCodeDTO dto) {
        authService.sendEmailCode(dto);
        return Result.success("验证码已发送，请查收邮件", null);
    }
    
    /**
     * @brief 重置密码
     * @param dto 邮箱、验证码与新密码
     * @return 操作结果
     */
    @PostMapping("/reset-password")
    @RateLimit(qps = 3)
    @Operation(summary = "重置密码", description = "通过邮箱验证码重置密码")
    public Result<Void> resetPassword(@Validated @RequestBody PasswordResetDTO dto) {
        authService.resetPassword(dto);
        return Result.success("密码重置成功，请重新登录", null);
    }
    
    /**
     * @brief 刷新 Token
     * @param refreshToken 刷新令牌
     * @return 新的 Token 信息
     */
    @PostMapping("/refresh")
    @RateLimit(qps = 5)
    @Operation(summary = "刷新Token", description = "使用RefreshToken刷新AccessToken")
    public Result<LoginVO> refreshToken(@RequestParam String refreshToken) {
        LoginVO vo = authService.refreshToken(refreshToken);
        return Result.success("Token刷新成功", vo);
    }
    
    /**
     * @brief 用户登出
     * @param authorization Authorization 请求头（Bearer Token）
     * @return 操作结果
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "清除Token，退出登录")
    public Result<Void> logout(@RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        authService.logout(token);
        return Result.success("登出成功", null);
    }
}
