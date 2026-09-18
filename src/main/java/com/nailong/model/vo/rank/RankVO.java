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
    
    private Long userId;  ///< 用户ID
    private String username;  ///< 用户名
    private String nickname;  ///< 用户昵称
    private String avatar;  ///< 用户头像
    private Long rank;  ///< 排名
    private Integer totalScore;  ///< 总积分
    private Integer solvedCount;  ///< 解题数量
    private String direction;  ///< 方向（前端/后端/算法等）
    private String grade;  ///< 年级
    private Long lastUpdateTime;  ///< 上次更新时间（用于实时排行榜）
    private Integer scoreChange;  ///< 积分变化趋势（正数表示上升，负数表示下降）
    private Integer rankChange;  ///< 排名变化趋势（正数表示上升，负数表示下降）
    private Boolean isCurrentUser;  ///< 是否为当前登录用户
}
