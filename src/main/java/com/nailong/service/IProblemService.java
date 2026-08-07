package com.nailong.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nailong.model.dto.problem.*;
import com.nailong.model.vo.problem.*;

/**
 * @brief 题目服务接口
 * @author Nailong
 */
public interface IProblemService {
    
    /**
     * @brief 创建题目
     * @param dto       题目创建请求
     * @param creatorId 创建者用户 ID
     */
    void createProblem(ProblemCreateDTO dto, Long creatorId);
    
    /**
     * @brief 更新题目
     * @param dto 题目更新请求
     */
    void updateProblem(ProblemUpdateDTO dto);
    
    /**
     * @brief 删除题目
     * @param id 题目 ID
     */
    void deleteProblem(Long id);
    
    /**
     * @brief 获取题目详情
     * @param id     题目 ID
     * @param userId 当前用户 ID（用于判断 AC 状态等）
     * @return 题目详情
     */
    ProblemDetailVO getProblemDetail(Long id, Long userId);
    
    /**
     * @brief 分页查询题目列表
     * @param dto    查询条件
     * @param userId 当前用户 ID
     * @return 题目分页列表
     */
    Page<ProblemListVO> getProblemList(ProblemQueryDTO dto, Long userId);
    
    /**
     * @brief 计算衰减后的分数
     * @param baseScore   基础分数
     * @param solvedCount 已解决人数
     * @return 衰减后的分数
     * @details 根据已解决人数对基础分进行衰减，人数越多分值越低
     */
    Integer calculateDecayedScore(Integer baseScore, Integer solvedCount);
}
