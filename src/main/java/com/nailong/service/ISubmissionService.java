package com.nailong.service;

import com.nailong.model.dto.problem.SubmitAnswerDTO;
import com.nailong.model.dto.submission.SubmissionCountDTO;
import com.nailong.model.vo.SubmitResultVO;
import com.nailong.model.vo.submission.SubmissionDetailVO;
import com.nailong.model.vo.submission.SubmissionListVO;

import java.util.List;

/**
 * @brief 提交服务接口
 * @author Nailong
 */
public interface ISubmissionService {
    
    /**
     * @brief 提交答案（从 ProblemController 调用）
     * @param dto    提交请求
     * @param userId 用户 ID
     * @param ip     客户端 IP
     */
    void submitAnswer(SubmitAnswerDTO dto, Long userId, String ip);
    
    /**
     * @brief 提交答案（从 SubmissionController 调用）
     * @param dto 提交请求
     * @return 判题结果
     */
    SubmitResultVO submitAnswer(SubmitAnswerDTO dto);
    
    /**
     * @brief 获取用户的提交记录
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @return 提交记录列表
     */
    List<SubmissionListVO> getUserSubmissions(Integer pageNum, Integer pageSize);
    
    /**
     * @brief 获取题目的提交统计
     * @param problemId 题目 ID
     * @return 提交次数、通过率等统计数据
     */
    SubmissionCountDTO getProblemSubmissionStats(Long problemId);
    
    /**
     * @brief 获取提交详情
     * @param submissionId 提交 ID
     * @return 提交详情
     */
    SubmissionDetailVO getSubmissionDetail(Long submissionId);
    
    /**
     * @brief 重新判题
     * @param submissionId 提交 ID
     * @return 重新判题结果
     */
    SubmitResultVO rejudgeSubmission(Long submissionId);
}
