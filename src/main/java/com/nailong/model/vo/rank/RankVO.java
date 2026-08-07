package com.nailong.model.vo.rank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @brief 排行榜VO
 * @details 用于展示排行榜数据
 * @author Nailong
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankVO {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 用户昵称
     */
    private String nickname;
    
    /**
     * 用户头像
     */
    private String avatar;
    
    /**
     * 排名
     */
    private Long rank;
    
    /**
     * 总积分
     */
    private Integer totalScore;
    
    /**
     * 解题数量
     */
    private Integer solvedCount;
    
    /**
     * 方向（前端/后端/算法等）
     */
    private String direction;
    
    /**
     * 年级
     */
    private String grade;
    
    /**
     * 上次更新时间（用于实时排行榜）
     */
    private Long lastUpdateTime;
    
    /**
     * 积分变化趋势（正数表示上升，负数表示下降）
     */
    private Integer scoreChange;
    
    /**
     * 排名变化趋势（正数表示上升，负数表示下降）
     */
    private Integer rankChange;
    
    /**
     * 是否为当前登录用户
     */
    private Boolean isCurrentUser;
}
