package com.nailong.service;

import com.nailong.model.dto.user.*;
import com.nailong.model.vo.user.LoginVO;

/**
 * @brief 认证服务接口
 * @author Nailong
 */
public interface IAuthService {
    
    /**
     * @brief 用户注册
     * @param dto 注册请求（含邮箱验证码）
     * @throws com.nailong.common.exception.BusinessException 用户名/邮箱已存在或验证码无效时抛出
     */
    void register(UserRegisterDTO dto);
    
    /**
     * @brief 用户登录
     * @param dto 登录请求（用户名/邮箱 + 密码）
     * @param ip  客户端 IP
     * @return 登录结果（含 Access/Refresh Token 与用户信息）
     * @details 支持用户名或邮箱登录；失败时抛出业务异常
     */
    LoginVO login(UserLoginDTO dto, String ip);
    
    /**
     * @brief 发送邮箱验证码
     * @param dto 邮箱与验证码类型（register/reset）
     */
    void sendEmailCode(EmailCodeDTO dto);
    
    /**
     * @brief 重置密码
     * @param dto 邮箱、验证码与新密码
     */
    void resetPassword(PasswordResetDTO dto);
    
    /**
     * @brief 刷新访问令牌
     * @param refreshToken 刷新令牌
     * @return 新的 Access/Refresh Token
     */
    LoginVO refreshToken(String refreshToken);
    
    /**
     * @brief 用户登出
     * @param token 当前 Access Token
     * @details 清除 Redis 中的 Token 记录
     */
    void logout(String token);
}
