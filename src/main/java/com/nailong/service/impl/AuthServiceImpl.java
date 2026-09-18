package com.nailong.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nailong.common.constant.RedisKey;
import com.nailong.common.constant.SecurityConstant;
import com.nailong.common.enums.ResultCode;
import com.nailong.common.exception.BusinessException;
import com.nailong.common.utils.JwtUtil;
import com.nailong.common.utils.PasswordUtil;
import com.nailong.mapper.UserMapper;
import com.nailong.model.dto.user.*;
import com.nailong.model.entity.User;
import com.nailong.model.vo.user.LoginVO;
import com.nailong.model.vo.user.UserInfoVO;
import com.nailong.service.IAuthService;
import com.nailong.service.IEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * @brief 认证服务实现
 * @details 实现注册、登录锁定、验证码、Token 刷新与登出
 * @author Nailong
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final IEmailService emailService;
    private final StringRedisTemplate redisTemplate;

    @Value("${nailong.jwt.access-token-expire}")
    private Long accessTokenExpire;

    @Value("${nailong.jwt.refresh-token-expire}")
    private Long refreshTokenExpire;

    @Value("${nailong.code.expire}")
    private Long codeExpire;

    /**
     * @brief 用户注册
     * @param dto 注册请求（含邮箱验证码）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(UserRegisterDTO dto) {
        // 校验密码强度 -> 查重 username/email -> 校验邮箱验证码 -> 密码哈希落库 -> 删验证码

        // 校验密码强度
        if (!PasswordUtil.isStrongPassword(dto.getPassword())) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(),
                    "密码需包含大小写字母和数字，长度 8-20");
        }

        // username 查重
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS);
        }

        // email 查重
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, dto.getEmail());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.EMAIL_ALREADY_EXISTS);
        }

        // 校验邮箱验证码
        String cacheKey = RedisKey.EMAIL_CODE + "register:" + dto.getEmail();
        String cacheCode = redisTemplate.opsForValue().get(cacheKey);
        if (cacheCode == null || !cacheCode.equals(dto.getCode())) {
            throw new BusinessException(ResultCode.INVALID_VERIFICATION_CODE);
        }

        // 密码哈希落库
        User user = new User();
        BeanUtil.copyProperties(dto, user);
        user.setPassword(PasswordUtil.encode(dto.getPassword()));
        user.setRole(SecurityConstant.ROLE_USER);
        user.setTotalScore(0);
        user.setSolvedCount(0);
        user.setStatus(1);

        userMapper.insert(user);
        // 删验证码
        redisTemplate.delete(cacheKey);

        log.info("用户注册成功: username={}, email={}", dto.getUsername(), dto.getEmail());
    }

    /**
     * @brief 用户登录
     * @param dto 登录请求（用户名/邮箱 + 密码）
     * @param ip  客户端 IP
     * @return 登录结果（含 Access/Refresh Token 与用户信息）
     */
    @Override
    public LoginVO login(UserLoginDTO dto, String ip) {
        // 检查登录锁定 -> 按用户名/邮箱查用户 -> 验密码 -> 签发双 Token -> 写 Redis -> 填 LoginVO

        String account = dto.getUsername();
        // 检查登录锁定
        assertNotLocked(account);

        // 按用户名/邮箱查用户并验密码
        User user = userMapper.selectByUsernameOrEmail(account);
        if (user == null || !PasswordUtil.matches(dto.getPassword(), user.getPassword())) {
            recordLoginFailure(account, ip);
            throw new BusinessException(ResultCode.AUTHENTICATION_ERROR.getCode(),
                    SecurityConstant.LOGIN_FAIL_MSG);
        }

        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }

        clearLoginFailure(account);

        // 更新最后登录信息
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(ip);
        userMapper.updateById(user);

        // 签发双 Token 并写 Redis
        String accessToken = jwtUtil.generateAccessToken(
                user.getId(), user.getUsername(), user.getRole()
        );
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());
        storeTokens(user.getId(), accessToken, refreshToken);

        // 填 LoginVO
        LoginVO vo = new LoginVO();
        vo.setAccessToken(accessToken);
        vo.setRefreshToken(refreshToken);
        vo.setTokenType("Bearer");
        vo.setExpiresIn(accessTokenExpire);
        vo.setUserInfo(BeanUtil.copyProperties(user, UserInfoVO.class));

        log.info("用户登录成功: userId={}, username={}, ip={}",
                user.getId(), user.getUsername(), ip);
        return vo;
    }

    /**
     * @brief 发送邮箱验证码
     * @param dto 邮箱与验证码类型（register/reset）
     */
    @Override
    public void sendEmailCode(EmailCodeDTO dto) {
        String email = dto.getEmail();
        String type = dto.getType();
        if (!"register".equals(type) && !"reset".equals(type)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "验证码类型无效");
        }

        String cacheKey = RedisKey.EMAIL_CODE + type + ":" + email;
        String existCode = redisTemplate.opsForValue().get(cacheKey);
        if (existCode != null) {
            throw new BusinessException("验证码已发送，请稍后再试");
        }

        String code = RandomUtil.randomNumbers(6);
        emailService.sendVerificationCode(email, code, type);
        redisTemplate.opsForValue().set(cacheKey, code, codeExpire, TimeUnit.SECONDS);
        log.info("验证码发送成功: email={}, type={}", email, type);
    }

    /**
     * @brief 重置密码
     * @param dto 邮箱、验证码与新密码
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(PasswordResetDTO dto) {
        if (!PasswordUtil.isStrongPassword(dto.getNewPassword())) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(),
                    "密码需包含大小写字母和数字，长度 8-20");
        }

        String cacheKey = RedisKey.EMAIL_CODE + "reset:" + dto.getEmail();
        String cacheCode = redisTemplate.opsForValue().get(cacheKey);
        if (cacheCode == null || !cacheCode.equals(dto.getCode())) {
            throw new BusinessException(ResultCode.INVALID_VERIFICATION_CODE);
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, dto.getEmail());
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        user.setPassword(PasswordUtil.encode(dto.getNewPassword()));
        userMapper.updateById(user);

        redisTemplate.delete(cacheKey);
        clearUserTokens(user.getId());
        log.info("密码重置成功: userId={}, email={}", user.getId(), dto.getEmail());
    }

    /**
     * @brief 刷新访问令牌
     * @param refreshToken 刷新令牌
     * @return 新的 Access/Refresh Token
     */
    @Override
    public LoginVO refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        }

        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        String cachedRefresh = redisTemplate.opsForValue().get(RedisKey.USER_REFRESH_TOKEN + userId);
        if (cachedRefresh == null || !cachedRefresh.equals(refreshToken)) {
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }

        String newAccessToken = jwtUtil.generateAccessToken(
                user.getId(), user.getUsername(), user.getRole()
        );
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getId());
        storeTokens(user.getId(), newAccessToken, newRefreshToken);

        LoginVO vo = new LoginVO();
        vo.setAccessToken(newAccessToken);
        vo.setRefreshToken(newRefreshToken);
        vo.setTokenType("Bearer");
        vo.setExpiresIn(accessTokenExpire);
        return vo;
    }

    /**
     * @brief 用户登出
     * @param token 当前 Access Token
     */
    @Override
    public void logout(String token) {
        if (!jwtUtil.validateToken(token)) {
            return;
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        clearUserTokens(userId);
        log.info("用户登出成功: userId={}", userId);
    }

    private void storeTokens(Long userId, String accessToken, String refreshToken) {
        redisTemplate.opsForValue().set(
                RedisKey.USER_TOKEN + userId, accessToken, accessTokenExpire, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(
                RedisKey.USER_REFRESH_TOKEN + userId, refreshToken, refreshTokenExpire, TimeUnit.SECONDS);
    }

    private void clearUserTokens(Long userId) {
        redisTemplate.delete(RedisKey.USER_TOKEN + userId);
        redisTemplate.delete(RedisKey.USER_REFRESH_TOKEN + userId);
    }

    private void assertNotLocked(String account) {
        Boolean locked = redisTemplate.hasKey(SecurityConstant.REDIS_PREFIX_ACCOUNT_LOCK + account);
        if (Boolean.TRUE.equals(locked)) {
            throw new BusinessException(ResultCode.ACCOUNT_LOCKED.getCode(),
                    SecurityConstant.ACCOUNT_LOCKED_MSG);
        }
    }

    private void recordLoginFailure(String account, String ip) {
        String key = SecurityConstant.REDIS_PREFIX_LOGIN_ATTEMPTS + account;
        Long attempts = redisTemplate.opsForValue().increment(key);
        if (attempts != null && attempts == 1L) {
            redisTemplate.expire(key, SecurityConstant.ACCOUNT_LOCK_DURATION, TimeUnit.SECONDS);
        }
        if (attempts != null && attempts >= SecurityConstant.MAX_LOGIN_ATTEMPTS) {
            redisTemplate.opsForValue().set(
                    SecurityConstant.REDIS_PREFIX_ACCOUNT_LOCK + account,
                    ip == null ? "1" : ip,
                    SecurityConstant.ACCOUNT_LOCK_DURATION,
                    TimeUnit.SECONDS
            );
            redisTemplate.delete(key);
            log.warn("账号因登录失败次数过多被锁定: account={}, ip={}", account, ip);
        }
    }

    private void clearLoginFailure(String account) {
        redisTemplate.delete(SecurityConstant.REDIS_PREFIX_LOGIN_ATTEMPTS + account);
        redisTemplate.delete(SecurityConstant.REDIS_PREFIX_ACCOUNT_LOCK + account);
    }
}
