package com.nailong.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nailong.model.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @brief 用户Mapper接口
 * @author Nailong
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * @brief 根据用户名或邮箱查询用户
     * @param account 用户名或邮箱
     * @return 用户实体，未找到则返回 null
     */
    @Select("SELECT * FROM user WHERE (username = #{account} OR email = #{account}) AND deleted = 0")
    User selectByUsernameOrEmail(@Param("account") String account);
    
    /**
     * @brief 更新用户积分与解题数量
     * @param userId 用户ID
     * @param score  增加的积分
     * @return 影响的行数
     */
    @Update("UPDATE user SET total_score = total_score + #{score}, " +
            "solved_count = solved_count + 1, " +
            "update_time = NOW() " +
            "WHERE id = #{userId} AND deleted = 0")
    int updateScore(@Param("userId") Long userId, @Param("score") Integer score);
    
    /**
     * @brief 获取全站排行榜数据
     * @param limit 返回条数上限
     * @return 按积分排序的用户列表
     */
    @Select("SELECT id, username, nickname, avatar, grade, direction, " +
            "total_score, solved_count " +
            "FROM user " +
            "WHERE deleted = 0 AND status = 1 " +
            "ORDER BY total_score DESC, solved_count DESC, id ASC " +
            "LIMIT #{limit}")
    List<User> selectRankList(@Param("limit") Integer limit);
    
    /**
     * @brief 根据招新方向获取排行榜
     * @param direction 招新方向
     * @param limit     返回条数上限
     * @return 指定方向的用户排行列表
     */
    @Select("SELECT id, username, nickname, avatar, grade, direction, " +
            "total_score, solved_count " +
            "FROM user " +
            "WHERE direction = #{direction} AND deleted = 0 AND status = 1 " +
            "ORDER BY total_score DESC, solved_count DESC, id ASC " +
            "LIMIT #{limit}")
    List<User> selectRankListByDirection(@Param("direction") String direction, 
                                          @Param("limit") Integer limit);
}