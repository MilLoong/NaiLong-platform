package com.nailong.controller;

import com.nailong.common.annotation.RateLimit;
import com.nailong.common.result.Result;
import com.nailong.model.dto.problem.SubmitAnswerDTO;
import com.nailong.model.dto.submission.SubmissionCountDTO;
import com.nailong.model.vo.SubmitResultVO;
import com.nailong.model.vo.submission.SubmissionDetailVO;
import com.nailong.model.vo.submission.SubmissionListVO;
import com.nailong.service.ISubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * @brief 提交记录控制器
 * @details 提交记录相关接口
 * @author Nailong
 */
@RestController
@RequestMapping("/api/submission")
@Tag(name = "提交管理", description = "提交记录相关接口")
@RequiredArgsConstructor
public class SubmissionController {

    private final ISubmissionService submissionService;

    /**
     * @brief 提交答案
     * @param dto 提交答案请求体
     * @return 提交结果
     */
    @PostMapping
    @RateLimit(qps = 3)
    @Operation(summary = "提交答案", description = "提交题目答案")
    public Result<SubmitResultVO> submitAnswer(@Valid @RequestBody SubmitAnswerDTO dto) {
        SubmitResultVO result = submissionService.submitAnswer(dto);
        return Result.success("提交成功", result);
    }

    /**
     * @brief 获取用户的提交记录
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @return 提交记录列表
     */
    @GetMapping("/user")
    @Operation(summary = "获取用户提交记录", description = "获取当前用户的提交记录")
    public Result<List<SubmissionListVO>> getUserSubmissions(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        List<SubmissionListVO> submissionList = submissionService.getUserSubmissions(pageNum, pageSize);
        return Result.success("获取成功", submissionList);
    }

    /**
     * @brief 获取题目的提交统计
     * @param problemId 题目ID
     * @return 提交统计信息
     */
    @GetMapping("/problem/{problemId}/stats")
    @Operation(summary = "获取题目提交统计", description = "获取指定题目的提交统计信息")
    public Result<SubmissionCountDTO> getProblemSubmissionStats(@PathVariable Long problemId) {
        SubmissionCountDTO stats = submissionService.getProblemSubmissionStats(problemId);
        return Result.success("获取成功", stats);
    }

    /**
     * @brief 获取提交详情
     * @param submissionId 提交记录ID
     * @return 提交详情
     */
    @GetMapping("/{submissionId}")
    @Operation(summary = "获取提交详情", description = "获取指定提交记录的详细信息（本人或管理员）")
    public Result<SubmissionDetailVO> getSubmissionDetail(@PathVariable Long submissionId) {
        SubmissionDetailVO detail = submissionService.getSubmissionDetail(submissionId);
        return Result.success("获取成功", detail);
    }

    /**
     * @brief 重新判题（管理员）
     * @param submissionId 提交记录ID
     * @return 重新判题结果
     */
    @PostMapping("/{submissionId}/rejudge")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "重新判题", description = "重新判题，仅管理员可用")
    public Result<SubmitResultVO> rejudgeSubmission(@PathVariable Long submissionId) {
        SubmitResultVO result = submissionService.rejudgeSubmission(submissionId);
        return Result.success("重新判题成功", result);
    }
}
