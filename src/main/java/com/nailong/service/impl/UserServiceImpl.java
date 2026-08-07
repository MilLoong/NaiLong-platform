package com.nailong.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nailong.common.exception.BusinessException;
import com.nailong.common.enums.ResultCode;
import com.nailong.common.utils.BeanUtil;
import com.nailong.common.utils.PasswordUtil;
import com.nailong.common.utils.RedisUtils;
import com.nailong.mapper.UserMapper;
import com.nailong.model.dto.user.UserUpdateDTO;
import com.nailong.model.entity.User;
import com.nailong.model.vo.user.UserInfoVO;
import com.nailong.model.vo.user.UserProfileVO;
import com.nailong.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @brief 用户服务实现
 * @details 提供用户信息查询、资料更新、角色/状态管理及积分统计
 * @author Nailong
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final UserMapper userMapper;
    private final RedisUtils redisUtils;

    @Autowired
    public UserServiceImpl(UserMapper userMapper, RedisUtils redisUtils) {
        this.userMapper = userMapper;
        this.redisUtils = redisUtils;
    }

    /**
     * @brief 获取当前用户基本信息
     * @param userId 用户 ID
     * @return 用户基本信息 VO
     */
    @Override
    public UserInfoVO getCurrentUserInfo(Long userId) {
        User user = getById(userId);
        if (user == null || user.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return BeanUtil.copyProperties(user, UserInfoVO.class);
    }

    /**
     * @brief 获取用户个人资料
     * @param userId 用户 ID
     * @return 用户资料 VO
     */
    @Override
    public UserProfileVO getUserProfile(Long userId) {
        User user = getById(userId);
        if (user == null || user.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return BeanUtil.copyProperties(user, UserProfileVO.class);
    }

    /**
     * @brief 更新用户个人资料
     * @param dto    更新请求
     * @param userId 当前用户 ID
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateUserProfile(UserUpdateDTO dto, Long userId) {
        User user = getById(userId);
        if (user == null || user.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        BeanUtil.copyPropertiesIgnoreFields(dto, user, "id");
        updateById(user);

        redisUtils.delete("user:info:" + userId);
    }

    /**
     * @brief 更新用户角色
     * @param userId 用户 ID
     * @param role   新角色
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateUserRole(Long userId, String role) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        user.setRole(role);
        updateById(user);
    }

    /**
     * @brief 更新用户状态
     * @param userId 用户 ID
     * @param status 状态（1 正常，0 禁用）
     * @details 禁用用户时清除其 Token，强制下线
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateUserStatus(Long userId, Integer status) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        user.setStatus(status);
        updateById(user);

        if (status == 0) {
            redisUtils.delete("user:token:" + userId);
        }
    }

    /**
     * @brief 重置用户密码
     * @param userId      用户 ID
     * @param newPassword 新密码（明文）
     * @details 更新密码后清除用户缓存与 Token
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void resetUserPassword(Long userId, String newPassword) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        String encryptedPassword = PasswordUtil.encode(newPassword);
        user.setPassword(encryptedPassword);
        updateById(user);

        redisUtils.delete("user:info:" + userId);
        redisUtils.delete("user:token:" + userId);
    }

    /**
     * @brief 更新用户积分
     * @param userId 用户 ID
     * @param score  增量积分（可为负）
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateUserScore(Long userId, Integer score) {
        int rows = userMapper.updateScore(userId, score);
        if (rows <= 0) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
    }

    /**
     * @brief 增加用户解题数量
     * @param userId 用户 ID
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateUserSolvedCount(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        user.setSolvedCount(user.getSolvedCount() + 1);
        updateById(user);
    }
}
