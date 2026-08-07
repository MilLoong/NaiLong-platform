package com.nailong.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nailong.model.entity.Problem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @brief 题目Mapper接口
 * @author Nailong
 */
@Mapper
public interface ProblemMapper extends BaseMapper<Problem> {
    
    /**
     * @brief 更新题目分数（分数衰减），使用乐观锁避免并发问题
     * @param problemId 题目ID
     * @param newScore  更新后的分数
     * @return 影响的行数
     */
    int updateScoreAndCount(@Param("problemId") Long problemId, 
                           @Param("newScore") Integer newScore);
    
    /**
     * @brief 增加题目提交次数
     * @param problemId 题目ID
     * @return 影响的行数
     */
    int increaseSubmitCount(@Param("problemId") Long problemId);
}