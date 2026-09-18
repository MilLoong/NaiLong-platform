package com.nailong.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @brief 用户排名视图对象
 * @details 用于展示用户在排行榜中的相关信息
 * @author Nailong
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RankVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;  ///< 用户ID
    private String username;  ///< 用户名
    private String nickname;  ///< 用户昵称
    private String avatar;  ///< 用户头像
    private Integer solvedProblemsCount;  ///< 解决的问题数量
    private Integer totalSubmissionsCount;  ///< 总提交次数
    private Double passRate;  ///< 通过率（百分比）
    private Integer rank;  ///< 排名
    private Integer role;  ///< 用户角色（0：普通用户，1：管理员）
    private Long registerTime;  ///< 用户注册时间（毫秒时间戳）
    private Long lastLoginTime;  ///< 最后登录时间（毫秒时间戳）
    private Integer score;  ///< 用户积分
    private String level;  ///< 用户等级
    private Boolean isOnline;  ///< 是否在线
    private Integer consecutiveLoginDays;  ///< 连续登录天数
    /**
     * 创建排名视图对象的构建器
     * @return RankVO构建器
     */
    public static RankVOBuilder builder() {
        return new RankVOBuilder();
    }

    /**
     * 计算并设置通过率
     * 如果总提交次数为0，则通过率为0
     */
    public void calculatePassRate() {
        if (totalSubmissionsCount == null || totalSubmissionsCount == 0) {
            this.passRate = 0.0;
        } else if (solvedProblemsCount == null) {
            this.passRate = 0.0;
        } else {
            this.passRate = (double) solvedProblemsCount / totalSubmissionsCount * 100;
        }
    }

    /**
     * 排名视图对象构建器类
     */
    public static class RankVOBuilder {
        private Long userId;
        private String username;
        private String nickname;
        private String avatar;
        private Integer solvedProblemsCount;
        private Integer totalSubmissionsCount;
        private Double passRate;
        private Integer rank;
        private Integer role;
        private Long registerTime;
        private Long lastLoginTime;
        private Integer score;
        private String level;
        private Boolean isOnline;
        private Integer consecutiveLoginDays;

        public RankVOBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public RankVOBuilder username(String username) {
            this.username = username;
            return this;
        }

        public RankVOBuilder nickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        public RankVOBuilder avatar(String avatar) {
            this.avatar = avatar;
            return this;
        }

        public RankVOBuilder solvedProblemsCount(Integer solvedProblemsCount) {
            this.solvedProblemsCount = solvedProblemsCount;
            return this;
        }

        public RankVOBuilder totalSubmissionsCount(Integer totalSubmissionsCount) {
            this.totalSubmissionsCount = totalSubmissionsCount;
            return this;
        }

        public RankVOBuilder passRate(Double passRate) {
            this.passRate = passRate;
            return this;
        }

        public RankVOBuilder rank(Integer rank) {
            this.rank = rank;
            return this;
        }

        public RankVOBuilder role(Integer role) {
            this.role = role;
            return this;
        }

        public RankVOBuilder registerTime(Long registerTime) {
            this.registerTime = registerTime;
            return this;
        }

        public RankVOBuilder lastLoginTime(Long lastLoginTime) {
            this.lastLoginTime = lastLoginTime;
            return this;
        }

        public RankVOBuilder score(Integer score) {
            this.score = score;
            return this;
        }

        public RankVOBuilder level(String level) {
            this.level = level;
            return this;
        }

        public RankVOBuilder isOnline(Boolean isOnline) {
            this.isOnline = isOnline;
            return this;
        }

        public RankVOBuilder consecutiveLoginDays(Integer consecutiveLoginDays) {
            this.consecutiveLoginDays = consecutiveLoginDays;
            return this;
        }

        /**
         * 构建排名视图对象
         * @return RankVO实例
         */
        public RankVO build() {
            RankVO rankVO = new RankVO();
            rankVO.setUserId(userId);
            rankVO.setUsername(username);
            rankVO.setNickname(nickname);
            rankVO.setAvatar(avatar);
            rankVO.setSolvedProblemsCount(solvedProblemsCount);
            rankVO.setTotalSubmissionsCount(totalSubmissionsCount);
            
            // 如果没有设置通过率，则自动计算
            if (passRate == null && solvedProblemsCount != null && totalSubmissionsCount != null) {
                if (totalSubmissionsCount > 0) {
                    rankVO.setPassRate((double) solvedProblemsCount / totalSubmissionsCount * 100);
                } else {
                    rankVO.setPassRate(0.0);
                }
            } else {
                rankVO.setPassRate(passRate);
            }
            
            rankVO.setRank(rank);
            rankVO.setRole(role);
            rankVO.setRegisterTime(registerTime);
            rankVO.setLastLoginTime(lastLoginTime);
            rankVO.setScore(score);
            rankVO.setLevel(level);
            rankVO.setIsOnline(isOnline);
            rankVO.setConsecutiveLoginDays(consecutiveLoginDays);
            
            return rankVO;
        }
    }
}