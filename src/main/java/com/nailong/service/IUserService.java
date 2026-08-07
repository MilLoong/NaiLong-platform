package com.nailong.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nailong.model.dto.user.UserUpdateDTO;
import com.nailong.model.entity.User;
import com.nailong.model.vo.user.UserInfoVO;
import com.nailong.model.vo.user.UserProfileVO;

/**
 * @brief 用户服务接口
 * @author Nailong
 */
public interface IUserService extends IService<User> {

    /**
     * @brief 获取当前登录用户信息
     * @param userId 用户 ID
     * @return 用户基本信息
     */
    UserInfoVO getCurrentUserInfo(Long userId);

    /**
     * @brief 获取用户资料
     * @param userId 用户 ID
     * @return 用户资料详情
     */
    UserProfileVO getUserProfile(Long userId);

    /**
     * @brief 更新用户资料
     * @param dto    资料更新请求
     * @param userId 用户 ID
     */
    void updateUserProfile(UserUpdateDTO dto, Long userId);

    /**
     * @brief 更新用户角色
     * @param userId 用户 ID
     * @param role   目标角色
     */
    void updateUserRole(Long userId, String role);

    /**
     * @brief 更新用户状态
     * @param userId 用户 ID
     * @param status 目标状态
     */
    void updateUserStatus(Long userId, Integer status);

    /**
     * @brief 重置用户密码
     * @param userId      用户 ID
     * @param newPassword 新密码
     */
    void resetUserPassword(Long userId, String newPassword);

    /**
     * @brief 更新用户总积分
     * @param userId 用户 ID
     * @param score  积分增量或目标值
     */
    void updateUserScore(Long userId, Integer score);

    /**
     * @brief 更新用户解题数量
     * @param userId 用户 ID
     * @details 根据提交记录重新统计已 AC 题目数
     */
    void updateUserSolvedCount(Long userId);
}
