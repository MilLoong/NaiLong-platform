package com.nailong.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nailong.model.dto.submission.SubmissionCountDTO;
import com.nailong.model.entity.Submission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @brief 提交记录Mapper接口
 * @author Nailong
 */
@Mapper
public interface SubmissionMapper extends BaseMapper<Submission> {
    
    /**
     * @brief 检查用户是否已解决该题目
     * @param userId    用户ID
     * @param problemId 题目ID
     * @return 已解决返回大于 0，否则返回 0
     */
    int isProblemSolvedByUser(@Param("userId") Long userId, @Param("problemId") Long problemId);
    
    /**
     * @brief 获取用户的提交记录
     * @param userId 用户ID
     * @param limit  返回条数上限
     * @return 提交记录列表
     */
    List<Submission> getUserSubmissions(@Param("userId") Long userId, @Param("limit") Integer limit);
    
    /**
     * @brief 获取题目的提交统计信息
     * @param problemId 题目ID
     * @return 提交统计数据
     */
    SubmissionCountDTO getProblemSubmissionStats(@Param("problemId") Long problemId);
}