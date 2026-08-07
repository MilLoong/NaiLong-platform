package com.nailong.service;

import com.nailong.model.vo.user.UserRankVO;

import java.util.List;

/**
 * @brief 排行榜服务接口
 * @author Nailong
 */
public interface IRankService {
    
    /**
     * @brief 获取总排行榜
     * @param limit 返回条数上限
     * @return 用户排名列表
     */
    List<UserRankVO> getRankList(Integer limit);
    
    /**
     * @brief 获取指定方向的排行榜
     * @param direction 方向标识
     * @param limit     返回条数上限
     * @return 用户排名列表
     */
    List<UserRankVO> getRankListByDirection(String direction, Integer limit);
    
    /**
     * @brief 获取用户排名
     * @param userId 用户 ID
     * @return 总榜排名（从 1 开始）
     */
    Long getUserRank(Long userId);
    
    /**
     * @brief 获取用户在指定方向的排名
     * @param userId    用户 ID
     * @param direction 方向标识
     * @return 方向榜排名（从 1 开始）
     */
    Long getUserRankByDirection(Long userId, String direction);
    
    /**
     * @brief 同步排行榜数据
     * @details 从 MySQL 同步用户积分到 Redis 有序集合
     */
    void syncRankData();
    
    /**
     * @brief 更新单个用户的排行榜数据
     * @param userId 用户 ID
     */
    void updateUserRank(Long userId);
}
